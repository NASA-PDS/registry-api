locals {

  # Concatenate the load balancer domain to spring boot args
  spring_boot_args_with_host = "${var.spring_boot_args} --server.authorizedForwardedHost=${aws_lb.registry-api-lb.dns_name},${var.cloudfront_dns}"
}

resource "aws_lb" "registry-api-lb" {
  name               = "registry-api-lb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = var.aws_fg_security_groups
  subnets            = var.aws_lb_subnets

  enable_deletion_protection = false

  access_logs {
    bucket  = var.aws_s3_bucket_logs_id
    prefix  = "registry/registry-api-lb"
    enabled = true
  }

  tags = var.common_tags
}



resource "aws_lb_target_group" "pds-registry-api-target-group" {
  name        = "pds-registry-tg"
  port        = 80
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = var.aws_fg_vpc

  lifecycle {
    create_before_destroy = true
  }

  health_check {
    enabled = true
    path    = "/health"
    matcher = "200"
    interval = 300
  }

  tags = var.common_tags
}

resource "aws_lb_listener" "registry-api-ld-listener" {
  load_balancer_arn = aws_lb.registry-api-lb.arn
  port              = 443
  protocol          = "HTTPS"
  certificate_arn   = var.aws_acm_certificate_arn
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.pds-registry-api-target-group.arn
  }
  tags = var.common_tags
}

resource "aws_lb_listener_rule" "pds-registry-forward-rule" {
  listener_arn = aws_lb_listener.registry-api-ld-listener.arn

  action {
    type             = "forward"
    target_group_arn =  aws_lb_target_group.pds-registry-api-target-group.arn
  }

  # no condition for now
  # TODO add condition so that the same load balancer can be
  # used for multiple back-end service
  condition {
    path_pattern {
      values           = ["/*"]
    }
  }
}


# Credentials for ECR pull through cache from GHCR
resource "aws_secretsmanager_secret" "github_ecr_credentials" {
  count = var.create_github_secret_credentials

  name = "ecr-pullthroughcache/github-credentials"
  tags = var.common_tags
}

resource "aws_secretsmanager_secret_version" "github_ecr_credentials" {
  count = var.create_github_secret_credentials

  secret_id     = aws_secretsmanager_secret.github_ecr_credentials[count.index].id
  secret_string = jsonencode({
    username = var.github_username
    accessToken    = var.github_token
  })
}

# Look up the secret when it is not created by this script
data "aws_secretsmanager_secret" "github_ecr_credentials" {
  count = 1 - var.create_github_secret_credentials
  name  = "ecr-pullthroughcache/github-credentials"
}

locals {
  github_ecr_credentials_arn = var.create_github_secret_credentials == 1 ? aws_secretsmanager_secret.github_ecr_credentials[0].arn : data.aws_secretsmanager_secret.github_ecr_credentials[0].arn
}

# Add a Pull Through Cache rule for GHCR
resource "aws_ecr_pull_through_cache_rule" "ghcr" {
  ecr_repository_prefix = "ghcr"
  upstream_registry_url = "ghcr.io"
  credential_arn        = local.github_ecr_credentials_arn
}

resource "aws_ecr_repository" "ghcr_registry_api" {
    name = "ghcr/nasa-pds/registry-api"
    tags = var.common_tags
}

# Log groups hold logs from our app.
resource "aws_cloudwatch_log_group" "pds-registry-log-group" {
  name = "/ecs/pds-registry-api-task"

  tags = var.common_tags
}


# The task definition for app.
resource "aws_ecs_task_definition" "pds-registry-ecs-task" {
  family = "pds-registry-api-task"

  container_definitions = <<EOF
  [
    {
      "name": "registry-api-container",
      "image": "${var.registry_api_docker_image}",
      "portMappings": [
        {
          "containerPort": 80
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-region": "${var.aws_region}",
          "awslogs-group": "${aws_cloudwatch_log_group.pds-registry-log-group.name}",
          "awslogs-stream-prefix": "ecs"
        }
      },
      "healthCheck" : {
        "retries": 3,
        "command": [
          "CMD-SHELL",
          "date || exit 1"
        ],
        "timeout": 5,
        "interval": 60,
        "startPeriod": 300
      },
      "environment": [
        {"name": "SERVER_PORT", "value": "80"},
        {"name": "SPRING_BOOT_APP_ARGS", "value": "${local.spring_boot_args_with_host}"}
      ]
    }
  ]

EOF

  execution_role_arn = var.ecs_task_execution_role
  task_role_arn      = var.ecs_task_role

  # These are the minimum values for Fargate containers.
  cpu                      = var.aws_fg_cpu_units
  memory                   = var.aws_fg_ram_units
  requires_compatibilities = ["FARGATE"]

  # This is required for Fargate containers
  network_mode = "awsvpc"

  tags = var.common_tags
}

# Define the cluster
resource "aws_ecs_cluster" "pds-registry-api-ecs" {
  name = "pds-registry-api-cluster"

  tags = var.common_tags
}


# The main service.
resource "aws_ecs_service" "pds-registry-reg-service" {
  name            = "pds-registry-api-service"
  task_definition = aws_ecs_task_definition.pds-registry-ecs-task.arn
  cluster         = aws_ecs_cluster.pds-registry-api-ecs.id
  launch_type     = "FARGATE"

  desired_count = 1

  load_balancer {
    target_group_arn = aws_lb_target_group.pds-registry-api-target-group.arn
    container_name   = "registry-api-container"
    container_port   = "80"
  }

  network_configuration {
    assign_public_ip = false
    security_groups = var.aws_fg_security_groups
    subnets = var.aws_fg_subnets
  }

  tags = var.common_tags

  depends_on = [aws_ecr_repository.ghcr_registry_api, aws_ecr_pull_through_cache_rule.ghcr]
}


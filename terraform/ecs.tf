resource "aws_lb" "registry-api-lb" {
  name               = "registry-api-lb-new"
  internal           = false
  load_balancer_type = "application"
  security_groups    = var.aws_fg_security_groups
  subnets            = var.aws_lb_subnets

  enable_deletion_protection = false

  access_logs {
    bucket  = var.aws_s3_bucket_logs_id
    prefix  = "registry-api-lb"
    enabled = true
  }

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}

resource "aws_lb_target_group" "pds-registry-api-target-group" {
  name        = "pds-${var.venue}-registry-tgt"
  port        = 80
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = var.aws_fg_vpc

  health_check {
    enabled = true
    path    = "/healthcheck"
    matcher = "200"
    interval = 300
  }
}

resource "aws_lb_listener" "registry-api-ld-listener" {
  load_balancer_arn = aws_lb.registry-api-lb.arn
  port              = 80
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.pds-registry-api-target-group.arn
  }
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

# Define the cluster
resource "aws_ecs_cluster" "pds-registry-api-ecs" {
  name = "pds-${var.venue}-registry-api-ecs"

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}

# Do we need individual dev/test/prod repositories?
# I don't think we do, but then we need to use prod account instead of the dev account, would that work ?
data "aws_ecr_repository" "pds-registry-api-service" {
  name = "pds-registry-api-service"
}

# Log groups hold logs from our app.
resource "aws_cloudwatch_log_group" "pds-registry-log-group" {
  name = "/ecs/pds-${var.venue}-registry-api-svc-task"

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}


# The task definition for app.
resource "aws_ecs_task_definition" "pds-registry-ecs-task" {
  family = "pds-${var.venue}-registry-api-svc-task"

  container_definitions = <<EOF
  [
    {
      "name": "pds-${var.venue}-reg-container",
      "image": "${var.aws_fg_image}",
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
        {"name": "SPRING_BOOT_APP_ARGS", "value": "${var.spring_boot_args}"}
      ]
    }
  ]

EOF

  execution_role_arn = var.ecs_task_execution_role
  task_role_arn      = var.ecs_task_role

  # These are the minimum values for Fargate containers.
  cpu                      = 256
  memory                   = 512
  requires_compatibilities = ["FARGATE"]

  # This is required for Fargate containers
  network_mode = "awsvpc"

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}



# The main service.
resource "aws_ecs_service" "pds-registry-reg-service" {
  name            = "pds-${var.venue}-registry-api-service"
  task_definition = aws_ecs_task_definition.pds-registry-ecs-task.arn
  cluster         = aws_ecs_cluster.pds-registry-api-ecs.id
  launch_type     = "FARGATE"

  desired_count = 1

  load_balancer {
    target_group_arn = aws_lb_target_group.pds-registry-api-target-group.arn
    container_name   = "pds-${var.venue}-reg-container"
    container_port   = "80"
  }

  network_configuration {
    assign_public_ip = false
    security_groups = var.aws_fg_security_groups
    subnets = var.aws_fg_subnets
  }

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}


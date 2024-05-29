# Define the cluster
resource "aws_ecs_cluster" "ecs_cluster" {
  name = "pds-${var.node_name_abbr}-${var.venue}-reg-cluster"

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}

# Do we need individual dev/test/prod repositories?
data "aws_ecr_repository" "pds-registry-api-service" {
  name = "pds-registry-api-service"
}

# Log groups hold logs from our app.
resource "aws_cloudwatch_log_group" "pds-registry-log-group" {
  name = "/ecs/pds-${var.node_name_abbr}-${var.venue}-reg-api-svc-task"

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}

# The main service.
resource "aws_ecs_service" "pds-registry-reg-service" {
  name            = "pds-${var.node_name_abbr}-${var.venue}-reg-service"
  task_definition = aws_ecs_task_definition.pds-registry-ecs-task.arn
  cluster         = aws_ecs_cluster.ecs_cluster.id
  launch_type     = "FARGATE"

  desired_count = 1

  load_balancer {
    target_group_arn = aws_lb_target_group.pds-registry-target-group.arn
    container_name   = "pds-${var.node_name_abbr}-${var.venue}-reg-container"
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

# The task definition for app.
resource "aws_ecs_task_definition" "pds-registry-ecs-task" {
  family = "pds-${var.node_name_abbr}-${var.venue}-reg-api-svc-task"

  container_definitions = <<EOF
  [
    {
      "name": "pds-${var.node_name_abbr}-${var.venue}-reg-container",
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
        {"name": "SPRING_BOOT_APP_ARGS": "--openSearch.host=p5qmxrldysl1gy759hqf.us-west-2.aoss.amazonaws.com --openSearch.CCSEnabled=true --openSearch.username="" --openSearch.disciplineNodes=atm-delta --registry.service.version=1.5.0-SNAPSHOT"}
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

# role under which ECS will execute tasks.
data "aws_iam_role" "pds-task-execution-role" {
  name    = "am-ecs-task-execution"
}

resource "aws_lb_target_group" "pds-registry-target-group" {
  name        = "pds-${var.node_name_abbr}-${var.venue}-reg-tgt"
  port        = 80
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = var.aws_fg_vpc

  health_check {
    enabled = true
    path    = "/healthcheck"
    matcher = "200"
    interval = 60
  }
}

resource "aws_lb_listener_rule" "pds-registry-forward-rule" {
  listener_arn = var.aws_lb_listener_arn

  action {
    type             = "forward"
    target_group_arn =  aws_lb_target_group.pds-registry-target-group.arn
  }

  condition {
    http_header {
      http_header_name = var.http_header_forward_name
      values           = [var.http_header_forward_value,]
    }
  }
}

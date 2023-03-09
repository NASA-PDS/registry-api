# Define the cluster
resource "aws_ecs_cluster" "ecs_cluster" {
  name = "pds-${var.node_name_abbr}-${var.venue}-provenance"

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}

# ECR
data "aws_ecr_repository" "pds-registry-api-service" {
  name = var.aws_ecr_repository
}

# Log groups hold logs from our app.
resource "aws_cloudwatch_log_group" "pds-prov-log-group" {
  name = "/ecs/pds-${var.node_name_abbr}-${var.venue}-provenance-task"

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}

# The main service.
resource "aws_ecs_service" "pds-provenance-service" {
  name            = "pds-${var.node_name_abbr}-${var.venue}-prov-service"
  task_definition = aws_ecs_task_definition.pds-prov-ecs-task.arn
  cluster         = aws_ecs_cluster.ecs_cluster.id
  launch_type     = "FARGATE"

  desired_count = 0

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
resource "aws_ecs_task_definition" "pds-prov-ecs-task" {
  family = "pds-${var.node_name_abbr}-${var.venue}-prov-task"

  container_definitions = <<EOF
  [
    {
      "name": "pds-${var.node_name_abbr}-${var.venue}-prov-container",
      "image": "${var.aws_fg_image}",
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-region": "${var.aws_region}",
          "awslogs-group": "${aws_cloudwatch_log_group.pds-prov-log-group.name}",
          "awslogs-stream-prefix": "prov"
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
      "secrets": [
        {"name": "PROV_CREDENTIALS", "valueFrom": "${data.aws_secretsmanager_secret.es_login_secret.arn}"},
        {"name": "PROV_ENDPOINT", "valueFrom": "${aws_ssm_parameter.prov_endpoint_parameter.name}"},
        {"name": "PROV_REMOTES", "valueFrom": "${aws_ssm_parameter.prov_remotes_parameter.name}"}
      ]
    }
  ]

EOF

  execution_role_arn = data.aws_iam_role.pds-task-execution-role.arn
  task_role_arn      = data.aws_iam_role.pds-task-execution-role.arn

  # provenance is somewhat memory intenstive, hence asking for 56GB
  # memory which corresponds to 8,192 CPU units (8 vCPU)
  cpu                      = 8192
  memory                   = 57344
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

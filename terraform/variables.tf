variable "aws_account_id" {
  description = "AWS Account Id"
  default = "445837347542"
}

variable "node_name_abbr" {
  description = "Node name abbreviation"
}

variable "venue" {
  description = "Deployment venue (prod, test, dev)"
  default = "dev"
}

variable "aws_region" {
  description = "AWS Region"
  default = "us-west-2"
}

variable "aws_shared_credentials_file" {
  description = "AWS shared credentials file"
  default = "~/.aws/credentials"
}

variable "aws_profile" {
  description = "AWS profile"
  default = "default"
}

variable "aws_fg_vpc" {
  description = "AWS VPC for Fargate"
  # default = "vpc-00a46a06a0f4139b7"
}

variable "aws_fg_security_groups" {
  description = "AWS Security groups for Fargate"
  type = list(string)
  # default = ["sg-0ec8931299e5949a2"]
}

variable "aws_fg_subnets" {
  description = "AWS Subnets for Fargate"
  type = list(string)
  # default = ["subnet-005cbaf96a20adb30", "subnet-070c009607513d587"]
}

variable "es_user_name" {
  description = "User name for elastic search"
}

variable "es_password" {
  description = "Password for elastic search"
}

variable "es_hosts" {
  description = "comma separated list of ES hosts"
}

# This cannot be specified as a variable
# variable "aws_task_exec_role_name" {
  # description = "task execution role" 
  # default = "arn:aws:iam::445837347542:role/am-ecs-task-execution"
  # default = "ecs-task-execution"
# }

variable "aws_fg_image" {
  description = "AWS image name for Fargate"
  # default = "445837347542.dkr.ecr.us-west-2.amazonaws.com/pds-registry-api-service:0.5.0-SNAPSHOT.http"
}

variable "aws_lb_listener_arn" {
  description = "ARN of the AWS LB listener to associated with the service target group"
  default = "arn:aws:elasticloadbalancing:us-west-2:445837347542:listener/app/pds-en-ecs/7870b4ad486ca87b/085568f01bd7a139"
}

variable "http_header_forward_name" {
  description = "Name of the http header for which requests are forwarded to this service's target group"
  default = "x-request-node"
}

variable "http_header_forward_value" {
  description = "Value of the http_header for which requests are forwarded to this service's target group"
  # except for production, the venue is part of the x-request-node value
  # default = ${var.node_name_abbr}-${var.venue}"
}

variable "aws_fg_cpu_units" {
  description = "CPU Units for fargate"
  default = 256
}

variable "aws_fg_ram_units" {
  description = "RAM Units for Fargate"
  default = 512
}

variable "aws_ecr_pull_policy_arn" {
  description = "ARN for the ECR pull policy"
  default = "arn:aws:iam::aws:policy/service-role/AWSAppRunnerServicePolicyForECRAccess"
}

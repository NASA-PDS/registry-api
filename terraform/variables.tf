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

# This cannot be specified as a variable
# variable "aws_task_exec_role_name" {
  # description = "task execution role" 
  # default = "arn:aws:iam::445837347542:role/am-ecs-task-execution"
  # default = "ecs-task-execution"
# }

variable "aws_fg_image" {
  description = "AWS image name for Fargate"
  default = "445837347542.dkr.ecr.us-west-2.amazonaws.com/pds-registry-api-service:0.4.0-SNAPSHOT.http"
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

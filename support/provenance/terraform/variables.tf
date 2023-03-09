variable "aws_account_id" {
  description = "AWS Account Id"
  default = "445837347542"
}

variable "node_name_abbr" {
  description = "Node name abbreviation"
  default = "en"
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
}

variable "aws_fg_security_groups" {
  description = "AWS Security groups for Fargate"
  type = list(string)
}

variable "aws_fg_subnets" {
  description = "AWS Subnets for Fargate"
  type = list(string)
}

variable "prov_endpoint" {
  description = "Opensearch endpoint (full URL) which to connect."
}

variable "prov_remote_lists" {
  description = "JSON list containing strings of space separated ES remotes"
}

variable "aws_ecr_repository" {
  description = "Name of the AWS ECR Repository"
  default = "pds-registry-api-service"
}

variable "aws_fg_image" {
  description = "AWS image name for Fargate"
}

variable "aws_ecr_pull_policy_arn" {
  description = "ARN for the ECR pull policy"
  default = "arn:aws:iam::aws:policy/service-role/AWSAppRunnerServicePolicyForECRAccess"
}

variable "aws_eventbridge_role_arn" {
  description = "Role for eventbridge execution"
}

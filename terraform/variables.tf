variable "node_name_abbr" {
  description = "Node name abbreviation"
  default="en"
}

variable "aws_region" {
  description = "AWS Region"
  default = "us-west-2"
}

variable "spring_boot_args" {
  description = "Spring Boot server arguments"
}

variable "aws_profile" {
  description = "AWS profile"
  default = ""
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

variable "aws_lb_subnets" {
  description = "AWS Subnets for the load balancer"
  type = list(string)
}

variable "ecs_task_role" {
  description = "ECS task role"
}

variable "ecs_task_execution_role" {
  description = "ECS task execution role"
}

variable "registry_api_docker_image" {
  description = "AWS image name for Fargate"
}

variable "aws_s3_bucket_logs_id" {
  description = "AWS S3 bucket with the logs"
}

variable "aws_fg_cpu_units" {
  description = "CPU Units for fargate"
  default = 256
}

variable "aws_fg_ram_units" {
  description = "RAM Units for Fargate"
  default = 512
}

variable "aws_acm_certificate_arn" {
  description = "ACM SSL Certificate for the load balancer"
}

variable "component_name" {
    description = "Component this subcomponents belongs to"
    type        = string
    default = "registry"
}

variable "common_tags" {
  description = "Common tags to apply to all resources"
  type        = map(string)
  default = {
    Project     = "registry"
    ManagedBy   = "terraform"
  }
}

variable "github_username" {
  description = "GitHub username for ECR pull through cache"
}

variable "github_token" {
  description = "GitHub personal access token for ECR pull through cache"
  sensitive   = true
}

variable "create_github_secret_credentials" {
  description = "Set to 1 to create the GitHub credentials secret in Secrets Manager (and wire it to the ECR pull through cache rule). Set to 0 to skip creation, e.g. when the secret already exists or is managed elsewhere."
  type        = number
  default     = 1
}
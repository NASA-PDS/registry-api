variable "node_name_abbr" {
  description = "Node name abbreviation"
  default="en"
}

variable "venue" {
  description = "Deployment venue (prod, test, dev)"
  default = "delta"
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

variable "ecs_task_role" {
  description = "ECS task role"
}

variable "ecs_task_execution_role" {
  description = "ECS task execution role"
}

variable "aws_fg_image" {
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

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

variable "tenant" {
  description = "Tenant identifier (e.g., en, ge, stac)"
  default     = "en"
}

variable "venue" {
  type        = string
  description = "Deployment venue/environment identifier (e.g., pds-cds-dev, pds-cds-prod)"
}

variable "cicd"  {
  description = "CICD identifier tag value (e.g., iac, jenkins, github-actions)"
  type        = string
  default     = "iac"
}

# Component Configuration
variable "component_name" {
  type        = string
  description = "Component/repository name used for SSM parameter prefixes"
  default     = "registry"
}


variable "managedby"  {
    description = "Tag value for owner managing the resource (E.g. for PDS Team we have PDS Team Email Distro)"
    type        = string
    default     = "nasa-pds/pds-cds-infra"
}
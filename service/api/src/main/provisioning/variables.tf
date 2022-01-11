variable "node_name_abbr" {
  description = "Abbrieviated node name"
}

variable "provider_aws_version" {
  description = "AWS Terraform provider version"
  default = "~> 3.0"
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

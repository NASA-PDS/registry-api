
terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = var.provider_aws_version
    }
  }
}

#
# AWS
#
provider "aws" {
  region = var.aws_region
  shared_credentials_file = var.aws_shared_credentials_file
  profile = var.aws_profile
}


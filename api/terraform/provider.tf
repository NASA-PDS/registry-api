provider "aws" {
  version = "~> 3.0"
  region  = var.aws_region
  profile = var.aws_profile
}

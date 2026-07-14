provider "aws" {
  region  = var.aws_region
  profile = var.aws_profile
}

terraform {
  # variables are not supported for the bucket details. Instead, we declare an empty block and specify the details on the terraform init command line:
  #
  # terraform init -backend-config="bucket=${TFSTATE_BUCKET}" -backend-config="key=${TFSTATE_KEY}" -backend-config="region=${TFSTATE_REGION}"
  #
  # using environment variables (as shown) or explicit values.
  # See https://stackoverflow.com/questions/63048738/how-to-declare-variables-for-s3-backend-in-terraform
  #
  required_version = ">= 1.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.54.0"
    }
  }
}

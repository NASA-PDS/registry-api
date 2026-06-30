# Backend configuration for S3 state storage
# Variables are not supported in backend blocks. Instead, provide configuration via:
#
# 1. backend-config.tfvars file:
#    terraform init -backend-config=backend-config.tfvars
#
# 2. Command line arguments:
#    terraform init -backend-config="bucket=${TFSTATE_BUCKET}" -backend-config="key=${TFSTATE_KEY}"
#
# 3. Environment variables or interactive prompts
#
# See https://stackoverflow.com/questions/63048738/how-to-declare-variables-for-s3-backend-in-terraform

terraform {
  backend "s3" {
    # Backend configuration values provided via backend-config.tfvars
    # Example backend-config.tfvars content:
    #   bucket         = "pds-infra"
    #   key            = "registry/opensearch/terraform.tfstate"
    #   region         = "us-east-1"
    #   dynamodb_table = "terraform-state-lock"
    #   encrypt        = true
    #   profile        = "your-aws-profile"
  }
}
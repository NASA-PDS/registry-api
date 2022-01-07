# elastic search login secret
#
# Note that if this secret is deleted as part of a terraform destroy, it needs to be immediately deleted manually since
# by default secrets are only 'scheduled for deletion' in 7 days preventing it from being recreated in subsequent
# terraform apply's. To force the deletion of the secret:
#
#    aws secretsmanager delete-secret --secret-id <id> --force-delete-without-recovery --region <region>
#
resource aws_secretsmanager_secret "es_login_secret" {
  name = "pds/${var.node_name_abbr}/${var.venue}/registry/es/login"

  tags = {
    Alpha = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}

resource aws_secretsmanager_secret_version "es_login_secret" {
  secret_id = aws_secretsmanager_secret.es_login_secret.id

  secret_string = <<EOF
    {
      "${var.es_user_name}": "${var.es_password}"
    }
EOF
} 

# Store the list of es hosts as a parameter to be injected into the container as an environment variable
resource "aws_ssm_parameter" "es_hosts_parameter" {
  name = "/pds/${var.node_name_abbr}/${var.venue}/registry/es/hosts"
  type = "String"
  value = var.es_hosts

  tags = {
    Alpha = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}

# Store the node name as a parameter to be injected into the container as an environment variable
resource "aws_ssm_parameter" "node_name_parameter" {
  name = "/pds/${var.node_name_abbr}/${var.venue}/registry/node_name"
  type = "String"
  value = var.node_name_abbr

  tags = {
    Alpha = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry"
  }
}

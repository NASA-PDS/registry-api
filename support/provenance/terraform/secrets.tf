# opensearch login secret
# It is assumed this secret is created by the registry terraform
#
data "aws_secretsmanager_secret" "es_login_secret" {
  name = "pds/${var.node_name_abbr}/${var.venue}/registry/es/login"
}

# opensearch endpoint parameter
#
resource "aws_ssm_parameter" "prov_endpoint_parameter" {
  name = "/pds/${var.node_name_abbr}/${var.venue}/registry/prov/endpoint"
  type = "String"
  value = var.prov_endpoint

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry-provenance"
  }
}

# list of remotes to process, as a json list of space separate lists
# e.g. '[ "sbnpsi-prod-ccs sbnumd-prod-ccs ppi-prod-ccs",
#         "naif-prod-ccs geo-prod-ccs rms-prod",
#         "atm-prod-ccs img-prod-ccs psa-prod" ]
# Note that '-ccs' is missing from rms-prod and psa-prod
#
# Remotes are organized into sets that are processed in one
# execution of the provenance script.
# 
resource "aws_ssm_parameter" "prov_remotes_parameter" {
  name = "/pds/${var.node_name_abbr}/${var.venue}/provenance/remote_lists"
  type = "String"
  value = var.prov_remote_lists

  tags = {
    Alfa = var.node_name_abbr
    Bravo = var.venue
    Charlie = "registry-provenance"
  }
}

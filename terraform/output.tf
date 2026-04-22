locals {

  module_relative_path = replace(abspath(path.module), "/^.*\\/terraform\\//", "")
  ssm_prefix = "/pds/${var.component_name}/${local.module_relative_path}"
}


resource "aws_ssm_parameter" "load_balancer_domain" {
  name  = "${ssm_prefix}/api-load-balancer-domain"
  type  = "String"
  overwrite   = true
  value = aws_lb.registry-api-lb.dns_name
}
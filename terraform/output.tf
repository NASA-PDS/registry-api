locals {

  module_relative_path = replace(abspath(path.module), "/^.*\\/terraform(\\/|$)/", "")
  ssm_prefix = "/pds/${var.component_name}${local.module_relative_path}"
}


output "load_balancer_domain" {
  description = "Registry API load balancer domain"
  value = aws_lb.registry-api-lb.dns_name
}

resource "aws_ssm_parameter" "load_balancer_domain" {
  name  = "${local.ssm_prefix}/api-load-balancer-domain"
  description = "Registry API load balancer domain"
  type  = "String"
  value = aws_lb.registry-api-lb.dns_name
  tags = var.common_tags
}
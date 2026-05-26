# Terraform scripts for Registry-API

## Requirements

Install Terraform:

    brew install terraform


## Interfaces of the registry API

To run, the registry API needs to be interfaced with external components which are not deployed with the terraform scripts here:

- a **VPC** shared with the OpenSearch service
- **subnets**
- an EC2 **load balancer** and a **listener**
- a **security group** for the ECS cluster authorizing inbound rules from the load - - balancer on port 80. Outbound to anywhere.
- a **docker image** on ECR
- an **Opensearch** service containing indices registry, registry-refs, possibly prefixed per discipline, e.g. atm-registry, atm-registry-refs, geo-registry,...


These interfaces are going to be used a arguments of the terraform scripts.


## Deploy

Initialize the parameters, starting from the terraform.tfvars.example file provided.

Copy it:

    cp terraform.tfvars.example terraform.tfvars

And update the values.


Run the terraform scripts:




```
    terraform init -backend-config=backend-config.tfvars
    terraform plan
    terraform apply
```

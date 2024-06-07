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


Run the terraform scripts:

```
    terraform apply \
        -var 'ecs_task_role=your-task-role-arn' \
        -var 'ecs_task_execution_role=your-task-execution-role-arn' \
        -var 'venue=your-venue' \
        -var 'aws_fg_vpc=your-vpc-arn' \
        -var 'aws_fg_security_groups=["your security group, e.g. sg-1223455..."]' \
        -var 'aws_fg_subnets=["your subnet e.g. subnet-1234..."]' \
        -var 'aws_fg_image=your-docker-image-available-on-ECR' \
        -var 'aws_lb_listener_arn=your ec2 load balancer listener arn' \
        -var 'spring_boot_args=--openSearch.host=your-opensearch-url-without-http --openSearch.CCSEnabled=true --openSearch.username=our-username-empty-for-opensearch-serverless --openSearch.disciplineNodes=the-prefixes-of-the-registry-indices-in-opensearch --registry.service.version=the-version-of-the-api-to-be-displayed-in-the-application'
```


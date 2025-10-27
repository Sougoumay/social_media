output "vpc_id" {
  value = aws_vpc.main_social_media_vpc.id
  description = "ID of the main VPC"
}

output "public_subnet_ids" {
  value = [
    aws_subnet.public_subnet_1.id,
    aws_subnet.public_subnet_2.id
  ]
  description = "IDs of public subnets"
}

output "private_subnet_ids" {
  value = [
    aws_subnet.private_subnet_1.id,
    aws_subnet.private_subnet_2.id
  ]
  description = "IDs of private subnets"
}

output "internet_gateway_id" {
  value = aws_internet_gateway.social_media_gw.id
  description = "ID of the Internet Gateway"
}

output "nat_gateway_id" {
  value = aws_nat_gateway.social_media_nat_gateway.id
  description = "ID of the NAT Gateway"
}

output "nat_eip" {
  value = aws_eip.social_media_nat_eip.public_ip
  description = "Elastic IP associated with the NAT Gateway"
}

output "public_route_table_id" {
  value = aws_route_table.social_media_public_route_table.id
  description = "ID of the public route table"
}

output "private_route_table_id" {
  value = aws_route_table.social_media_private_route_table.id
  description = "ID of the private route table"
}

output "availability_zones" {
  value = [
    var.az1,
    var.az2
  ]
  description = "Availability Zones used in the deployment"
}

# L'ID de l'instance
output "ec2_instance_id" {
  value = aws_instance.social_media_ec2.id
}

# L'adresse IP privée
output "ec2_private_ip" {
  value = aws_instance.social_media_ec2.private_ip
}

# Le DNS public (pratique pour se connecter via SSH ou tester HTTP/HTTPS)
output "ec2_public_dns" {
  value = aws_instance.social_media_ec2.public_dns
}

# L'AZ (utile pour savoir où l'instance est lancée)
output "ec2_availability_zone" {
  value = aws_instance.social_media_ec2.availability_zone
}

# # L'ID de l'instance bastion
# output "bastion_ec2_instance_id" {
#   value = aws_instance.social_media_ec2.id
# }

# # L'adresse IP privée du bastion
# output "bastion_ec2_private_ip" {
#   value = aws_instance.social_media_ec2.private_ip
# }

# # L'adresse IP publique du bastion qui a un IGW et "map_public_ip_on_launch = true")
# output "bastion_ec2_public_ip" {
#   value = aws_instance.social_media_ec2_bastion.public_ip
# }

# # Le DNS public (pratique pour se connecter via SSH ou tester HTTP/HTTPS)
# output "bastion_ec2_public_dns" {
#   value = aws_instance.social_media_ec2_bastion.public_dns
# }

# # L'AZ (utile pour savoir où l'instance est lancée)
# output "bastion_availability_zone" {
#   value = aws_instance.social_media_ec2_bastion.availability_zone
# }

# Outputs du Load Balancer
output "alb_arn" {
  description = "ARN de l'Application Load Balancer"
  value       = aws_lb.social_media_application_load_balancer.arn
}

output "alb_dns_name" {
  description = "DNS name de l'Application Load Balancer"
  value       = aws_lb.social_media_application_load_balancer.dns_name
}

output "alb_zone_id" {
  description = "Zone ID pour le Load Balancer (utile pour Route53)"
  value       = aws_lb.social_media_application_load_balancer.zone_id
}

output "alb_security_groups" {
  description = "IDs des Security Groups associés au ALB"
  value       = aws_lb.social_media_application_load_balancer.security_groups
}

output "alb_subnets" {
  description = "Subnets publics utilisés par le ALB"
  value       = aws_lb.social_media_application_load_balancer.subnets
}

# Outputs du Target Group
output "target_group_arn" {
  description = "ARN du Target Group associé au ALB"
  value       = aws_lb_target_group.social_media_target_group.arn
}

output "target_group_name" {
  description = "Nom du Target Group"
  value       = aws_lb_target_group.social_media_target_group.name
}

output "target_group_health_check_path" {
  description = "Chemin configuré pour le health check du Target Group"
  value       = aws_lb_target_group.social_media_target_group.health_check[0].path
}

output "rds_endpoint" {
  description = "RDS endpoint to connect to the database"
  value       = aws_db_instance.social_media_rds.endpoint
}

output "rds_port" {
  description = "Port of the RDS instance"
  value       = aws_db_instance.social_media_rds.port
}

output "rds_username" {
  description = "Master username for the RDS instance"
  value       = aws_db_instance.social_media_rds.username
}

output "rds_name" {
  description = "Database name created in RDS"
  value       = aws_db_instance.social_media_rds.identifier
}

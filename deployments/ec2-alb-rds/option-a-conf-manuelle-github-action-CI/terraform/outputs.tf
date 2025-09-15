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


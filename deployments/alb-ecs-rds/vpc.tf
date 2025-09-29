resource "aws_vpc" "main_social_media_vpc_ecs" {
  cidr_block = var.main_cidr
  enable_dns_hostnames = true
  enable_dns_support = true

  tags = {
    Name = "social-media-ecs-vpc"
    Project = "social-media"
  }
}

resource "aws_subnet" "ecs_private_subnet_1" {
  vpc_id     = aws_vpc.main_social_media_vpc_ecs.id
  cidr_block = var.cidr_private_subnet_1
  map_public_ip_on_launch = false
  availability_zone = var.az1

  tags = {
    Name = "ecs-private-subnet-1a"
    Project = "social-media"
  }
}

resource "aws_subnet" "ecs_private_subnet_2" {
  vpc_id     = aws_vpc.main_social_media_vpc_ecs.id
  cidr_block = var.cidr_private_subnet_2
  map_public_ip_on_launch = false
  availability_zone = var.az2

  tags = {
    Name = "ecs-private-subnet-1b"
    Project = "social-media"
  }
}

resource "aws_subnet" "ecs_public_subnet_1" {
  vpc_id     = aws_vpc.main_social_media_vpc_ecs.id
  cidr_block = var.cidr_public_subnet_1
  map_public_ip_on_launch = true
  availability_zone = var.az1

  tags = {
    Name = "ecs-public-subnet-1a"
    Project = "social-media"
  }
}

resource "aws_subnet" "ecs_public_subnet_2" {
  vpc_id     = aws_vpc.main_social_media_vpc_ecs.id
  cidr_block = var.cidr_public_subnet_2
  map_public_ip_on_launch = true
  availability_zone = var.az2

  tags = {
    Name = "ecs-public-subnet-1b"
    Project = "social-media"
  }
}

resource "aws_internet_gateway" "ecs_social_media_gw" {
  vpc_id = aws_vpc.main_social_media_vpc_ecs.id

  tags = {
    Name = "ecs-social-media-IGW"
    Project = "social-media"
  }
}

resource "aws_route_table" "ecs_social_media_public_route_table" {
  vpc_id = aws_vpc.main_social_media_vpc_ecs.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.ecs_social_media_gw.id
  }

  tags = {
    Name = "ecs-social-media-public-route-table"
    Project = "social-media"
  }
}

resource "aws_route_table_association" "ecs_association_public_subnet_1" {
  subnet_id      = aws_subnet.ecs_private_subnet_1.id
  route_table_id = aws_route_table.ecs_social_media_public_route_table.id
}

resource "aws_route_table_association" "ecs_association_public_subnet_2" {
  subnet_id      = aws_subnet.ecs_public_subnet_2.id
  route_table_id = aws_route_table.ecs_social_media_public_route_table.id
}


### La creation de la NAT Gateway
resource "aws_eip" "ecs_social_media_nat_eip" {
  domain = "vpc"

  tags = {
    Name    = "ecs-social-media-nat-eip"
    Project = "social-media"
  }
}

resource "aws_nat_gateway" "ecs_social_media_nat_gateway" {
  allocation_id = aws_eip.ecs_social_media_nat_eip.id
  subnet_id     = aws_subnet.ecs_public_subnet_1.id

  tags = {
    Name    = "ecs-social-media-nat-gateway"
    Project = "social-media"
  }

  # To ensure proper ordering, it is recommended to add an explicit dependency
  # on the Internet Gateway for the VPC.
  depends_on = [aws_internet_gateway.ecs_social_media_gw]
}

resource "aws_route_table" "ecs_social_media_private_route_table" {
  vpc_id = aws_vpc.main_social_media_vpc_ecs.id

  route {
    cidr_block = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.ecs_social_media_nat_gateway.id
  }

  tags = {
    Name = "ecs-social-media-private-route-table"
    Project = "social-media"
  }
}

resource "aws_route_table_association" "ecs_association_private_subnet_1" {
  subnet_id      = aws_subnet.ecs_private_subnet_1.id
  route_table_id = aws_route_table.ecs_social_media_private_route_table.id
}

resource "aws_route_table_association" "ecs_association_private_subnet_2" {
  subnet_id      = aws_subnet.ecs_private_subnet_1.id
  route_table_id = aws_route_table.ecs_social_media_private_route_table.id
}
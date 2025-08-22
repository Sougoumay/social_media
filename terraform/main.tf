terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

# Configure the AWS Provider
provider "aws" {
  region = "us-east-1"
}

### VPC

# Create a VPC
resource "aws_vpc" "sm_vpc" {
  cidr_block = "10.0.0.0/16"
  instance_tenancy = "default"

  # Nécesaire pour EKS
  enable_dns_hostnames = true
  enable_dns_support = true

  tags = {
    Name = "social_media"
    Environnement = "Developement"
  }

}

resource "aws_internet_gateway" "sm_gw" {
  vpc_id = aws_vpc.sm_vpc.id

  tags = {
    Name = "social_media"
    Environnement = "Developement"
  }
}

# DAu minumum deux subnets sont demandés par EKS
resource "aws_subnet" "sm_vpc_subnet_1" {
  vpc_id     = aws_vpc.sm_vpc.id
  cidr_block = "10.0.1.0/24"
  availability_zone = "us-east-1a"
  map_public_ip_on_launch = true # Pour permettre aux sous-reseaux d'avoir chacune une adresse publique


  tags = {
    Name = "social_media"
    Environnement = "Developement"
  }
}

resource "aws_subnet" "sm_vpc_subnet_2" {
  vpc_id     = aws_vpc.sm_vpc.id
  cidr_block = "10.0.2.0/24"
  availability_zone = "us-east-1b"
  map_public_ip_on_launch = true # Pour permettre aux sous-reseaux d'avoir chacune une adresse publique

  tags = {
    Name = "social_media"
    Environnement = "Developement"
  }
}

resource "aws_route_table" "sm_route_table" {
  vpc_id = aws_vpc.sm_vpc.id

  tags = {
    Name = "social_media"
    Environnement = "Developement"
  }
}

# Envoie tout trafic qui n'est pas interne à la VPC vers la gateway pour l'acheminer sur internet
resource "aws_route" "sm_route" {
  route_table_id            = aws_route_table.sm_route_table.id
  destination_cidr_block = "0.0.0.0/0"
  gateway_id = aws_internet_gateway.sm_gw.id
}

# Definir l'association le sous reseau 1 et la route table
resource "aws_route_table_association" "sm_subnet_1_association" {
  subnet_id = aws_subnet.sm_vpc_subnet_1.id
  route_table_id = aws_route_table.sm_route_table.id
}

# Definir l'association le sous reseau 2 et la route table
resource "aws_route_table_association" "sm_subnet_2_association" {
  subnet_id = aws_subnet.sm_vpc_subnet_2.id
  route_table_id = aws_route_table.sm_route_table.id
}
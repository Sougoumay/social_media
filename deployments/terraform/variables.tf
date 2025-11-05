variable "ec2_instance_name" {
  description = "Value of EC2 instance's name"
  type = string
}

variable "ec2_instance_type" {
  description = "The EC2 instance's type."
  type        = string
}

variable "region" {
  description = "Region in AWS to create resources"
  type = string
}

variable "account_id" {
  description = "ID of the account to create resources"
  type = string
}

variable "az1" {
  description = "Availability zone 1 in the region us-east-1"
  type = string
}

variable "az2" {
  description = "Availability zone 2 in the region us-east-1"
  type = string
}

variable "main_cidr" {
  description = "L'ensemble des adresses reseaux du VPC"
  type = string
}

variable "cidr_private_subnet_1" {
  description = "Le sous reseau privée de l'instance ec2 et l'AZ principal de la RDS"
  type = string
}

variable "cidr_private_subnet_2" {
  description = "Le sous reseau privée pour la replication standby de la RDS"
  type = string
}

variable "cidr_public_subnet_1" {
  description = "Le sous reseau public 1"
  type = string
}

variable "cidr_public_subnet_2" {
  description = "Le sous reseau public 2"
  type = string
}

variable "rds_db_name" {
  description = "Le nom de la base de données"
  type = string
}

variable "rds_db_user" {
  description = "L'utilisateur principale de la base de donnée"
  type = string
}

variable "rds_db_password" {
  description = "Le mot de passe de la base de données"
  type = string
}


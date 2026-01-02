output "ec2_instance_id" {
  description = "ID de l'instance EC2"
  value       = aws_instance.example.id
}

output "ec2_public_ip" {
  description = "Adresse IP publique de l'EC2"
  value       = aws_instance.example.public_ip
}

output "ec2_public_dns" {
  description = "DNS public de l'EC2"
  value       = aws_instance.example.public_dns
}

output "vpc_id" {
  description = "ID du VPC"
  value       = aws_vpc.example.id
}

output "public_subnet_id" {
  description = "ID du subnet public"
  value       = aws_subnet.public.id
}

output "security_group_id" {
  description = "Security Group attaché à l'EC2"
  value       = aws_security_group.allow_ssh_and_http.id
}

output "iam_role_name" {
  description = "IAM role attaché à l'EC2 pour CloudWatch"
  value       = aws_iam_role.example_iam_role_ec2.name
}

output "instance_profile_name" {
  description = "IAM instance profile attaché à l'EC2"
  value       = aws_iam_instance_profile.example_instance_profile.name
}


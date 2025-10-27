resource "aws_db_subnet_group" "social_media_rds_subnets" {
  name       = "sm-rds-subnet-group"
  subnet_ids = [aws_subnet.private_subnet_1.id, aws_subnet.private_subnet_2.id]

  tags = {
    Name    = "sm-rds-subnet-group"
    Project = "social-media"
  }
}

resource "aws_db_instance" "social_media_rds" {
  identifier              = "sm-db"
  allocated_storage       = 20
  max_allocated_storage   = 20
  engine                  = "mysql"
  engine_version          = "8.0"
  instance_class          = "db.t3.micro"
  db_name                 = "smdb"
  username                = "admin"
  password                = "ChangeMe123!"
  db_subnet_group_name    = aws_db_subnet_group.social_media_rds_subnets.name
  vpc_security_group_ids  = [aws_security_group.social_media_RDS_SG.id]
  multi_az                = false
  publicly_accessible     = false
  skip_final_snapshot     = true
  deletion_protection     = false
  

  tags = {
    Name    = "social-media-rds"
    Project = "social-media"
  }
}

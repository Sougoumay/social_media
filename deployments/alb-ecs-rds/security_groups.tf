### Application Load Balancer Security Group ###
resource "aws_security_group" "ecs_social_media_ALB_SG" {
  name        = "SG for the ALB"
  description = "Allow HTTP/HTTPS traffic from and into internet"
  vpc_id      = aws_vpc.main_social_media_vpc_ecs.id

  tags = {
    Name = "ecs-social-media-ALB-SG"
    Project = "social-media"
  }
}

resource "aws_vpc_security_group_ingress_rule" "ecs_social_media_ALB_allow_http_ipv4" {
  security_group_id = aws_security_group.ecs_social_media_ALB_SG.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 80
  ip_protocol       = "tcp"
  to_port           = 80
}

resource "aws_vpc_security_group_ingress_rule" "ecs_social_media_ALB_allow_http_ipv6" {
  security_group_id = aws_security_group.ecs_social_media_ALB_SG.id
  cidr_ipv6         = "::/0"
  from_port         = 80
  ip_protocol       = "tcp"
  to_port           = 80
}

resource "aws_vpc_security_group_ingress_rule" "ecs_social_media_ALB_allow_https_ipv4" {
  security_group_id = aws_security_group.ecs_social_media_ALB_SG.id
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 443
  ip_protocol       = "tcp"
  to_port           = 443
}

resource "aws_vpc_security_group_ingress_rule" "ecs_social_media_ALB_allow_https_ipv6" {
  security_group_id = aws_security_group.ecs_social_media_ALB_SG.id
  cidr_ipv6         = "::/0"
  from_port         = 443
  ip_protocol       = "tcp"
  to_port           = 443
}

resource "aws_vpc_security_group_egress_rule" "ecs_allow_all_traffic_ipv4" {
  security_group_id = aws_security_group.ecs_social_media_ALB_SG.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1" # semantically equivalent to all ports
}

resource "aws_vpc_security_group_egress_rule" "ecs_allow_all_traffic_ipv6" {
  security_group_id = aws_security_group.ecs_social_media_ALB_SG.id
  cidr_ipv6         = "::/0"
  ip_protocol       = "-1" # semantically equivalent to all ports
}

### CS TAsk Definition Security Group ###
resource "aws_security_group" "ecs_social_media_Task_Definition_SG" {
  name        = "social-media-task-definition-sg"
  description = "Allow inbound HTTP/HTTPS traffic from SG of the ALB"
  vpc_id      = aws_vpc.main_social_media_vpc_ecs.id

  # Inbound rules
  ingress {
    description = "Allow HTTP from SG of the ALB"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    security_groups = [aws_security_group.ecs_social_media_ALB_SG.id]
  }

  ingress {
    description = "Allow HTTPS from SG of the ALB"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    security_groups = [aws_security_group.ecs_social_media_ALB_SG.id]
  }

  # Outbound rules
  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    Name    = "ecs-social-media-task_definition-sg"
    Project = "social-media"
  }
}


### RDS Instance Security Group ###
resource "aws_security_group" "ecs_social_media_RDS_SG" {
  name        = "social-media-rds-sg"
  description = "Allow traffic from SG of the ECS Task Definition to database"
  vpc_id      = aws_vpc.main_social_media_vpc_ecs.id

  # Inbound rules
  ingress {
    description = "Allow traffic from SG of the ECS Task Definition to database on port 3306"
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    security_groups = [aws_security_group.ecs_social_media_Task_Definition_SG.id]
  }

  # Outbound rules
  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    Name    = "ecs-social-media-rds-sg"
    Project = "social-media"
  }
}



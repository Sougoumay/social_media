resource "aws_lb_target_group" "social_media_target_group" {
  name     = "sm-target-group"
  port     = 80
  protocol = "HTTP"
  protocol_version = "HTTP1"
  ip_address_type = "ipv4"
  vpc_id   = aws_vpc.main_social_media_vpc.id

  health_check {
    protocol = "HTTP"
    path = "/actuator/health"
    healthy_threshold = 5
    unhealthy_threshold = 5
    timeout = 10
    interval = 30
  }

  tags = {
    Name    = "sm-target-group"
    Project = "social-media"
  }

}

resource "aws_lb_target_group_attachment" "sm_lb_tg_attachment" {
  target_group_arn = aws_lb_target_group.social_media_target_group.arn
  target_id        = aws_instance.social_media_ec2.id
  port             = 80
}

resource "aws_lb" "social_media_application_load_balancer" {
  name               = "social-media-application-lb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.social_media_ALB_SG.id]
  subnets            = [aws_subnet.public_subnet_1.id,aws_subnet.public_subnet_2.id]
  preserve_host_header = true
  ip_address_type = "ipv4"

  enable_deletion_protection = true

  tags = {
    Name = "sm-app-lb"
    Project = "social-media"
  }
}

resource "aws_lb_listener" "social_media_lb_listener" {
  load_balancer_arn = aws_lb.social_media_application_load_balancer.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.social_media_target_group.arn
  }
}


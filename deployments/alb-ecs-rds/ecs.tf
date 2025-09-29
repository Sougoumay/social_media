resource "aws_cloudwatch_log_group" "ecs_social_media_cloudwatch_log_group" {
  name = "social-media-ecs-cloudwatch-log-group"

  tags = {
    Name    = "sm-ecs-cloudwatch-log-group"
    Project = "social-media"
  }
}

resource "aws_ecs_cluster" "ecs_social_media_cluster" {
  name = "social-media-ecs-cluster"

  configuration {
    execute_command_configuration {
      logging    = "OVERRIDE"

      log_configuration {
        cloud_watch_encryption_enabled = true
        cloud_watch_log_group_name     = aws_cloudwatch_log_group.ecs_social_media_cloudwatch_log_group.name
      }
    }
  }

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name    = "sm-ecs-cluster"
    Project = "social-media"
  }
}

resource "aws_ecs_task_definition" "service" {
  family = "service"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = 1024
  memory                   = 2048
  skip_destroy             = true
  execution_role_arn = aws_iam_role.ecs_social_media_iam_role.arn
  task_role_arn = aws_iam_role.ecs_social_media_iam_role.arn

  container_definitions = jsonencode([
    {
      name      = "application"
      image     = "social-media"
      cpu       = 512
      memory    = 1024
      essential = true
      portMappings = [
        {
          containerPort = 80
          hostPort      = 80
          protocol      = "tcp"
        }
      ]
    }
  ])

  tags = {
    Name    = "sm-ecs-task-definition"
    Project = "social-media"
  }

  runtime_platform {
    operating_system_family = "Linux"
    cpu_architecture        = "X86_64"
  }


  placement_constraints {
    type       = "memberOf"
    expression = "attribute:ecs.availability-zone in [us-east-1a, us-east-1b]"
  }
}
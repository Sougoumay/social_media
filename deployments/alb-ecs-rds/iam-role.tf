resource "aws_iam_role" "ecs_social_media_iam_role" {
  name = "socail_media_ecs_iam_role"
  assume_role_policy = jsonencode({
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowAccessToECSForTaskExecutionRole",
            "Effect": "Allow",
            "Principal": {
                "Service": "ecs-tasks.amazonaws.com"
            },
            "Action": "sts:AssumeRole"
        }
        ]
    })

  tags = {
    Name    = "sm-ecs-iam-rome"
    Project = "social-media"
  }
}

resource "aws_iam_role_policy" "ecs_social_media_iam_role_policy" {
  name = "socail_media_ecs_iam_role_policy"
  role = aws_iam_role.ecs_social_media_iam_role.id

  policy = jsonencode({
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ecr:GetAuthorizationToken",
                "ecr:BatchCheckLayerAvailability",
                "ecr:GetDownloadUrlForLayer",
                "ecr:BatchGetImage",
                "logs:CreateLogStream",
                "logs:PutLogEvents"
            ],
            "Resource": "*"
        }]}
    )
    
}
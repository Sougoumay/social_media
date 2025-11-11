# Rôle IAM pour l'EC2, pour accéder à S3 et SSM
resource "aws_iam_role" "sm_iam_role_ec2" {
  name = "social-media-iam-role-ec2"
  path = "/project/social-media/"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"         # Autorise l’EC2 à assumer ce rôle
        Effect = "Allow"
        Sid    = ""
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      },
    ]
  })

  tags = {
    Name    = "sm-iam-role-ec2"
    Project = "social-media"
  }
}

# Instance profile pour attacher le rôle à l'EC2
resource "aws_iam_instance_profile" "sm_instance_profile" {
  name = "social-media-ec2-instance-profile"
  role = aws_iam_role.sm_iam_role_ec2.name
}

# Attachement de la policy AWS gérée SSM à l'EC2
resource "aws_iam_role_policy_attachment" "sm_ssm_policy_attachment" {
  role       = aws_iam_role.sm_iam_role_ec2.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore" 
  # Permet à EC2 d’être gérée via SSM, nécessaire pour que GitHub Actions envoie des commandes
}

# Rôle GitHub Actions (OIDC) pour pouvoir s’assumer temporairement
resource "aws_iam_role" "github_role" {
  name = "Github-IAM-Role"
  path = "/"

  assume_role_policy = jsonencode({
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow"
      "Principal": {
        "Federated": aws_iam_openid_connect_provider.sm_iam_openid_connect_for_github.arn
      },
      "Action": "sts:AssumeRoleWithWebIdentity", # GitHub assume le rôle via OIDC
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com", 
          "token.actions.githubusercontent.com:sub": "repo:Sougoumay/social_media:ref:refs/heads/main"
          # Restreint le rôle à une repo et branche spécifiques
        }
      }
    }
  ]})

  tags = {
    Name    = "Github IAM Role"
    Project = "Social-Media"
  }
}

# Policy attachée au rôle GitHub
resource "aws_iam_policy" "sm_github_iam_policy" {
  name        = "Social-media-github-IAM-policy"
  description = "Policy pour GitHub pousser le jar dans S3 et envoyer un deploy via SSM"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      # Accès au Bucket S3
      {
        Effect = "Allow"
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:ListBucket"
        ]
        Resource = [
          "arn:aws:s3:::sm_jar_bucket",
          "arn:aws:s3:::sm_jar_bucket/*"
        ]
      },

      # Autoriser SendCommand via document AWS-RunShellScript
      {
        Effect = "Allow"
        Action = ["ssm:SendCommand"]
        Resource = "*"
      },


      # Autoriser la découverte d’instances (nécessaire pour SSM)
      {
        Effect = "Allow"
        Action = [
          "ec2:DescribeInstances"
        ]
        Resource = "*"
      }
    ]
  })

  tags = {
    Name    = "SM-Github-IAM-Policy"
    Project = "social-media"
  }
}


# Attachement de la policy au rôle GitHub
resource "aws_iam_role_policy_attachment" "sm_github_policy_attachment" {
  role       = aws_iam_role.github_role.name
  policy_arn = aws_iam_policy.sm_github_iam_policy.arn
}

resource "aws_iam_openid_connect_provider" "sm_iam_openid_connect_for_github" {
  url = "https://token.actions.githubusercontent.com"

  client_id_list = ["sts.amazonaws.com"]

  thumbprint_list = ["6938fd4d98bab03faadb97b34396831e3780aea1"]
}


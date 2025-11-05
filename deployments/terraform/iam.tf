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
          Service = "ec2.amazonaws.com"   # Seulement les EC2 peuvent assumer ce rôle
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
        "Federated": "arn:aws:iam::${var.account_id}:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity", # GitHub assume le rôle via OIDC
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com", 
          "token.actions.githubusercontent.com:sub": "repo:Sougoumay/social-media:ref:refs/heads/deploy-ec2-alb-rds"
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
  description = "Policy pour GitHub pousser le jar et exécuter SSM"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "s3:PutObject",           # Autorise GitHub à pousser le jar
          "ssm:SendCommand"         # Autorise GitHub à exécuter des commandes SSM sur EC2
        ]
        Effect   = "Allow"
        Resource = [
          "arn:aws:s3:::sm_jar_bucket/*",                               # Bucket cible
          "arn:aws:ec2:${var.region}:${var.account_id}:instance/${aws_instance.social_media_ec2.id}" 
          # Limité à l'instance EC2 spécifique
        ]
      },
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

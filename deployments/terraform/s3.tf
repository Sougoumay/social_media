# Création du bucket S3 pour stocker les JARs de l'application
resource "aws_s3_bucket" "social_media_s3_jar" {
  bucket        = "sm-jar-bucket"       # Nom du bucket
  force_destroy = true                   # Permet de supprimer le bucket même s’il contient des fichiers

  tags = {
    Name        = "s3-bucket-for-jar"   
    Environment = "social-media"       
  }
}

# Génération de la policy IAM pour le bucket
data "aws_iam_policy_document" "sm_jar_bucket_policy_doc" {

  # Autoriser le compte root à accéder à tout le bucket
  statement {
    sid    = "AllowRootAccount"           
    effect = "Allow"                      
    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${var.account_id}:root"]  # Compte root AWS
    }
    actions = ["s3:*"]                    # Toutes les actions sur S3
    resources = [
      aws_s3_bucket.social_media_s3_jar.arn,      # Bucket
      "${aws_s3_bucket.social_media_s3_jar.arn}/*" # Tous les objets du bucket
    ]
  }

  # Autoriser l'EC2 via son rôle IAM à lister et lire les objets
  statement {
    sid    = "AllowEC2RoleAccess"
    effect = "Allow"
    principals {
      type        = "AWS"
      identifiers = [aws_iam_role.sm_iam_role_ec2.arn] # Rôle IAM attaché à EC2
    }
    actions   = ["s3:GetObject", "s3:ListBucket"]      # Lecture + listing
    resources = [
      aws_s3_bucket.social_media_s3_jar.arn,
      "${aws_s3_bucket.social_media_s3_jar.arn}/*"
    ]
  }

  # Autoriser l'utilisateur DevOps-Training à tout faire sur le bucket
  statement {
    sid    = "AllowDevOpsUserAccess"
    effect = "Allow"
    principals {
      type        = "AWS"
      identifiers = ["arn:aws:iam::${var.account_id}:user/DevOps-Training"]
    }
    actions   = ["s3:*"]                      # Full access
    resources = [
      aws_s3_bucket.social_media_s3_jar.arn,
      "${aws_s3_bucket.social_media_s3_jar.arn}/*"
    ]
  }

  # Autoriser GitHub Actions via un rôle assumé
  statement {
    sid    = "AllowGitHubActionsRole"
    effect = "Allow"
    principals {
      type        = "AWS"
      identifiers = [aws_iam_role.github_role.arn] # Rôle GitHub Actions
    }
    actions   = ["s3:*"]                        # Full access pour pousser le JAR
    resources = [
      aws_s3_bucket.social_media_s3_jar.arn,
      "${aws_s3_bucket.social_media_s3_jar.arn}/*"
    ]
  }
}

# Application de la policy IAM au bucket
resource "aws_s3_bucket_policy" "sm_jar_bucket_policy" {
  bucket = aws_s3_bucket.social_media_s3_jar.id  # Le bucket cible
  policy = data.aws_iam_policy_document.sm_jar_bucket_policy_doc.json  # Policy ci-dessus
}

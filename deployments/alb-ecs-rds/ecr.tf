resource "aws_ecr_repository" "foo" {
  name                 = "sm-images-container"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = false
  }

  tags = {
    Name    = "social-media-ecr"
    Project = "social-media"
  }
}
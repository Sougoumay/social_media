# Il faut d'abord créé la clé publique et privée avec ssh : ssh-keygen -t rsa -b 4096 -f ~/.ssh/sm-key-pair
resource "aws_key_pair" "socia_media_ec2_key_pair" {
  key_name   = "sm-key-pair"
  public_key = file("~/.ssh/sm-key-pair.pub")

  tags = {
    Name = "key-pair"
    Project = "social-media"
  }
}


data "aws_ami" "ubuntu" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}

resource "aws_instance" "social_media_ec2" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.private_subnet_1.id
  vpc_security_group_ids = [aws_security_group.social_media_EC2_SG.id]
  key_name = aws_key_pair.socia_media_ec2_key_pair.key_name

  tags = {
    Name = "ec2-instance"
    Project = "social-media"
  }
}

resource "aws_instance" "social_media_ec2_bastion" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public_subnet_1.id
  vpc_security_group_ids = [aws_security_group.social_media_ec2_bastion_SG.id]
  key_name = aws_key_pair.socia_media_ec2_key_pair.key_name

  tags = {
    Name = "ec2-instance-bastion"
    Project = "social-media"
  }
}
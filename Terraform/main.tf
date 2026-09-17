# Generación de Llave SSH
resource "tls_private_key" "deploy_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "deploy_key" {
  key_name   = "cloudnative-deploy-key"
  public_key = tls_private_key.deploy_key.public_key_openssh
}

# Elastic IPs
resource "aws_eip" "backend_eip" {
  domain = "vpc"
}

resource "aws_eip" "frontend_eip" {
  domain = "vpc"
}

# Grupos de Seguridad
resource "aws_security_group" "backend_sg" {
  name        = "encargo-backend-sg"
  description = "Permite acceso SSH y trafico al API Gateway interno (8095)"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = var.backend_gateway_port
    to_port     = var.backend_gateway_port
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "frontend_sg" {
  name        = "encargo-frontend-sg"
  description = "Permite acceso Web y SSH"

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 5173
    to_port     = 5173
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Obtener AMI de Ubuntu 22.04 LTS
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }
}

# Instancia Backend (Microservicios + Spring Gateway)
resource "aws_instance" "backend" {
  ami                  = data.aws_ami.ubuntu.id
  instance_type        = var.backend_instance_type
  key_name             = aws_key_pair.deploy_key.key_name
  vpc_security_group_ids = [aws_security_group.backend_sg.id]

  root_block_device {
    volume_size = 25
    volume_type = "gp3"
  }

  user_data = <<-EOF
              #!/bin/bash
              set -e
              
              # Configurar 4GB de SWAP
              fallocate -l 4G /swapfile
              chmod 600 /swapfile
              mkswap /swapfile
              swapon /swapfile
              echo '/swapfile none swap sw 0 0' >> /etc/fstab

              # Instalar Docker
              apt-get update
              apt-get install -y ca-certificates curl gnupg git
              install -m 0755 -d /etc/apt/keyrings
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
              chmod a+r /etc/apt/keyrings/docker.gpg
              echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu jammy stable" > /etc/apt/sources.list.d/docker.list
              apt-get update
              apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

              # Clonar el proyecto
              mkdir -p /home/ubuntu/app
              git clone -b ${var.git_branch} ${var.git_repo_url} /home/ubuntu/app

              # Escribir el archivo .env
              cat <<EOT > /home/ubuntu/app/.env
              ECR_REGISTRY=827920207598.dkr.ecr.us-east-1.amazonaws.com
              ENTRA_TENANT_ID=120aafaf-ea47-4c03-b1b6-68ef7c7c9dce
              ENTRA_AUDIENCE=aec497bb-c720-40cc-9e4f-87f811226d6f
              DB_HOST=${aws_db_instance.mysql_db.address} 
              DB_USERNAME=${var.db_username}
              DB_PASSWORD=${var.db_password}
              EOT

              chown -R ubuntu:ubuntu /home/ubuntu/app
              cd /home/ubuntu/app
              docker compose up -d
              EOF

  tags = {
    Name = "Encargo-Backend-EC2"
  }
}

resource "aws_eip_association" "backend_eip_assoc" {
  instance_id   = aws_instance.backend.id
  allocation_id = aws_eip.backend_eip.id
}

# Instancia Frontend (Nginx SPA)
resource "aws_instance" "frontend" {
  ami                  = data.aws_ami.ubuntu.id
  instance_type        = var.frontend_instance_type
  key_name             = aws_key_pair.deploy_key.key_name
  vpc_security_group_ids = [aws_security_group.frontend_sg.id]

  root_block_device {
    volume_size = 15
    volume_type = "gp3"
  }

  user_data = <<-EOF
              #!/bin/bash
              set -e

              # Configurar 2GB SWAP
              fallocate -l 2G /swapfile
              chmod 600 /swapfile
              mkswap /swapfile
              swapon /swapfile
              echo '/swapfile none swap sw 0 0' >> /etc/fstab

              # Instalar Docker
              apt-get update
              apt-get install -y ca-certificates curl gnupg git
              install -m 0755 -d /etc/apt/keyrings
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
              chmod a+r /etc/apt/keyrings/docker.gpg
              echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu jammy stable" > /etc/apt/sources.list.d/docker.list
              apt-get update
              apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

              mkdir -p /home/ubuntu/app
              git clone -b ${var.git_branch} ${var.git_repo_url} /home/ubuntu/app

              cat <<EOT > /home/ubuntu/app/frontend/.env
              VITE_ENTRA_CLIENT_ID=47099009-530d-4953-9c13-8453c7369262
              VITE_ENTRA_TENANT_ID=120aafaf-ea47-4c03-b1b6-68ef7c7c9dce
              VITE_API_GATEWAY_URL=http://${aws_eip.backend_eip.public_ip}:${var.backend_gateway_port}
              EOT

              chown -R ubuntu:ubuntu /home/ubuntu/app
              cd /home/ubuntu/app
              docker compose up -d frontend
              EOF

  tags = {
    Name = "Encargo-Frontend-EC2"
  }
}

resource "aws_eip_association" "frontend_eip_assoc" {
  instance_id   = aws_instance.frontend.id
  allocation_id = aws_eip.frontend_eip.id
}
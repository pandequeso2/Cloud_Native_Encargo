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

  ingress {
    description = "Dashboard Eureka Server"
    from_port   = 8761
    to_port     = 8761
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Dashboard Kafka UI"
    from_port   = 8080
    to_port     = 8080
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

data "aws_caller_identity" "current" {}

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
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = var.backend_instance_type
  key_name                    = aws_key_pair.deploy_key.key_name
  vpc_security_group_ids      = [aws_security_group.backend_sg.id]
  user_data_replace_on_change = true

  root_block_device {
    volume_size = 25
    volume_type = "gp3"
  }

  user_data = <<-EOF
              #!/bin/bash
              set -e
              
              fallocate -l 4G /swapfile
              chmod 600 /swapfile
              mkswap /swapfile
              swapon /swapfile
              echo '/swapfile none swap sw 0 0' >> /etc/fstab

              apt-get update
              apt-get install -y ca-certificates curl gnupg git
              install -m 0755 -d /etc/apt/keyrings
              curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
              chmod a+r /etc/apt/keyrings/docker.gpg
              echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu jammy stable" > /etc/apt/sources.list.d/docker.list
              apt-get update
              apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin maven openjdk-17-jdk

              # Configurar DNS publico para evitar fallos de resolucion de Microsoft en contenedores
              cat << 'EODNS' > /etc/docker/daemon.json
              {
                "dns": ["8.8.8.8", "1.1.1.1"]
              }
              EODNS
              systemctl restart docker

              mkdir -p /home/ubuntu/app
              git clone -b ${var.git_branch} ${var.git_repo_url} /home/ubuntu/app

              # Copiar compose de produccion y configuracion de RabbitMQ
              cp /home/ubuntu/app/docker-compose.prod.yml /home/ubuntu/app/docker-compose.yml

              cat <<EOT > /home/ubuntu/app/.env
              ECR_REGISTRY=${data.aws_caller_identity.current.account_id}.dkr.ecr.us-east-1.amazonaws.com
              ENTRA_TENANT_ID=${var.entra_tenant_id}
              ENTRA_AUDIENCE=${var.entra_audience}
              DB_HOST=${aws_db_instance.mysql_db.address} 
              DB_USERNAME=${var.db_username}
              DB_PASSWORD=${var.db_password}
              RABBITMQ_USER=admin
              RABBITMQ_PASSWORD=admin123
              EOT

              # Script para compilar y levantar los microservicios automaticamente
              cat << 'EOT' > /home/ubuntu/app/start.sh
              #!/bin/bash
              set -e
              cd /home/ubuntu/app
              REGISTRY=$(grep ECR_REGISTRY .env | cut -d '=' -f2)

              # Compilar microservicios si no existen los artefactos JAR
              if [ ! -f "Backend/gateway/target/gateway-0.0.1-SNAPSHOT.jar" ]; then
                for dir in Backend/*/; do
                  if [ -f "$dir/pom.xml" ]; then
                    svc=$(basename "$dir")
                    echo "Compilando $svc..."
                    (cd "$dir" && mvn clean package -DskipTests)
                    sudo docker build -t "$REGISTRY/cloudnative-encargo-$${svc//_/-}:latest" "$dir"
                  fi
                done
              fi

              sudo docker pull rabbitmq:4.2-management || true
              sudo docker compose -f docker-compose.prod.yml up -d
              EOT
              chmod +x /home/ubuntu/app/start.sh

              /home/ubuntu/app/start.sh
              chown -R ubuntu:ubuntu /home/ubuntu/app
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
  ami                         = data.aws_ami.ubuntu.id
  instance_type               = var.frontend_instance_type
  key_name                    = aws_key_pair.deploy_key.key_name
  vpc_security_group_ids      = [aws_security_group.frontend_sg.id]
  user_data_replace_on_change = true

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

              cat <<EOT > /home/ubuntu/app/Frontend/.env
              VITE_ENTRA_CLIENT_ID=${var.entra_client_id}
              VITE_ENTRA_TENANT_ID=${var.entra_tenant_id}
              VITE_ENTRA_API_CLIENT_ID=${replace(var.entra_audience, "api://", "")}
              VITE_API_BASE_URL=
              VITE_REDIRECT_URI=https://${aws_eip.frontend_eip.public_ip}
              EOT

              # Inyectar la IP del Backend en la configuracion de Nginx
              sed -i "s|http://.*:8095|http://${aws_eip.backend_eip.public_ip}:${var.backend_gateway_port}|g" /home/ubuntu/app/Frontend/nginx.conf

              chown -R ubuntu:ubuntu /home/ubuntu/app

              # Compilar y levantar la SPA automáticamente al iniciar la EC2
              cd /home/ubuntu/app/Frontend
              docker build \
                --build-arg VITE_ENTRA_CLIENT_ID="${var.entra_client_id}" \
                --build-arg VITE_ENTRA_TENANT_ID="${var.entra_tenant_id}" \
                --build-arg VITE_ENTRA_API_CLIENT_ID="${replace(var.entra_audience, "api://", "")}" \
                --build-arg VITE_API_BASE_URL="" \
                --build-arg VITE_REDIRECT_URI="https://${aws_eip.frontend_eip.public_ip}" \
                -t cloudnative-frontend .

              docker rm -f frontend || true
              docker run -d --name frontend --restart always -p 80:80 -p 443:443 cloudnative-frontend
              EOF

  tags = {
    Name = "Encargo-Frontend-EC2"
  }
}

resource "aws_eip_association" "frontend_eip_assoc" {
  instance_id   = aws_instance.frontend.id
  allocation_id = aws_eip.frontend_eip.id
}

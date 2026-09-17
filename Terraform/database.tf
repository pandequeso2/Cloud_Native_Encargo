# Grupo de Seguridad para la Base de Datos
resource "aws_security_group" "db_sg" {
  name        = "encargo-db-sg"
  description = "Permite trafico MySQL solo desde el backend"

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.backend_sg.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Instancia RDS MySQL
resource "aws_db_instance" "mysql_db" {
  identifier             = "encargo-bitacora-db"
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  db_name                = "bitacora_db" # Base de datos inicial que creará automáticamente
  username               = var.db_username
  password               = var.db_password
  parameter_group_name   = "default.mysql8.0"
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  skip_final_snapshot    = true # Evita errores al intentar hacer "terraform destroy"
  publicly_accessible    = false
}

# Output para ver la dirección una vez termine
output "db_host" {
  value       = aws_db_instance.mysql_db.address
  description = "Dirección del host de la base de datos"
}
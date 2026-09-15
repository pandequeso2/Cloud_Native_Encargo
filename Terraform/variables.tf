variable "aws_region" {
  description = "Región de AWS donde se despliega todo"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Prefijo usado para nombrar todos los recursos (ECR, EC2, RDS, etc.)"
  type        = string
  default     = "cloudnative-encargo"
}

variable "ec2_instance_type" {
  description = "Tamaño de la instancia EC2. t3.medium es el mínimo razonable para 12 contenedores Java; si se queda sin memoria, subir a t3.large."
  type        = string
  default     = "t3.medium"
}

variable "key_pair_name" {
  description = "Nombre del Key Pair de EC2 ya creado en la consola de AWS, para poder hacer SSH"
  type        = string
}

variable "allowed_ssh_cidr" {
  description = "IP (o rango) desde donde se permite SSH al EC2, en formato CIDR. Usa tu IP pública + /32."
  type        = string
}

variable "db_username" {
  description = "Usuario administrador de la base de datos RDS"
  type        = string
  default     = "admin"
}

variable "db_password" {
  description = "Contraseña de la base de datos RDS"
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  description = "Tamaño de la instancia RDS"
  type        = string
  default     = "db.t3.medium"
}

variable "entra_tenant_id" {
  description = "Directory (tenant) ID de Microsoft Entra ID"
  type        = string
}

variable "entra_audience" {
  description = "Client ID (GUID, sin api://) del App Registration del backend en Entra ID"
  type        = string
}

# Nombres de todos los servicios que se dockerizan y suben a ECR.
# Deben coincidir EXACTO con las carpetas de Backend/ y con docker-compose.yml.
variable "services" {
  description = "Lista de microservicios + gateway + eureka + frontend a contenerizar"
  type        = list(string)
  default = [
    "eureka-server",
    "gateway",
    "notas-service",
    "asignaturas-service",
    "profesores-service",
    "grupos-service",
    "integrantes-service",
    "roles-service",
    "secciones-service",
    "trabajos-service",
    "entregas-service",
    "comentarios-service",
    "frontend"
  ]
}
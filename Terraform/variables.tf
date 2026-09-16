variable "aws_region" {
  type        = string
  default     = "us-east-1"
  description = "Región de AWS donde se desplegará la infraestructura."
}

variable "git_repo_url" {
  type        = string
  default     = "https://github.com/tu-usuario/Cloud_Native_Encargo.git"
  description = "URL del repositorio GitHub del proyecto."
}

variable "git_branch" {
  type        = string
  default     = "main"
  description = "Rama del repositorio a desplegar."
}

variable "backend_instance_type" {
  type        = string
  default     = "t2.medium"
  description = "Tipo de instancia para alojar los microservicios y API Gateway."
}

variable "frontend_instance_type" {
  type        = string
  default     = "t2.micro"
  description = "Tipo de instancia para la SPA Frontend."
}

variable "azure_tenant_id" {
  type        = string
  default     = "551dc2ab-db79-43ed-97de-ec90a21f3e0c"
  description = "Tenant ID de Microsoft Entra ID."
}

variable "backend_gateway_port" {
  type        = number
  default     = 8095
  description = "Puerto expuesto por el Spring Cloud Gateway."
}

variable "db_host" {
  type        = string
  default     = "terraform-20260915215520479900000001.cd1sokqbxuai.us-east-1.rds.amazonaws.com"
  description = "Endpoint DNS de la base de datos RDS MySQL."
}

variable "db_username" {
  type        = string
  default     = "admin"
}

variable "db_password" {
  type        = string
  sensitive   = true
  default     = "BitacoraSegura2026"
}

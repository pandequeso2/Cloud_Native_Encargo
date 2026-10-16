variable "aws_region" {
  type        = string
  default     = "us-east-1"
  description = "Región de AWS donde se desplegará la infraestructura."
}

variable "git_repo_url" {
  type        = string
  default     = "https://github.com/pandequeso2/Cloud_Native_Encargo.git"
  description = "URL del repositorio GitHub del proyecto."
}

variable "git_branch" {
  type        = string
  default     = "Develop"
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
  type    = string
  default = "admin"
}

variable "db_password" {
  type      = string
  sensitive = true
  default   = "BitacoraSegura2026"
}

variable "entra_tenant_id" {
  type        = string
  default     = "551dc2ab-db79-43ed-97de-ec90a21f3e0c"
  description = "ID del Tenant (Directorio) en Microsoft Entra ID."
}

variable "entra_audience" {
  type        = string
  default     = "api://cdde4875-2f04-4a9e-b27c-2e1ad93c9366"
  description = "Audience de la API (cloudnative-backend) en Microsoft Entra ID."
}

variable "entra_client_id" {
  type        = string
  default     = "be1f94f2-51ba-4a74-ab8d-b258e2123e6e"
  description = "Client ID de la SPA (cloudnative-frontend) en Microsoft Entra ID."
}
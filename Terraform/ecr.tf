locals {
  microservicios = [
    "cloudnative-encargo-eureka-server",
    "cloudnative-encargo-gateway",
    "cloudnative-encargo-notas-service",
    "cloudnative-encargo-asignaturas-service",
    "cloudnative-encargo-profesores-service",
    "cloudnative-encargo-grupos-service",
    "cloudnative-encargo-integrantes-service",
    "cloudnative-encargo-roles-service",
    "cloudnative-encargo-secciones-service",
    "cloudnative-encargo-trabajos-service",
    "cloudnative-encargo-entregas-service",
    "cloudnative-encargo-comentarios-service",
    "cloudnative-encargo-frontend"
  ]
}

resource "aws_ecr_repository" "repositorios" {
  for_each             = toset(local.microservicios)
  name                 = each.key
  image_tag_mutability = "MUTABLE"
  
  force_delete         = true 
}
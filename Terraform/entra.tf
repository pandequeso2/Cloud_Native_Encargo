# Aplicación Backend en Entra ID
resource "azuread_application" "backend" {
  display_name     = "bitacora-backend-api"
  identifier_uris  = ["api://bitacora-backend-api"]
  owners           = [data.azuread_client_config.current.object_id]

  api {
    requested_accessor_claim {
      title                      = "Read/Write Access"
      description                = "Permite acceso a los servicios de Bitacora"
      enabled                    = true
      user_consent_display_name  = "Acceso general"
      user_consent_description   = "Permite realizar peticiones al backend"
      value                      = "access_as_user"
      id                         = "11111111-2222-3333-4444-555555555555"
    }
  }
}

data "azuread_client_config" "current" {}

# Aplicación Frontend (SPA) en Entra ID
resource "azuread_application" "frontend" {
  display_name = "bitacora-frontend-spa"
  owners       = [data.azuread_client_config.current.object_id]

  single_page_application {
    redirect_uris = [
      "http://${aws_eip.frontend_eip.public_ip}",
      "http://localhost:3000"
    ]
  }

  required_resource_access {
    resource_app_id = azuread_application.backend.client_id

    resource_access {
      id   = "11111111-2222-3333-4444-555555555555"
      type = "Scope"
    }
  }
}
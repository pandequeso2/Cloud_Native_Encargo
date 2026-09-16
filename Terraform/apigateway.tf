resource "aws_apigatewayv2_api" "http_api" {
  name          = "encargo-bitacora-api"
  protocol_type = "HTTP"

  cors_configuration {
    allow_origins = ["http://${aws_eip.frontend_eip.public_ip}", "http://localhost:3000"]
    allow_methods = ["GET", "POST", "PUT", "DELETE", "OPTIONS"]
    allow_headers = ["Authorization", "Content-Type", "X-Requested-With"]
    max_age       = 300
  }
}

resource "aws_apigatewayv2_authorizer" "entra_jwt" {
  api_id           = aws_apigatewayv2_api.http_api.id
  authorizer_type  = "JWT"
  identity_sources = ["$request.header.Authorization"]
  name             = "entra-id-jwt-authorizer"

  jwt_configuration {
    audience = [azuread_application.backend.client_id]
    issuer   = "https://login.microsoftonline.com/${var.azure_tenant_id}/v2.0"
  }
}

resource "aws_apigatewayv2_integration" "backend_integration" {
  api_id                 = aws_apigatewayv2_api.http_api.id
  integration_type       = "HTTP_PROXY"
  integration_method     = "ANY"
  integration_uri        = "http://${aws_eip.backend_eip.public_ip}:${var.backend_gateway_port}/{proxy}"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_route" "options_route" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "OPTIONS /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.backend_integration.id}"
}

resource "aws_apigatewayv2_route" "protected_route" {
  api_id             = aws_apigatewayv2_api.http_api.id
  route_key          = "ANY /{proxy+}"
  target             = "integrations/${aws_apigatewayv2_integration.backend_integration.id}"
  authorization_type = "JWT"
  authorizer_id      = aws_apigatewayv2_authorizer.entra_jwt.id
}

resource "aws_apigatewayv2_stage" "default_stage" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}

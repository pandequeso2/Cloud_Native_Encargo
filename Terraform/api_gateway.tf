resource "aws_apigatewayv2_api" "http_api" {
  name          = "${var.project_name}-api"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_integration" "backend_proxy" {
  api_id                 = aws_apigatewayv2_api.http_api.id
  integration_type       = "HTTP_PROXY"
  integration_method     = "ANY"
  integration_uri        = "http://${aws_instance.backend.public_ip}:8095/{proxy}"
  payload_format_version = "1.0"
}

resource "aws_apigatewayv2_authorizer" "entra_id" {
  api_id           = aws_apigatewayv2_api.http_api.id
  authorizer_type  = "JWT"
  identity_sources = ["$request.header.Authorization"]
  name             = "entra-id-authorizer"

  jwt_configuration {
    audience = [var.entra_audience]
    issuer   = "https://login.microsoftonline.com/${var.entra_tenant_id}/v2.0"
  }
}

resource "aws_apigatewayv2_route" "proxy" {
  api_id    = aws_apigatewayv2_api.http_api.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.backend_proxy.id}"

  authorization_type = "JWT"
  authorizer_id       = aws_apigatewayv2_authorizer.entra_id.id
}

resource "aws_apigatewayv2_stage" "default" {
  api_id      = aws_apigatewayv2_api.http_api.id
  name        = "$default"
  auto_deploy = true
}
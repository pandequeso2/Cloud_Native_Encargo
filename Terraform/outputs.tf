output "backend_public_ip" {
  description = "IP Pública estática asignada a la EC2 de Backend."
  value       = aws_eip.backend_eip.public_ip
}

output "frontend_public_ip" {
  description = "IP Pública estática asignada a la EC2 de Frontend."
  value       = aws_eip.frontend_eip.public_ip
}

output "frontend_url" {
  description = "URL de acceso a la SPA."
  value       = "http://${aws_eip.frontend_eip.public_ip}"
}

output "api_gateway_invoke_url" {
  description = "URL base expuesta por AWS API Gateway v2."
  value       = aws_apigatewayv2_stage.default_stage.invoke_url
}

output "ec2_ssh_private_key" {
  description = "Clave privada SSH para acceder a las instancias EC2."
  value       = tls_private_key.deploy_key.private_key_pem
  sensitive   = true
}
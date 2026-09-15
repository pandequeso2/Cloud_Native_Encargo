output "ec2_public_ip" {
  value = aws_instance.backend.public_ip
}

output "api_gateway_url" {
  value = aws_apigatewayv2_api.http_api.api_endpoint
}

output "rds_endpoint" {
  value = aws_db_instance.default.address
}

output "ecr_repository_urls" {
  value = { for k, v in aws_ecr_repository.services : k => v.repository_url }
}
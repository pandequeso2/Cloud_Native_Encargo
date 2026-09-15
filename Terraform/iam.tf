# AWS Academy no permite crear roles IAM nuevos (iam:CreateRole denegado).
# En su lugar, usamos el rol y el instance profile que Academy ya deja
# pre-creados en cada laboratorio: LabRole / LabInstanceProfile.
data "aws_iam_instance_profile" "lab" {
  name = "LabInstanceProfile"
}
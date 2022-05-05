module "container_repository" {
  source          = "git::git@bitbucket.org:christian_m/aws_ecr.git?ref=v1.0.1"
  repository_name = var.application_name
}

resource "aws_ses_receipt_rule_set" "default" {
  rule_set_name = var.receipt_rule_set
  provider = aws.secondary_region
}
variable "region" {}

variable "secondary_region" {}

variable "environment" {}

variable "account_id" {}

variable "domain_name" {}

variable "application_name" {}

variable "receive_email_address_prefix" {}

variable "receipt_rule_set" {}

data "aws_iam_group" "api_user" {
  group_name = "api_user"
}

locals {
  bucket_name          = "${var.environment}-${var.application_name}.${var.domain_name}"
  dynamodb_table_name  = "${var.environment}_${var.application_name}_message_locks"
  receive_email_prefix = "${var.environment}-${var.receive_email_address_prefix}"
  iam_user_name        = "${var.environment}_local_${var.application_name}"
}
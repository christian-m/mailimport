variable "region" {}

variable "secondary_region" {}

variable "environment" {}

variable "application_name" {}

variable "account_id" {}

variable "domain_name" {}

variable "db_username" {}

data "aws_iam_group" "api_user" {
  group_name = "api_user"
}

data "aws_s3_bucket" "s3_bucket" {
  bucket = local.bucket_name
}

data "aws_dynamodb_table" "message_lock_db" {
  name = local.dynamodb_table_name
}

locals {
  bucket_name         = "${var.environment}-${var.application_name}.${var.domain_name}"
  dynamodb_table_name = "${var.environment}_${var.application_name}_message_locks"
  domain_name         = "${var.environment}-${var.application_name}.${var.domain_name}"
  db_instance_name    = "${var.environment}-${var.application_name}"
  iam_user_name       = "${var.environment}_ecs_${var.application_name}"
  vpc_domain_name     = "${var.environment}.${var.domain_name}"
  availability_zones  = ["${var.region}a", "${var.region}b", "${var.region}c"]
}
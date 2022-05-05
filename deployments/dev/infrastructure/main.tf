module "message_lock_db" {
  source          = "git::git@bitbucket.org:christian_m/aws_dynamodb.git?ref=v1.0"
  environment     = var.environment
  table_name      = local.dynamodb_table_name
  hash_key_column = {
    name = "LockID",
    type = "S",
  }
}

module "s3_bucket" {
  source          = "git::git@bitbucket.org:christian_m/aws_s3_bucket_private.git?ref=v1.0"
  environment     = var.environment
  bucket_name     = local.bucket_name
  bucket_policies = [
    jsonencode({
      "Version" : "2012-10-17",
      "Statement" : [
        {
          "Sid" : "AllowSESPuts",
          "Effect" : "Allow",
          "Principal" : {
            "Service" : "ses.amazonaws.com"
          },
          "Action" : "s3:PutObject",
          "Resource" : "arn:aws:s3:::${local.bucket_name}/*",
          "Condition" : {
            "StringEquals" : {
              "aws:Referer" : var.account_id
            }
          }
        }
      ]
    }),
  ]
  force_destroy = true
}

module "sns_success_topic" {
  source      = "git::git@bitbucket.org:christian_m/aws_sns_topic.git?ref=v1.0"
  environment = var.environment
  topic_name  = "${var.environment}_mailimport_success"
  providers   = {
    aws = aws.secondary_region
  }
}

module "sqs_sucess_queue" {
  source           = "git::git@bitbucket.org:christian_m/aws_sqs_queue.git?ref=v1.0"
  environment      = var.environment
  queue_name       = "${var.environment}_mailimport_success"
  queue_source_arn = module.sns_success_topic.topic_arn
}

module "sns_sqs_success_subscription" {
  source        = "git::git@bitbucket.org:christian_m/aws_sns_subscription.git?ref=v1.0"
  environment   = var.environment
  topic_arn     = module.sns_success_topic.topic_arn
  subscriptions = {
    success_topic_subscription = {
      protocol     = "sqs"
      endpoint_arn = module.sqs_sucess_queue.queue_arn
    }
  }
  providers = {
    aws = aws.secondary_region
  }
}

module "sns_error_topic" {
  source      = "git::git@bitbucket.org:christian_m/aws_sns_topic.git?ref=v1.0"
  environment = var.environment
  topic_name  = "${var.environment}_mailimport_error"
  providers   = {
    aws = aws.secondary_region
  }
}

module "sqs_error_queue" {
  source           = "git::git@bitbucket.org:christian_m/aws_sqs_queue.git?ref=v1.0"
  environment      = var.environment
  queue_name       = "${var.environment}_mailimport_error"
  queue_source_arn = module.sns_error_topic.topic_arn
}

module "sns_sqs_error_subscription" {
  source        = "git::git@bitbucket.org:christian_m/aws_sns_subscription.git?ref=v1.0"
  environment   = var.environment
  topic_arn     = module.sns_error_topic.topic_arn
  subscriptions = {
    error_topic_subscription = {
      protocol     = "sqs"
      endpoint_arn = module.sqs_error_queue.queue_arn
    }
  }
  providers = {
    aws = aws.secondary_region
  }
}

module "ses" {
  source                = "git::git@bitbucket.org:christian_m/aws_ses_identity.git?ref=v1.1"
  environment          = var.environment
  domain_name          = var.domain_name
  receive_email_prefix = local.receive_email_prefix
  success_topic_arn    = module.sns_success_topic.topic_arn
  error_topic_arn      = module.sns_error_topic.topic_arn
  bucket_name          = local.bucket_name
  receipt_rule_set     = var.receipt_rule_set
  providers            = {
    aws = aws.secondary_region
  }
  depends_on = [module.s3_bucket]
}

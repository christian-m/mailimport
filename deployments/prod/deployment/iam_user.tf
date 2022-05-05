resource "aws_iam_user" "mailimport" {
  name = local.iam_user_name
  path = "/api_user/"
}

resource "aws_iam_access_key" "mailimport_user" {
  user = aws_iam_user.mailimport.name
}

resource "aws_iam_user_group_membership" "api_user" {
  user   = aws_iam_user.mailimport.name
  groups = [data.aws_iam_group.api_user.group_name]
}

resource "aws_iam_user_policy" "use_s3" {
  user   = aws_iam_user.mailimport.name
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : [
          "s3:ListBucket",
          "s3:GetBucketLocation",
          "s3:ListObjects",
          "s3:PutObject",
          "s3:GetObject",
          "s3:DeleteObject",
          "s3:DeleteObjects"
        ],
        "Resource" : [
          "arn:aws:s3:::${data.aws_s3_bucket.s3_bucket.bucket}/*",
          "arn:aws:s3:::${data.aws_s3_bucket.s3_bucket.bucket}"
        ]
      }
    ]
  })
}

resource "aws_iam_user_policy" "use_dynamodb" {
  user   = aws_iam_user.mailimport.name
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : [
          "dynamodb:Query",
          "dynamodb:GetRecords",
          "dynamodb:PutItem",
          "dynamodb:GetItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem"
        ],
        "Resource" : [
          "arn:aws:dynamodb:eu-central-1:${var.account_id}:table/${data.aws_dynamodb_table.message_lock_db.name}/index/*",
          "arn:aws:dynamodb:eu-central-1:${var.account_id}:table/${data.aws_dynamodb_table.message_lock_db.name}",
          "arn:aws:dynamodb:eu-central-1:${var.account_id}:table/${data.aws_dynamodb_table.message_lock_db.name}/stream/*"
        ]
      }
    ]
  })
}

resource "aws_iam_user_policy" "use_sqs" {
  user   = aws_iam_user.mailimport.name
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : [
          "sqs:ListQueues",
          "sqs:GetQueueUrl",
          "sqs:GetQueueAttributes",
          "sqs:ReceiveMessage",
          "sqs:SendMessage",
          "sqs:DeleteMessage",
          "sqs:DeleteMessageBatch"
        ],
        "Resource" : [
          "*"
        ]
      }
    ]
  })
}

resource "aws_iam_user_policy" "cloudwatch_logs" {
  user   = aws_iam_user.mailimport.name
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:DescribeLogGroups",
          "logs:DescribeLogStreams",
          "logs:PutLogEvents",
          "logs:GetLogEvents",
          "logs:FilterLogEvents"
        ],
        Resource : "arn:aws:logs:*:*:*"
      }
    ]
  })
}

resource "aws_iam_user_policy" "cloudwatch_metrics" {
  user   = aws_iam_user.mailimport.name
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : [
          "cloudwatch:PutMetricData",
        ],
        "Resource" : "*",
        "Condition" : {
          "Bool" : {
            "aws:SecureTransport" : "true"
          }
        }
      }
    ]
  })
}
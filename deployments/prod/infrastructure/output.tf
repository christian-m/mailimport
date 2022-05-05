output "bucket_name" {
  value = local.bucket_name
}

output "dynamo_db_table_name" {
  value = module.message_lock_db.table_name
}

output "success_queue_name" {
  value = module.sqs_sucess_queue.queue_name
}

output "success_dl_queue_name" {
  value = module.sqs_sucess_queue.dl_queue_name
}

output "error_queue_name" {
  value = module.sqs_error_queue.queue_name
}

output "error_dl_queue_name" {
  value = module.sqs_error_queue.dl_queue_name
}
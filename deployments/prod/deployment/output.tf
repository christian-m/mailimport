output "rds_endpoint" {
  value = module.rds.rds_hostname
}

output "rds_port" {
  value = module.rds.rds_port
}

output "rds_username" {
  value = var.db_username
}

output "rds_password" {
  value = random_password.db_password.result
  sensitive = true
}
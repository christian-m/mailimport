module "vpc" {
  source               = "git::git@bitbucket.org:christian_m/aws_vpc.git?ref=v1.0"
  environment          = var.environment
  region               = var.region
  domain_name          = local.vpc_domain_name
  availability_zones   = local.availability_zones
  cidr                 = "10.0.0.0/16"
  private_subnets_cidr = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
  public_subnets_cidr  = ["10.0.4.0/24", "10.0.5.0/24", "10.0.6.0/24"]
  enable_dns_hostnames = true
  enable_dns_support   = true
}

module "alb" {
  source           = "git::git@bitbucket.org:christian_m/aws_alb.git?ref=v1.0.1"
  environment      = var.environment
  vpc_id           = module.vpc.vpc_id
  subnet_ids       = module.vpc.subnet_public_ids
  application_name = var.application_name
  domain_name      = local.domain_name
  domain_zone      = var.domain_name
}

resource "random_password" "db_password" {
  length           = 16
  override_special = "!#$%&*()-_=+[]{}<>:?"
  special          = true
}

module "ecs" {
  source                         = "git::git@bitbucket.org:christian_m/aws_ecs.git?ref=v1.0"
  environment                    = var.environment
  region                         = var.region
  account_id                     = var.account_id
  application_name               = var.application_name
  vpc_id                         = module.vpc.vpc_id
  subnet_ids                     = module.vpc.subnet_private_ids
  ingress_security_group_id      = module.alb.alb_security_group_id
  load_balancer_target_group_arn = module.alb.alb_target_group_arn
  container_environment          = [
    { "name" : "AWS_ACCESS_KEY", "value" : aws_iam_access_key.mailimport_user.id },
    { "name" : "AWS_SECRET_KEY", "value" : aws_iam_access_key.mailimport_user.secret },
    { "name" : "CLOUD_AWS_REGION_STATIC", "value" : var.region },
    { "name" : "SPRING_PROFILES_ACTIVE", "value" : var.environment },
    {
      "name" : "SPRING_DATASOURCE_URL",
      "value" : "jdbc:postgresql://${module.rds.rds_hostname}:${module.rds.rds_port}/postgres"
    },
    { "name" : "SPRING_DATASOURCE_USERNAME", "value" : var.db_username },
    { "name" : "SPRING_DATASOURCE_PASSWORD", "value" : random_password.db_password.result }
  ]
}

module "rds" {
  source                    = "git::git@bitbucket.org:christian_m/aws_rds_postgres.git?ref=v1.0.1"
  environment               = var.environment
  db_instance_name          = local.db_instance_name
  db_username               = var.db_username
  db_password               = random_password.db_password.result
  vpc_id                    = module.vpc.vpc_id
  subnet_ids                = module.vpc.subnet_private_ids
  ingress_security_group_id = module.ecs.service_security_group_id
}

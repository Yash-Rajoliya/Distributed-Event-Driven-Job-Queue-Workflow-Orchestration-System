variable "region" {
  type        = string
  description = "The AWS deployment region for infrastructure resources"
  default     = "ap-south-1"
}

variable "environment" {
  type        = string
  description = "Deployment environment (e.g., dev, staging, prod)"
  default     = "dev"
}

variable "project_name" {
  type        = string
  description = "Name of the project used for resource naming and tagging"
  default     = "djqueue"
}
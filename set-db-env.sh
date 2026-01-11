#!/bin/sh

# PostgreSQL Database Configuration
# Source this file with: source ./set-db-env.sh

export COFFEE_DATABASE_URL="jdbc:postgresql://c9ffqidprriprp.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com:5432/d8dcjdpish86ee"
export COFFEE_DATABASE_USERNAME="u9aggo1ma6e8qh"
export COFFEE_DATABASE_PASSWORD="p83b92055642189e3193d76e78414b6ec9850e7d4302885ff743600b83660eb13"

echo "Database environment variables set successfully!"
echo "URL: $COFFEE_DATABASE_URL"
echo "Username: $COFFEE_DATABASE_USERNAME"
echo "Password: [hidden]"

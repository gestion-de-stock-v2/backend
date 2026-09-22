#!/bin/bash
# Cree une base par service (pattern "Database per Service").
# Execute une seule fois, au premier demarrage du conteneur PostgreSQL.
set -e

for db in auth stock orders payments; do
  echo "  creation de la base '$db'"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-SQL
      CREATE DATABASE $db;
      GRANT ALL PRIVILEGES ON DATABASE $db TO $POSTGRES_USER;
SQL
done

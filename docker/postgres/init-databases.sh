#!/bin/bash
# Cree une base par service (pattern "Database per Service").
# Execute une seule fois, au premier demarrage du conteneur PostgreSQL.
set -e

for db in auth stock orders payments; do
  echo "  creation de la base '$db'"
  # --dbname est indispensable : sans lui psql se connecte a une base portant le
  # nom de l'utilisateur, qui n'existe pas, et le script echoue des la premiere base.
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "${POSTGRES_DB:-postgres}" <<-SQL
      CREATE DATABASE $db;
      GRANT ALL PRIVILEGES ON DATABASE $db TO $POSTGRES_USER;
SQL
done

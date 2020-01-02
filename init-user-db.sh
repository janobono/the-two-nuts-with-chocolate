#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER quarkus WITH PASSWORD 'quarkus';
    CREATE DATABASE quarkus;
    GRANT ALL PRIVILEGES ON DATABASE quarkus TO quarkus;
    CREATE USER springboot WITH PASSWORD 'springboot';
    CREATE DATABASE springboot;
    GRANT ALL PRIVILEGES ON DATABASE springboot TO springboot;
EOSQL

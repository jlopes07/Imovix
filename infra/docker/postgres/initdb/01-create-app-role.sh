#!/bin/sh
# =============================================================================
# Executado UMA vez, na criação do volume do PostgreSQL.
# Cria o role de aplicação com menor privilégio e o schema da aplicação.
#
#  - O superusuário (POSTGRES_USER) é reservado para administração.
#  - A API conecta com APP_DB_USER: sem SUPERUSER/CREATEDB/CREATEROLE.
#  - APP_DB_USER é dono apenas do schema da aplicação (necessário para o
#    Flyway criar tabelas). Em staging/produção, avaliar separar o usuário de
#    migração do usuário de runtime (somente DML).
# =============================================================================
set -eu

: "${APP_DB_USER:?APP_DB_USER não definido}"
: "${APP_DB_PASSWORD:?APP_DB_PASSWORD não definido}"
: "${APP_DB_SCHEMA:=imovix}"

psql -v ON_ERROR_STOP=1 \
     --username "$POSTGRES_USER" \
     --dbname "$POSTGRES_DB" \
     -v app_user="$APP_DB_USER" \
     -v app_password="$APP_DB_PASSWORD" \
     -v app_schema="$APP_DB_SCHEMA" \
	CREATE EXTENSION IF NOT EXISTS "pgcrypto";

	CREATE ROLE :"app_user" WITH LOGIN PASSWORD :'app_password'
	    NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION NOBYPASSRLS;

	-- Ninguém além dos roles explicitamente autorizados acessa o banco
	REVOKE ALL ON DATABASE :"db_name" FROM PUBLIC;
	GRANT CONNECT, TEMPORARY ON DATABASE :"db_name" TO :"app_user";

	-- Schema "public" não é usado pela aplicação
	REVOKE ALL ON SCHEMA public FROM PUBLIC;

	CREATE SCHEMA IF NOT EXISTS :"app_schema" AUTHORIZATION :"app_user";
	ALTER ROLE :"app_user" IN DATABASE :"db_name" SET search_path = :"app_schema";
	ALTER ROLE :"app_user" SET timezone = 'UTC';
EOSQL

echo "Role de aplicação '$APP_DB_USER' e schema '$APP_DB_SCHEMA' criados."

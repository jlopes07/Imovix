#!/usr/bin/env bash
# Script de Backup Automatizado do PostgreSQL (Imovix) para Linux/Staging

set -euo pipefail

CONTAINER_NAME="${1:-imovix-postgres}"
DB_USER="${2:-imovix_owner}"
DB_NAME="${3:-imovix}"
BACKUP_DIR="${4:-infra/backups}"

mkdir -p "${BACKUP_DIR}"

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/imovix_backup_${TIMESTAMP}.sql"

echo "[INFO] Iniciando backup do banco '${DB_NAME}'..."
docker exec -t "${CONTAINER_NAME}" pg_dump -U "${DB_USER}" -d "${DB_NAME}" --clean --if-exists --no-owner --no-privileges > "${BACKUP_FILE}"

if [ -s "${BACKUP_FILE}" ]; then
    echo "[SUCCESS] Backup concluido com sucesso: ${BACKUP_FILE}"
    ls -lh "${BACKUP_FILE}"
else
    echo "[ERROR] Arquivo de backup vazio gerado." >&2
    exit 1
fi

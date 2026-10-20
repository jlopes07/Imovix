#!/usr/bin/env bash
# Script de Restauracao de Backup do PostgreSQL (Imovix) para Linux/Staging

set -euo pipefail

BACKUP_FILE="${1:-}"
CONTAINER_NAME="${2:-imovix-postgres}"
DB_USER="${3:-imovix_owner}"
DB_NAME="${4:-imovix}"

if [ -z "${BACKUP_FILE}" ] || [ ! -f "${BACKUP_FILE}" ]; then
    echo "[ERROR] Arquivo de backup invalido ou nao fornecido." >&2
    echo "Uso: ./restore-db.sh <caminho_do_arquivo.sql>" >&2
    exit 1
fi

echo "[WARNING] Restaurando banco '${DB_NAME}' a partir de ${BACKUP_FILE}..."
docker exec -i "${CONTAINER_NAME}" psql -U "${DB_USER}" -d "${DB_NAME}" < "${BACKUP_FILE}"
echo "[SUCCESS] Restauracao concluida com sucesso."

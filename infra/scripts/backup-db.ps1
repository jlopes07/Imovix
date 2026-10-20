# Script de Backup Automatizado do PostgreSQL (Imovix)
# Execucao compativel com PowerShell no Windows e containers Docker.

param (
    [string]$ContainerName = "imovix-postgres",
    [string]$DbUser = "imovix_owner",
    [string]$DbName = "imovix",
    [string]$BackupDir = "infra/backups"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
}

$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$BackupFile = "$BackupDir/imovix_backup_$Timestamp.sql"

Write-Host "[INFO] Iniciando backup do banco de dados '$DbName'..."

try {
    # Executa pg_dump dentro do container com menor impacto e compressao
    docker exec -t $ContainerName pg_dump -U $DbUser -d $DbName --clean --if-exists --no-owner --no-privileges > $BackupFile

    if ((Get-Item $BackupFile).Length -gt 0) {
        Write-Host "[SUCCESS] Backup concluido com sucesso: $BackupFile"
        Write-Host "[INFO] Tamanho do backup: $((Get-Item $BackupFile).Length / 1KB) KB"
    } else {
        throw "Arquivo de backup gerado esta vazio."
    }
} catch {
    Write-Error "[ERROR] Falha durante a execucao do backup: $_"
    exit 1
}

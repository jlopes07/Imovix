# Script de Restauracao de Backup do PostgreSQL (Imovix)

param (
    [Parameter(Mandatory=$true)]
    [string]$BackupFile,
    [string]$ContainerName = "imovix-postgres",
    [string]$DbUser = "imovix_owner",
    [string]$DbName = "imovix"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $BackupFile)) {
    Write-Error "[ERROR] Arquivo de backup nao encontrado: $BackupFile"
    exit 1
}

Write-Host "[WARNING] Restaurando banco de dados '$DbName' a partir de $BackupFile..."

try {
    Get-Content $BackupFile | docker exec -i $ContainerName psql -U $DbUser -d $DbName
    Write-Host "[SUCCESS] Restauracao do banco concluida com sucesso."
} catch {
    Write-Error "[ERROR] Falha durante a restauracao: $_"
    exit 1
}

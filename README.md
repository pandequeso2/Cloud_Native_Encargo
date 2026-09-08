Contenido de env
ENTRA_TENANT_ID=551dc2ab-db79-43ed-97de-ec90a21f3e0c
ENTRA_AUDIENCE=cdde4875-2f04-4a9e-b27c-2e1ad93c9366

Paso 1 — compila los 12 módulos. Desde la raíz del repo, en PowerShell:

powershell
cd Backend
foreach ($d in "eureka-server","gateway","notas_service","asignaturas_service","profesores_service","grupos_service","integrantes_service","roles_service","secciones_service","trabajos_service","entregas_service","comentarios_service") {
    Write-Host "=== Compilando $d ===" -ForegroundColor Cyan
    Push-Location $d
    .\mvnw.cmd clean package -DskipTests
    Pop-Location
}
cd ..


Paso 2:
docker compose up -d --build
$env:SPRING_PROFILES_ACTIVE = "local"
Remove-Item Env:GCP_PROJECT_ID -ErrorAction SilentlyContinue
Remove-Item Env:GCS_BUCKET -ErrorAction SilentlyContinue
Remove-Item Env:GCS_PREFIX -ErrorAction SilentlyContinue
Write-Host "Perfil: local (archivos en ./local-data/)"
.\mvnw.cmd spring-boot:run

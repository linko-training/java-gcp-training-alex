$env:SPRING_PROFILES_ACTIVE = "cloud"
$env:GCP_PROJECT_ID = if ($env:GCP_PROJECT_ID) { $env:GCP_PROJECT_ID } else { "linko-training" }
$env:GCS_BUCKET = if ($env:GCS_BUCKET) { $env:GCS_BUCKET } else { "linko-challenge-files" }
$env:GCS_PREFIX = if ($env:GCS_PREFIX) { $env:GCS_PREFIX } else { "mid-uploads/" }
Write-Host "Perfil: cloud (GCS bucket: $env:GCS_BUCKET, prefix: $env:GCS_PREFIX)"
Write-Host "Requiere credenciales GCP: gcloud auth application-default login"
.\mvnw.cmd spring-boot:run -Pcloud

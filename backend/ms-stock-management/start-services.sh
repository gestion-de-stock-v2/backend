#!/bin/bash

SERVICES=(discovery config-server gateway customer product order payment notification)

mkdir -p logs

for s in "${SERVICES[@]}"; do
  if [ -d "services/$s" ]; then
    echo "▶ Démarrage de $s..."
    (cd "services/$s" && nohup mvn spring-boot:run > "../../logs/$s.log" 2>&1 &)
    sleep 20
  else
    echo "⚠ Dossier services/$s introuvable, ignoré"
  fi
done

echo ""
echo "✅ Tous les services sont lancés."
echo "Logs disponibles dans : logs/<nom-service>.log"

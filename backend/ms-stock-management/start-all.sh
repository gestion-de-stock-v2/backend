#!/bin/bash

# ============================================================
# Démarrage complet du backend ms-stock-management
# ============================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
SERVICES_DIR="$BASE_DIR/services"
LOG_DIR="$BASE_DIR/logs"
mkdir -p "$LOG_DIR"

echo "================================================"
echo "  Démarrage de l'infrastructure Docker"
echo "================================================"
docker-compose up -d
sleep 5

echo ""
echo "================================================"
echo "  Démarrage des microservices"
echo "================================================"

# Liste dans l'ORDRE de démarrage
SERVICES=(
  "config-server"
  "discovery"
  "gateway"
  "customer"
  "product"
  "order"
  "payment"
  "notification"
)

for SERVICE in "${SERVICES[@]}"; do
  SERVICE_PATH="$SERVICES_DIR/$SERVICE"

  if [ ! -d "$SERVICE_PATH" ]; then
    echo "⚠️  $SERVICE : dossier introuvable, ignoré"
    continue
  fi

  echo "▶  Démarrage de $SERVICE..."

  cd "$SERVICE_PATH"
  nohup mvn spring-boot:run > "$LOG_DIR/$SERVICE.log" 2>&1 &
  echo "   PID: $!  |  Log: $LOG_DIR/$SERVICE.log"

  # Attente avant le service suivant
  case "$SERVICE" in
    "config-server") sleep 15 ;;
    "discovery")     sleep 15 ;;
    "gateway")       sleep 10 ;;
    *)               sleep 5 ;;
  esac
done

echo ""
echo "================================================"
echo "  Tous les services sont lancés"
echo "================================================"
echo ""
echo "Logs : $LOG_DIR/"
echo ""
echo "Vérifier les processus :"
echo "  ps aux | grep spring-boot"
echo ""
echo "Voir un log :"
echo "  tail -f $LOG_DIR/product.log"
echo ""
echo "Arrêter tout :"
echo "  $BASE_DIR/stop-all.sh"

#!/bin/bash
cd "$(dirname "$0")"
BASE="$(pwd)"

echo "========================================"
echo "  DÉMARRAGE COMPLET DU BACKEND"
echo "========================================"

# ------------------------------------------------------------
# 1. Infrastructure Docker
# ------------------------------------------------------------
echo ""
echo "🐳 1. Docker Compose..."
cd "$BASE/ms-stock-management"
docker-compose up -d
sleep 5

# ------------------------------------------------------------
# 2. Backend standalone (EstoqueApplication) - port 8080
# ------------------------------------------------------------
echo ""
echo "🚀 2. Backend standalone (EstoqueApplication)..."
cd "$BASE"
nohup mvn spring-boot:run > "$BASE/logs/backend-standalone.log" 2>&1 &
echo "   PID: $!  |  Log: logs/backend-standalone.log"
sleep 20

# ------------------------------------------------------------
# 3. Microservices
# ------------------------------------------------------------
echo ""
echo "🏗️  3. Microservices..."
cd "$BASE/ms-stock-management"

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
  if [ -d "services/$SERVICE" ]; then
    echo "▶️  $SERVICE..."
    (cd "services/$SERVICE" && nohup mvn spring-boot:run > "$BASE/logs/$SERVICE.log" 2>&1 &)
    echo "   PID: $!  |  Log: logs/$SERVICE.log"

    case "$SERVICE" in
      "config-server") sleep 15 ;;
      "discovery")     sleep 15 ;;
      "gateway")       sleep 10 ;;
      *)               sleep 5 ;;
    esac
  else
    echo "⚠️  $SERVICE : dossier introuvable, ignoré"
  fi
done

echo ""
echo "========================================"
echo "  ✅ TOUT EST LANCÉ"
echo "========================================"
echo ""
echo "Logs : $BASE/logs/"
echo ""
echo "Arrêter : ./stop-all.sh"

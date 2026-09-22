#!/bin/bash
cd "$(dirname "$0")"
BASE="$(pwd)"
LOGS="$BASE/logs"
mkdir -p "$LOGS"

# ------------------------------------------------------------
# Nettoyage
# ------------------------------------------------------------
echo "🧹 Nettoyage des anciens processus..."
pkill -9 -f "spring-boot:run" 2>/dev/null
pkill -9 -f "EstoqueApplication" 2>/dev/null
sleep 3

for PORT in 8080 8050 8060 8070 8090 8222 8761 8888; do
  PID=$(sudo lsof -t -i :$PORT 2>/dev/null)
  [ ! -z "$PID" ] && sudo kill -9 $PID 2>/dev/null
done
sleep 2

# ------------------------------------------------------------
# 1. Docker
# ------------------------------------------------------------
echo ""
echo "🐳 1. Docker..."
(cd ms-stock-management && docker-compose up -d)
sleep 8

# ------------------------------------------------------------
# 2. Backend standalone (port 8080)
# ------------------------------------------------------------
echo ""
echo "🚀 2. Backend standalone (port 8080)..."
nohup mvn spring-boot:run > "$LOGS/backend-8080.log" 2>&1 &
echo "   PID: $!  → $LOGS/backend-8080.log"

# Attendre que le port 8080 soit UP (max 60s)
for i in {1..30}; do
  sleep 2
  if sudo lsof -i :8080 >/dev/null 2>&1; then
    echo "   ✅ Backend standalone UP"
    break
  fi
  echo "   ⏳ Attente backend... ($i/30)"
done

# ------------------------------------------------------------
# 3. Config Server
# ------------------------------------------------------------
echo ""
echo "⚙️  3. Config Server (8888)..."
cd "$BASE/ms-stock-management/services/config-server"
nohup mvn spring-boot:run > "$LOGS/config-server.log" 2>&1 &
echo "   PID: $!"

for i in {1..15}; do
  sleep 2
  if sudo lsof -i :8888 >/dev/null 2>&1; then
    echo "   ✅ Config Server UP"
    break
  fi
done

# ------------------------------------------------------------
# 4. Discovery (Eureka)
# ------------------------------------------------------------
echo ""
echo "🔍 4. Discovery (8761)..."
cd "$BASE/ms-stock-management/services/discovery"
nohup mvn spring-boot:run > "$LOGS/discovery.log" 2>&1 &
echo "   PID: $!"

for i in {1..15}; do
  sleep 2
  if sudo lsof -i :8761 >/dev/null 2>&1; then
    echo "   ✅ Discovery UP"
    break
  fi
done

# ------------------------------------------------------------
# 5. Microservices (dans l'ordre)
# ------------------------------------------------------------
echo ""
echo "🏗️  5. Microservices..."
cd "$BASE/ms-stock-management"

for s in gateway customer product order payment notification; do
  if [ -d "services/$s" ]; then
    echo "   ▶️  $s"
    (cd "services/$s" && nohup mvn spring-boot:run > "$LOGS/$s.log" 2>&1 &)
    sleep 8
  fi
done

echo ""
echo "════════════════════════════════════════"
echo "  ✅ TOUT LANCÉ"
echo "════════════════════════════════════════"
echo ""
echo "Logs : $LOGS/"
echo ""

# ------------------------------------------------------------
# 6. Vérification finale
# ------------------------------------------------------------
echo "📊 Vérification des ports :"
for PORT in 8080 8050 8060 8070 8090 8222 8761 8888; do
  if sudo lsof -i :$PORT >/dev/null 2>&1; then
    echo "   ✅ $PORT UP"
  else
    echo "   ❌ $PORT DOWN"
  fi
done

echo ""
echo "Eureka   : http://localhost:8761"
echo "Config   : http://localhost:8888"
echo "Frontend : http://localhost:4200"
echo "Arrêter  : ./stop-tout.sh"

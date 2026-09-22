#!/bin/bash
BASE="$(cd "$(dirname "$0")" && pwd)"
LOGS="$BASE/logs"
mkdir -p "$LOGS"

echo "════════════════════════════════════════"
echo "  NETTOYAGE"
echo "════════════════════════════════════════"
pkill -9 -f "spring-boot:run" 2>/dev/null
pkill -9 -f "EstoqueApplication" 2>/dev/null
pkill -9 -f "Application" 2>/dev/null
sleep 3

for PORT in 8080 8050 8060 8070 8090 8222 8761 8888; do
  PID=$(sudo lsof -t -i :$PORT 2>/dev/null)
  if [ ! -z "$PID" ]; then
    echo "  Port $PORT occupé par PID $PID → kill"
    sudo kill -9 $PID 2>/dev/null
  fi
done
sleep 2

echo ""
echo "════════════════════════════════════════"
echo "  1/3  DOCKER"
echo "════════════════════════════════════════"
cd "$BASE"
docker-compose up -d
sleep 8

echo ""
echo "════════════════════════════════════════"
echo "  2/3  CONFIG-SERVER + DISCOVERY"
echo "════════════════════════════════════════"
cd "$BASE/services/config-server"
nohup mvn spring-boot:run > "$LOGS/config-server.log" 2>&1 &
echo "  config-server lancé (PID $!)"
sleep 20

cd "$BASE/services/discovery"
nohup mvn spring-boot:run > "$LOGS/discovery.log" 2>&1 &
echo "  discovery lancé (PID $!)"
sleep 20

echo ""
echo "════════════════════════════════════════"
echo "  3/3  MICROSERVICES"
echo "════════════════════════════════════════"
for S in gateway customer product order payment notification; do
  if [ -d "$BASE/services/$S" ]; then
    cd "$BASE/services/$S"
    nohup mvn spring-boot:run > "$LOGS/$S.log" 2>&1 &
    echo "  $S lancé (PID $!)"
    sleep 10
  else
    echo "  ⚠ $S introuvable"
  fi
done

echo ""
echo "════════════════════════════════════════"
echo "  ✅ TOUT EST LANCÉ"
echo "════════════════════════════════════════"
echo ""
echo "Logs  : $LOGS/"
echo "Eureka: http://localhost:8761"
echo "Config: http://localhost:8888"
echo ""
echo "Voir les logs en direct :"
echo "  tail -f $LOGS/*.log"
echo ""
echo "Arrêter : ./stop-tout.sh"

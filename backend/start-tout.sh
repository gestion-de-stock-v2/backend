#!/bin/bash
cd "$(dirname "$0")"
BASE="$(pwd)"
mkdir -p logs

echo "🐳 1. Docker..."
(cd ms-stock-management && docker-compose up -d)
sleep 5

echo "🚀 2. Backend standalone (port 8080)..."
nohup mvn spring-boot:run > "$BASE/logs/backend-8080.log" 2>&1 &
echo "   PID: $!  → logs/backend-8080.log"
sleep 20

echo "🏗️  3. Microservices..."
cd ms-stock-management
for s in config-server discovery gateway customer product order payment notification; do
  if [ -d "services/$s" ]; then
    echo "   ▶️  $s"
    (cd "services/$s" && nohup mvn spring-boot:run > "$BASE/logs/$s.log" 2>&1 &)
    case "$s" in
      config-server|discovery) sleep 15 ;;
      gateway)                 sleep 10 ;;
      *)                       sleep 5 ;;
    esac
  fi
done

echo ""
echo "✅ TOUT LANCÉ"
echo "Logs : $BASE/logs/"

#!/bin/bash
BASE="$(cd "$(dirname "$0")" && pwd)"
mkdir -p "$BASE/logs"

echo "🐳 Docker..."
(cd "$BASE/ms-stock-management" && docker-compose up -d)
sleep 5

echo "🚀 Backend standalone (8080)..."
(cd "$BASE/standalone" && nohup mvn spring-boot:run > "$BASE/logs/standalone.log" 2>&1 &)
sleep 25

echo "🏗️  Microservices..."
for s in config-server discovery gateway customer product order payment notification; do
  if [ -d "$BASE/ms-stock-management/services/$s" ]; then
    echo "   ▶️  $s"
    (cd "$BASE/ms-stock-management/services/$s" && nohup mvn spring-boot:run > "$BASE/logs/$s.log" 2>&1 &)
    case "$s" in
      config-server|discovery) sleep 15 ;;
      gateway)                 sleep 10 ;;
      *)                       sleep 5 ;;
    esac
  fi
done

echo ""
echo "✅ Tout est lancé — logs : $BASE/logs/"

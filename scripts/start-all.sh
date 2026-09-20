#!/bin/bash

ROOT="$HOME/gestion de stock"
BACKEND_MONO="$ROOT/backend"
BACKEND_MS="$ROOT/backend/ms-stock-management"
FRONTEND="$ROOT/frontend"
LOGS="$ROOT/logs"

mkdir -p "$LOGS"

# Couleurs
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log() { echo -e "${GREEN}▶ $1${NC}"; }
warn() { echo -e "${YELLOW}⚠ $1${NC}"; }
err() { echo -e "${RED}✘ $1${NC}"; }

# Attend qu'un port soit ouvert (timeout en secondes)
wait_port() {
  local port=$1
  local timeout=${2:-90}
  local elapsed=0
  while ! nc -z localhost $port 2>/dev/null; do
    sleep 2
    elapsed=$((elapsed + 2))
    if [ $elapsed -ge $timeout ]; then
      return 1
    fi
  done
  return 0
}

# Tue les anciens processus
log "Nettoyage des anciens processus..."
pkill -9 -f "spring-boot" 2>/dev/null
pkill -9 -f "EstoqueApplication" 2>/dev/null
pkill -9 -f "ng serve" 2>/dev/null
sleep 2

# ============================================================
# 1. DOCKER (infra)
# ============================================================
log "Démarrage Docker (PostgreSQL, MongoDB, Kafka...)"
cd "$BACKEND_MS"
docker-compose up -d > "$LOGS/docker.log" 2>&1
sleep 8

if docker-compose ps | grep -q "Up"; then
  log "Docker démarré"
else
  err "Docker a échoué — voir $LOGS/docker.log"
fi

# ============================================================
# 2. DISCOVERY (Eureka)
# ============================================================
log "Démarrage Discovery (8761)..."
cd "$BACKEND_MS/services/discovery"
nohup mvn spring-boot:run > "$LOGS/discovery.log" 2>&1 &
wait_port 8761 120 && log "Discovery prêt" || err "Discovery timeout"

# ============================================================
# 3. CONFIG-SERVER
# ============================================================
log "Démarrage Config-server (8888)..."
cd "$BACKEND_MS/services/config-server"
nohup mvn spring-boot:run > "$LOGS/config-server.log" 2>&1 &
wait_port 8888 120 && log "Config-server prêt" || err "Config-server timeout"

# ============================================================
# 4. GATEWAY
# ============================================================
log "Démarrage Gateway (8222)..."
cd "$BACKEND_MS/services/gateway"
nohup mvn spring-boot:run > "$LOGS/gateway.log" 2>&1 &
wait_port 8222 120 && log "Gateway prêt" || err "Gateway timeout"

# ============================================================
# 5. MICROSERVICES MÉTIER
# ============================================================
for svc in customer:8090 product:8050 order:8070 payment:8060 notification:8040; do
  name="${svc%%:*}"
  port="${svc##*:}"
  if [ -d "$BACKEND_MS/services/$name" ]; then
    log "Démarrage $name ($port)..."
    (cd "$BACKEND_MS/services/$name" && nohup mvn spring-boot:run > "$LOGS/$name.log" 2>&1 &)
    wait_port $port 120 && log "$name prêt" || warn "$name timeout (log: $LOGS/$name.log)"
  fi
done

# ============================================================
# 6. MONOLITHE (ancien backend sur 8080)
# ============================================================
log "Démarrage Monolithe Spring Boot (8080)..."
cd "$BACKEND_MONO"
nohup mvn spring-boot:run > "$LOGS/monolith.log" 2>&1 &
wait_port 8080 120 && log "Monolithe prêt" || warn "Monolithe timeout (log: $LOGS/monolith.log)"

# ============================================================
# 7. FRONTEND ANGULAR
# ============================================================
log "Démarrage Frontend Angular (4200)..."
cd "$FRONTEND"
# Charge nvm si disponible
[ -s "$HOME/.nvm/nvm.sh" ] && source "$HOME/.nvm/nvm.sh" && nvm use 22 > /dev/null 2>&1
nohup ng serve --host 0.0.0.0 > "$LOGS/frontend.log" 2>&1 &
wait_port 4200 120 && log "Frontend prêt" || warn "Frontend timeout (log: $LOGS/frontend.log)"

# ============================================================
# RÉSUMÉ
# ============================================================
echo ""
echo "════════════════════════════════════════════════════════"
echo -e "${GREEN}✅ TOUT EST LANCÉ${NC}"
echo "════════════════════════════════════════════════════════"
echo ""
echo "🌐 Accès :"
echo "   Frontend      : http://localhost:4200"
echo "   Monolithe     : http://localhost:8080"
echo "   Gateway MS    : http://localhost:8222"
echo "   Eureka        : http://localhost:8761"
echo "   Config-server : http://localhost:8888"
echo "   pgAdmin       : http://localhost:5050"
echo "   Mongo Express : http://localhost:8081"
echo "   MailDev       : http://localhost:1080"
echo "   Zipkin        : http://localhost:9411"
echo ""
echo "📄 Logs : $LOGS/"
echo "   tail -f $LOGS/frontend.log"
echo ""
echo "🛑 Arrêter tout :"
echo "   ~/\"gestion de stock\"/scripts/stop-all.sh"
echo "════════════════════════════════════════════════════════"

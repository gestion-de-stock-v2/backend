#!/bin/bash
echo "Arrêt de tous les services..."
pkill -9 -f "spring-boot:run" 2>/dev/null
pkill -9 -f "EstoqueApplication" 2>/dev/null
pkill -9 -f "Application" 2>/dev/null
sleep 2
for PORT in 8080 8050 8060 8070 8090 8222 8761 8888; do
  PID=$(sudo lsof -t -i :$PORT 2>/dev/null)
  [ ! -z "$PID" ] && sudo kill -9 $PID 2>/dev/null
done
echo "✅ Tout est arrêté"

#!/bin/bash

echo "🛑 Arrêt de tous les services..."

# Java (Spring Boot)
pkill -9 -f "spring-boot" 2>/dev/null
pkill -9 -f "EstoqueApplication" 2>/dev/null

# Node (Angular)
pkill -9 -f "ng serve" 2>/dev/null
pkill -9 node 2>/dev/null

# Docker
cd "$HOME/gestion de stock/backend/ms-stock-management"
docker-compose down 2>/dev/null

echo "✅ Tout est arrêté."

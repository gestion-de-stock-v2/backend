#!/bin/bash

echo "Arrêt de tous les microservices Spring Boot..."
pkill -f "spring-boot:run"
pkill -f "spring-boot"
pkill -f "mvn.*spring-boot"
pkill -f ".*Application"

sleep 3

echo ""
echo "Processus restants :"
ps aux | grep -E "spring-boot|mvn" | grep -v grep || echo "Aucun ✓"

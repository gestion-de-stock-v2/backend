#!/bin/bash
echo "Arrêt de tous les services Spring Boot..."
pkill -9 -f "spring-boot:run"
pkill -9 -f "EstoqueApplication"
pkill -9 -f "Application"
pkill -9 -f "mvn.*spring-boot"
echo "✅ Terminé"

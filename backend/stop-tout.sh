#!/bin/bash
pkill -9 -f "spring-boot:run"
pkill -9 -f "mvn.*spring-boot"
echo "✅ Tout arrêté"

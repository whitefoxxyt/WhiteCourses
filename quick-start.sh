#!/bin/bash

# Script de démarrage - Backend + Tests + SonarQube
# Version optimisée sans WASM

set -e

GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "========================================="
echo "  AppCourse - Démarrage"
echo "  Backend + Tests + SonarQube"
echo "========================================="
echo ""

# Détecter la commande Docker Compose
if command -v docker &> /dev/null && docker compose version &> /dev/null 2>&1; then
    DOCKER_COMPOSE="docker compose"
elif command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
else
    echo "❌ Docker Compose non trouvé"
    exit 1
fi

echo -e "${BLUE}[1/4]${NC} Vérification de l'environnement..."
if [ ! -f .env ]; then
    if [ -f .env.example ]; then
        cp .env.example .env
        echo -e "${GREEN}✓${NC} Fichier .env créé"
    fi
fi

echo -e "${BLUE}[2/4]${NC} Construction des images Docker..."
echo -e "${YELLOW}⏳ Cela prendra 2-3 minutes...${NC}"
$DOCKER_COMPOSE build --parallel

echo ""
echo -e "${BLUE}[3/4]${NC} Démarrage des services..."
$DOCKER_COMPOSE up -d

echo ""
echo -e "${BLUE}[4/4]${NC} Attente de la disponibilité des services..."
echo -e "${YELLOW}⏳ MySQL démarre...${NC}"
sleep 10

echo ""
echo -e "${GREEN}========================================="
echo "✅ Services démarrés avec succès !"
echo "=========================================${NC}"
echo ""
echo -e "${GREEN}Services disponibles :${NC}"
echo "  • Backend API    : http://localhost:8080"
echo "  • SonarQube      : http://localhost:9000 (admin/admin)"
echo ""
echo -e "${GREEN}Commandes utiles :${NC}"
echo "  • Tests          : ./gradlew :server:test"
echo "  • Coverage       : ./gradlew :server:jacocoTestReport"
echo "  • Analyse Sonar  : ./analyze.sh"
echo "  • Logs           : $DOCKER_COMPOSE logs -f"
echo "  • Arrêter        : $DOCKER_COMPOSE down"
echo ""
echo -e "${GREEN}Vérifier les services :${NC}"
$DOCKER_COMPOSE ps
echo ""

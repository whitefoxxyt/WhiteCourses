#!/bin/bash

# Script pour lancer les tests et l'analyse SonarQube
# Usage: ./analyze.sh [sonar-token]

set -e

echo "========================================="
echo "AppCourse - Tests & SonarQube Analysis"
echo "========================================="

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if SonarQube is running
echo -e "${YELLOW}Checking SonarQube connection...${NC}"
SONAR_URL="${SONAR_HOST_URL:-http://localhost:9000}"
if curl -s -o /dev/null -w "%{http_code}" "$SONAR_URL/api/system/status" | grep -q "200"; then
    echo -e "${GREEN}✓ SonarQube is running${NC}"
else
    echo -e "${RED}✗ SonarQube is not accessible at $SONAR_URL${NC}"
    echo "Please start SonarQube with: docker-compose up -d sonarqube"
    exit 1
fi

# Run tests
echo -e "\n${YELLOW}Running backend tests...${NC}"
./gradlew :server:test --no-daemon

echo -e "\n${YELLOW}Generating JaCoCo coverage report...${NC}"
./gradlew :server:jacocoTestReport --no-daemon

echo -e "\n${YELLOW}Running frontend tests...${NC}"
./gradlew :composeApp:allTests --no-daemon || true

# Check if coverage report exists
if [ -f "server/build/reports/jacoco/test/jacocoTestReport.xml" ]; then
    echo -e "${GREEN}✓ Coverage report generated${NC}"
else
    echo -e "${RED}✗ Coverage report not found${NC}"
fi

# Run SonarQube analysis
echo -e "\n${YELLOW}Running SonarQube analysis...${NC}"

if [ -n "$1" ]; then
    export SONAR_TOKEN=$1
fi

if [ -z "$SONAR_TOKEN" ]; then
    echo -e "${RED}Warning: SONAR_TOKEN not set. Using default (may fail on production SonarQube)${NC}"
    ./gradlew sonar --no-daemon -Dsonar.host.url="$SONAR_URL" || true
else
    ./gradlew sonar --no-daemon -Dsonar.host.url="$SONAR_URL" -Dsonar.token="$SONAR_TOKEN"
fi

echo -e "\n${GREEN}=========================================${NC}"
echo -e "${GREEN}Analysis complete!${NC}"
echo -e "${GREEN}View results at: $SONAR_URL${NC}"
echo -e "${GREEN}=========================================${NC}"

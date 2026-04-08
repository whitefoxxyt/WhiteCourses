# Guide Docker, Tests & SonarQube

Ce guide explique comment utiliser Docker pour lancer l'application complète, exécuter les tests et analyser la qualité du code avec SonarQube.

## 📋 Table des matières

- [Prérequis](#prérequis)
- [Configuration Docker](#configuration-docker)
- [Démarrage rapide](#démarrage-rapide)
- [Tests](#tests)
- [SonarQube](#sonarqube)
- [Commandes utiles](#commandes-utiles)
- [Troubleshooting](#troubleshooting)

## 🔧 Prérequis

- Docker 20.10+
- Docker Compose 2.0+
- Gradle 8.5+ (pour les builds locaux)
- JDK 17+ (pour les builds locaux)

## 🐳 Configuration Docker

### Structure des services

Le projet utilise Docker Compose pour orchestrer plusieurs services :

```yaml
services:
  mysql          # Base de données MySQL 8.0
  server         # Backend Ktor (API REST)
  webapp         # Frontend Web (Compose Multiplatform - WASM)
  sonarqube-db   # PostgreSQL pour SonarQube
  sonarqube      # Analyse de code et qualité
```

### Fichiers Docker

- `docker-compose.yml` : Configuration des services
- `server/Dockerfile` : Image du backend Ktor
- `composeApp/Dockerfile` : Image du frontend web
- `.env.example` : Variables d'environnement (à copier vers `.env`)

### Configuration des variables d'environnement

```bash
cp .env.example .env
```

Modifiez `.env` selon vos besoins :

```env
DB_NAME=MaListe
DB_USER=appcourse
DB_PASSWORD=appcourse_password
SERVER_PORT=8080
```

## 🚀 Démarrage rapide

### 1. Démarrer tous les services

```bash
# Si vous avez Docker Compose v2+ (intégré à Docker)
docker compose up -d

# Si vous avez l'ancien docker-compose standalone
docker-compose up -d
```

Cette commande démarre :
- MySQL sur le port 3306
- Backend server sur le port 8080
- Frontend webapp sur le port 3000
- SonarQube sur le port 9000

### 2. Vérifier le statut des services

```bash
# Docker Compose v2+
docker compose ps

# Ancien docker-compose
docker-compose ps
```

### 3. Accéder aux services

- **Frontend Web** : http://localhost:3000
- **Backend API** : http://localhost:8080
- **SonarQube** : http://localhost:9000 (admin/admin par défaut)

### 4. Vérifier les logs

```bash
# Tous les services (Docker Compose v2+)
docker compose logs -f

# Tous les services (ancien)
docker-compose logs -f

# Un service spécifique
docker compose logs -f server
docker compose logs -f webapp
```

### 5. Arrêter les services

```bash
# Arrêter sans supprimer les volumes (Docker Compose v2+)
docker compose down

# Arrêter et supprimer les volumes (perte des données)
docker compose down -v

# Si vous utilisez l'ancien docker-compose
docker-compose down
docker-compose down -v
```

## 🧪 Tests

Le projet inclut des tests pour le backend et le frontend avec reporting de couverture via JaCoCo.

### Backend Tests

#### Exécuter tous les tests backend

```bash
./gradlew :server:test
```

#### Générer le rapport de couverture

```bash
./gradlew :server:jacocoTestReport
```

Le rapport est disponible dans :
- XML : `server/build/reports/jacoco/test/jacocoTestReport.xml`
- HTML : `server/build/reports/jacoco/test/html/index.html`

#### Vérifier la couverture minimale

```bash
./gradlew :server:jacocoTestCoverageVerification
```

Configuration actuelle : minimum 70% de couverture

#### Tests inclus

- **Repository Tests** : Tests unitaires avec H2 in-memory database
  - `JdbcProduitRepositoryTest` : Tests des opérations CRUD
  
- **Service Tests** : Tests unitaires avec mocks (MockK)
  - `ListeServiceTest` : Tests de la logique métier
  
- **Controller Tests** : Tests d'intégration des endpoints
  - `ListeRoutesTest` : Tests GET endpoints
  - `ListePatchRoutesTest` : Tests PATCH endpoints
  
- **Utility Tests**
  - `ExtensionsTest` : Tests des extensions Kotlin
  - `ObservabilityTest` : Tests de monitoring

### Frontend Tests

#### Exécuter les tests frontend

```bash
# Tous les tests multiplateforme
./gradlew :composeApp:allTests

# Tests communs uniquement
./gradlew :composeApp:cleanAllTests :composeApp:allTests
```

#### Tests inclus

- **Common Tests** : Tests partagés entre toutes les plateformes
  - `ComposeAppCommonTest` : Tests de base
  - `ui/AppTest` : Tests des composants UI

### Exécuter tous les tests du projet

```bash
./gradlew test
```

## 📊 SonarQube

SonarQube analyse la qualité du code, détecte les bugs, vulnérabilités et code smells, et mesure la couverture de code.

### Configuration initiale

1. **Démarrer SonarQube**

```bash
docker-compose up -d sonarqube sonarqube-db
```

2. **Accéder à SonarQube**

- URL : http://localhost:9000
- Login par défaut : `admin` / `admin`
- Changez le mot de passe au premier login

3. **Créer un token**

- Aller dans : Account → Security → Generate Tokens
- Nom : `appcourse-local`
- Type : `Global Analysis Token`
- Copier le token généré

### Analyse du code

#### Méthode 1 : Script automatisé

```bash
./analyze.sh [SONAR_TOKEN]
```

Ce script :
1. Vérifie que SonarQube est accessible
2. Exécute tous les tests
3. Génère les rapports de couverture
4. Lance l'analyse SonarQube
5. Affiche le lien vers les résultats

#### Méthode 2 : Commandes manuelles

```bash
# 1. Exécuter les tests
./gradlew :server:test

# 2. Générer la couverture
./gradlew :server:jacocoTestReport

# 3. Lancer l'analyse
./gradlew sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN_HERE
```

#### Méthode 3 : Variables d'environnement

```bash
export SONAR_HOST_URL=http://localhost:9000
export SONAR_TOKEN=your_token_here

./gradlew sonar
```

### Configuration SonarQube

#### Quality Gates personnalisées

La configuration par défaut impose :
- Couverture minimale : 70%
- Pas de bugs bloquants
- Pas de vulnérabilités
- Rating de maintenabilité : A

Modifiez dans `build.gradle.kts` (server) :

```kotlin
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal() // 80%
            }
        }
    }
}
```

#### Fichiers de configuration

- `sonar-project.properties` : Configuration globale du projet
- `build.gradle.kts` : Plugin SonarQube et paramètres

### Visualiser les résultats

Après l'analyse, accédez à http://localhost:9000 pour voir :

- **Overview** : Vue d'ensemble de la qualité du code
- **Issues** : Bugs, vulnérabilités, code smells détectés
- **Measures** : Métriques détaillées (complexité, duplication, etc.)
- **Coverage** : Couverture de code par fichier

## 🛠 Commandes utiles

### Docker

**Note:** Utilisez `docker compose` (v2+) ou `docker-compose` (v1) selon votre version.

```bash
# Rebuild les images après modification du code
docker compose build
# ou
docker-compose build

# Rebuild et redémarrer un service spécifique
docker compose up -d --build server

# Voir les ressources utilisées
docker stats

# Nettoyer les images inutilisées
docker system prune -a

# Accéder à un container
docker compose exec server sh
docker compose exec mysql mysql -u root -p

# Voir les variables d'environnement d'un service
docker compose exec server env
```

### Gradle

```bash
# Clean build
./gradlew clean build

# Build sans tests
./gradlew build -x test

# Lister toutes les tâches disponibles
./gradlew tasks --all

# Vérifier les dépendances
./gradlew dependencies

# Mettre à jour les dépendances
./gradlew dependencyUpdates
```

### Base de données

```bash
# Se connecter à MySQL dans Docker
docker compose exec mysql mysql -u appcourse -p MaListe
# ou
docker-compose exec mysql mysql -u appcourse -p MaListe

# Exécuter un script SQL
docker compose exec -T mysql mysql -u appcourse -p MaListe < script.sql

# Dump de la base de données
docker compose exec mysql mysqldump -u appcourse -p MaListe > backup.sql

# Restaurer une base de données
docker compose exec -T mysql mysql -u appcourse -p MaListe < backup.sql
```

## 🔍 Troubleshooting

### Problème : Le serveur ne démarre pas

**Symptômes** : `server` container exit immédiatement

**Solutions** :
1. Vérifier les logs : 
   ```bash
   docker compose logs server
   # ou
   docker-compose logs server
   ```
2. Vérifier que MySQL est démarré : 
   ```bash
   docker compose ps mysql
   ```
3. Attendre que MySQL soit prêt (healthcheck)
4. Vérifier la connexion DB dans les variables d'environnement

### Problème : Tests échouent localement

**Solutions** :
1. Nettoyer les builds : `./gradlew clean`
2. Vérifier les dépendances : `./gradlew dependencies --refresh-dependencies`
3. Vérifier la version JDK : `java -version` (doit être 17+)

### Problème : Port déjà utilisé

**Symptômes** : `Error: bind: address already in use`

**Solutions** :
```bash
# Trouver le processus utilisant le port
lsof -i :8080
# ou
netstat -tulpn | grep 8080

# Modifier le port dans docker-compose.yml
ports:
  - "8081:8080"  # Utiliser 8081 au lieu de 8080
```

### Problème : SonarQube ne démarre pas

**Symptômes** : `max virtual memory areas vm.max_map_count [65530] is too low`

**Solution** (Linux) :
```bash
sudo sysctl -w vm.max_map_count=262144

# Permanent
echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf
```

### Problème : Couverture de code non visible dans SonarQube

**Solutions** :
1. Vérifier que le rapport JaCoCo existe :
   ```bash
   ls -la server/build/reports/jacoco/test/jacocoTestReport.xml
   ```
2. Exécuter les tests avant l'analyse :
   ```bash
   ./gradlew :server:test :server:jacocoTestReport sonar
   ```
3. Vérifier le chemin dans `sonar-project.properties`

### Problème : Build Docker très lent

**Solutions** :
1. Utiliser le cache Docker :
   ```bash
   docker compose build --parallel
   # ou
   docker-compose build --parallel
   ```
2. Créer un `.dockerignore` (déjà présent) :
   ```
   .git
   .gradle
   build
   .idea
   *.iml
   ```
3. Utiliser un registry local pour les images de base

### Problème : Build WebApp échoue avec "kotlinWasmToolingSetup" ou "yarn"

**Cause** : Le build WASM nécessite Node.js et Yarn qui ne sont pas installés dans l'image Docker.

**Solution** : Le Dockerfile a été mis à jour pour installer Node.js 20 et Yarn automatiquement.

Si le problème persiste :
```bash
# Reconstruire l'image sans cache
docker compose build --no-cache webapp

# Ou tester le build localement d'abord
./gradlew :composeApp:wasmJsBrowserDistribution
```

**Note** : Le premier build peut prendre 5-10 minutes car il télécharge toutes les dépendances npm/yarn.

### Problème : "docker-compose: commande introuvable"

**Cause** : Vous avez Docker Compose v2+ intégré à Docker.

**Solution** :
```bash
# Utilisez "docker compose" au lieu de "docker-compose"
docker compose up -d
docker compose ps
docker compose logs -f

# Ou créez un alias
echo 'alias docker-compose="docker compose"' >> ~/.bashrc
source ~/.bashrc
```

### Problème : Le build prend trop de temps

**Cause** : Le premier build télécharge toutes les dépendances (Gradle, npm, images Docker).

**Optimisations** :
1. **Attendez la fin du premier build** (peut prendre 10-15 minutes)
2. Les builds suivants seront beaucoup plus rapides grâce au cache Docker
3. Pour accélérer, buildez d'abord localement :
   ```bash
   # Build backend
   ./gradlew :server:build -x test
   
   # Build frontend web (optionnel, prend du temps)
   # ./gradlew :composeApp:wasmJsBrowserDistribution
   
   # Puis lancez Docker (utilisera le cache)
   docker compose up -d
   ```

### Problème : Frontend Web ne charge pas

**Solutions** :
1. Vérifier les logs : 
   ```bash
   docker compose logs webapp
   ```
2. Vérifier le build WASM : 
   ```bash
   ./gradlew :composeApp:wasmJsBrowserDistribution
   ```
3. Vérifier la configuration Nginx dans `composeApp/nginx.conf`
4. Vérifier les CORS dans les logs du navigateur

## 📝 Note sur Docker Compose

Le projet supporte **les deux versions** de Docker Compose :

- **Docker Compose v2+** (intégré) : `docker compose` 
- **Docker Compose v1** (standalone) : `docker-compose`

Le script `quick-start.sh` détecte automatiquement la version disponible.

Si vous rencontrez l'erreur "docker-compose: commande introuvable", utilisez simplement `docker compose` (sans tiret).

## 📚 Ressources supplémentaires

- [Documentation Docker Compose](https://docs.docker.com/compose/)
- [Documentation Ktor](https://ktor.io/)
- [Documentation Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Documentation SonarQube](https://docs.sonarqube.org/)
- [Documentation JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/)

## 🔐 Sécurité

### Production

**Ne jamais** utiliser les credentials par défaut en production :

1. Changer tous les mots de passe dans `.env`
2. Utiliser des secrets Docker/Kubernetes
3. Activer SSL/TLS
4. Configurer les firewalls appropriés
5. Utiliser des images minimales (alpine)
6. Scanner les vulnérabilités : `docker scan IMAGE_NAME`

### Variables sensibles

Ajouter à `.gitignore` :
```
.env
*.key
*.pem
secrets/
```

## 📄 License

Ce projet est sous licence MIT.

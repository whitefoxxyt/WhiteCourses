# 🔧 Résolution : Port déjà utilisé

## Erreur rencontrée

```
failed to bind host port 0.0.0.0:3306/tcp: address already in use
```

## Cause

Un service MySQL est déjà en cours d'exécution sur le port 3306.

## Solutions

### Option 1 : Utiliser des ports différents (Recommandé)

Créez un fichier `.env` avec des ports personnalisés :

```bash
# Créer/modifier le fichier .env
cat > .env << 'EOF'
DB_NAME=MaListe
DB_USER=appcourse
DB_PASSWORD=appcourse_password
SERVER_PORT=8080

# Ports personnalisés si les ports par défaut sont occupés
MYSQL_PORT=3307
SERVER_PORT_HOST=8081
WEBAPP_PORT=3001
SONAR_PORT=9001
EOF
```

Puis modifiez `docker-compose.yml` pour utiliser ces variables :

```yaml
services:
  mysql:
    ports:
      - "${MYSQL_PORT:-3306}:3306"
  
  server:
    ports:
      - "${SERVER_PORT_HOST:-8080}:8080"
  
  webapp:
    ports:
      - "${WEBAPP_PORT:-3000}:80"
  
  sonarqube:
    ports:
      - "${SONAR_PORT:-9000}:9000"
```

### Option 2 : Arrêter le MySQL local

Si vous n'en avez pas besoin :

```bash
# Ubuntu/Debian
sudo systemctl stop mysql
sudo systemctl disable mysql  # Pour empêcher le démarrage automatique

# macOS
brew services stop mysql

# Puis relancer Docker
docker compose up -d mysql server
```

### Option 3 : Utiliser uniquement l'IP locale

Si vous voulez garder les deux MySQL :

Modifiez `docker-compose.yml` :

```yaml
services:
  mysql:
    ports:
      - "127.0.0.1:3307:3306"  # MySQL Docker sur port 3307
```

Puis :

```bash
docker compose up -d
```

Accès :
- MySQL local : `localhost:3306`
- MySQL Docker : `localhost:3307`

### Option 4 : Ne pas exposer le port MySQL

Si vous n'avez besoin d'accéder à MySQL que depuis le container `server` :

Modifiez `docker-compose.yml` et **supprimez** la ligne `ports` pour mysql :

```yaml
services:
  mysql:
    # Supprimez ou commentez cette ligne :
    # ports:
    #   - "3306:3306"
```

Le serveur Docker pourra toujours y accéder via le réseau interne `appcourse-network`.

## Solution rapide pour tester maintenant

```bash
# Arrêter MySQL local temporairement
sudo systemctl stop mysql

# Démarrer les services Docker
docker compose up -d mysql server

# Vérifier
docker compose ps
curl http://localhost:8080/health
```

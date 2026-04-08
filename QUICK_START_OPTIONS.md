# 🚀 Guide rapide : Démarrage sans le build WebApp dans Docker

Le build WASM dans Docker peut prendre 10-15 minutes la première fois. Voici deux options :

## Option 1 : Build local puis Docker (RECOMMANDÉ - Plus rapide)

### Étape 1 : Builder localement
```bash
./build-webapp-local.sh
```

### Étape 2 : Démarrer avec docker-compose simplifié
```bash
docker compose -f docker-compose.simple.yml up -d
```

**Avantage** : Le build WASM se fait localement (plus rapide avec le cache Gradle)

---

## Option 2 : Build complet dans Docker (Plus lent mais autonome)

```bash
docker compose up -d
```

**Note** : Le premier build peut prendre 10-15 minutes.

---

## Option 3 : Uniquement Backend + SonarQube (Sans frontend Web)

Si vous n'avez besoin que du backend :

```bash
docker compose up -d mysql server sonarqube sonarqube-db
```

Accès :
- Backend API : http://localhost:8080
- SonarQube : http://localhost:9000

---

## Comparaison

| Option | Temps 1er build | Autonomie | Recommandation |
|--------|----------------|-----------|----------------|
| Build local + Docker simple | ~3-5 min | ⚠️ Nécessite Gradle local | ⭐ **Recommandé** |
| Build complet Docker | ~10-15 min | ✅ Totalement autonome | Pour CI/CD |
| Backend uniquement | ~2-3 min | N/A | Tests backend |

---

## Dépannage

### Le build local échoue
```bash
# Vérifier Node.js
node --version  # Doit être 16+

# Si Node.js n'est pas installé
# Ubuntu/Debian :
sudo apt-get update
sudo apt-get install nodejs npm

# macOS :
brew install node

# Puis réessayer
./build-webapp-local.sh
```

### Le Dockerfile échoue toujours
Le Dockerfile a été mis à jour pour installer Node.js 20 automatiquement.

Si le problème persiste :
```bash
# Nettoyer et rebuilder
docker compose down
docker compose build --no-cache
docker compose up -d
```

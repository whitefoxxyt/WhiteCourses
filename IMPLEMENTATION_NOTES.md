# Notes d'implémentation Docker

## Problème rencontré : Build WASM dans Docker

### Le problème
Le build WASM (`wasmJsBrowserDistribution`) nécessite Node.js et Yarn, qui doivent être installés dans l'image Docker. De plus, le build initial télécharge beaucoup de dépendances npm/yarn, ce qui rend le premier build très long (10-15 minutes).

### Solutions implémentées

#### 1. Dockerfile complet (autonome)
`composeApp/Dockerfile` - Installe Node.js 20 et Yarn automatiquement
- ✅ Totalement autonome
- ⚠️ Build lent (10-15 min la première fois)
- Usage: `docker compose up -d`

#### 2. Dockerfile simple (build local)
`composeApp/Dockerfile.simple` - Utilise les fichiers pré-buildés
- ✅ Build rapide (2-3 min)
- ⚠️ Nécessite Gradle + Node.js localement
- Usage: `./build-webapp-local.sh` puis `docker compose -f docker-compose.simple.yml up -d`

#### 3. Backend uniquement
- ✅ Très rapide (2-3 min)
- ℹ️ Pas de frontend Web
- Usage: `docker compose up -d mysql server sonarqube sonarqube-db`

### Recommandation

Pour le développement local : **Option 2** (build local + Docker simple)
Pour CI/CD / Production : **Option 1** (Dockerfile complet)
Pour tests backend : **Option 3** (backend uniquement)

### Fichiers créés

- `build-webapp-local.sh` - Script de build local
- `docker-compose.simple.yml` - Docker Compose sans build WASM
- `composeApp/Dockerfile.simple` - Dockerfile pour pré-build
- `QUICK_START_OPTIONS.md` - Guide des options de démarrage

### Améliorations futures possibles

1. **Multi-stage caching avancé** : Séparer le téléchargement des dépendances npm
2. **Registry de cache** : Utiliser un registry Docker local pour les layers
3. **CI pre-build** : Builder les images en CI et les pousser sur un registry
4. **Development vs Production** : Deux Dockerfiles séparés

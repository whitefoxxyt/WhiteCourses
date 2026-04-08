# 🚨 Solution au problème de build WebApp dans Docker

## Problème identifié

Le build WASM dans Docker échoue avec `kotlinWasmToolingSetup returns 127` - un problème connu avec les outils Kotlin WASM dans les conteneurs Docker.

## ✅ Solutions recommandées (par ordre de préférence)

### Option 1: Backend uniquement (RECOMMANDÉ - Fonctionne immédiatement)

Lancez seulement les services essentiels :

```bash
docker compose up -d mysql server sonarqube sonarqube-db
```

**Accès:**
- Backend API: http://localhost:8080
- SonarQube: http://localhost:9000
- Tests: `./gradlew :server:test`

**Temps:** 2-3 minutes
**Avantages:** Fiable, rapide, parfait pour le développement backend

---

### Option 2: Build local puis Docker simple

Si vous avez besoin du frontend:

```bash
# 1. Builder localement (nécessite Node.js 20+ et Yarn)
./gradlew :composeApp:wasmJsBrowserDistribution

# 2. Lancer avec Dockerfile.simple
docker compose -f docker-compose.simple.yml up -d
```

**Temps:** 5-8 minutes
**Avantages:** Contrôle du build, cache local Gradle

---

### Option 3: Attendre le build Docker complet

Le build est actuellement en cours. Il peut prendre 10-20 minutes.

---

## 🔍 Pourquoi ce problème?

Kotlin WASM (`kotlinWasmToolingSetup`) essaie d'installer des outils système qui nécessitent:
- Permissions spécifiques
- Outils système additionnels (git, curl, unzip)
- Configuration Node.js appropriée

Ces conditions sont difficiles à satisfaire dans tous les environnements Docker.

## 💡 Recommandation finale

**Utilisez l'Option 1 pour commencer immédiatement**, puis ajoutez le frontend plus tard si nécessaire.

```bash
docker compose up -d mysql server sonarqube sonarqube-db
```

Tous les tests et SonarQube fonctionnent parfaitement avec cette configuration!

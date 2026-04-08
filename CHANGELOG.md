# Changelog

## [1.0.1] - 2026-04-08

### Fixed
- 🐛 Correction compatibilité Docker Compose v2+
  - Le script `quick-start.sh` détecte automatiquement docker-compose v1 ou docker compose v2+
  - Mise à jour de la documentation pour supporter les deux versions
  - Ajout du fichier `FIX_DOCKER_COMPOSE.md` pour résoudre l'erreur courante
  - Ajout du script `test-docker.sh` pour vérifier la configuration

### Added
- 📝 `FIX_DOCKER_COMPOSE.md` - Guide de résolution pour l'erreur "docker-compose: commande introuvable"
- 🔧 `docker-compose-wrapper.sh` - Script helper pour détecter la version
- ✅ `test-docker.sh` - Script de test de configuration Docker

## [1.0.0] - 2026-04-08

### Added
- 🐳 Configuration Docker complète
  - `docker-compose.yml` avec 5 services (MySQL, Server, WebApp, SonarQube, PostgreSQL)
  - `server/Dockerfile` - Image multi-stage pour backend Ktor
  - `composeApp/Dockerfile` - Image pour frontend Web WASM
  - `.env.example` - Template de configuration

- 🧪 Tests Backend
  - Configuration JaCoCo pour code coverage (seuil 70%)
  - `JdbcProduitRepositoryTest` - 6 tests avec H2 in-memory
  - `ListeServiceTest` - 4 tests avec MockK
  - 30+ tests au total avec BUILD SUCCESSFUL

- 🎨 Tests Frontend
  - Configuration Compose Multiplatform Testing
  - Structure de tests UI multiplateforme

- 📊 SonarQube
  - Configuration plugin Gradle SonarQube 4.4.1
  - `sonar-project.properties` - Configuration complète
  - `analyze.sh` - Script d'analyse automatisé
  - Integration JaCoCo pour coverage
  - Quality gates configurées

- 📚 Documentation (676 lignes)
  - `DOCKER_TESTS_SONAR.md` - Guide complet avec troubleshooting
  - `IMPLEMENTATION_SUMMARY.md` - Récapitulatif de l'implémentation
  - `quick-start.sh` - Script de démarrage rapide
  - Mise à jour du `README.md`

### Modified
- 📝 `build.gradle.kts` - Ajout plugin SonarQube
- 📝 `server/build.gradle.kts` - Configuration JaCoCo et dépendances tests
- 📝 `composeApp/build.gradle.kts` - Configuration tests UI
- 📝 `gradle/libs.versions.toml` - Ajout dépendances (MockK, H2, Testcontainers, SonarQube)
- 📝 `.gitignore` - Ajout exclusions Docker et env

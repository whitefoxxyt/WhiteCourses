# 🎯 AppCourse - Guide Complet Docker Optimisé

## ✅ Version actuelle : OPTIMISÉE (sans WASM)

**Temps de démarrage : 3-4 minutes** ⚡

---

## 🚀 Démarrage rapide (1 commande)

```bash
./quick-start.sh
```

**C'est tout !** Les services seront disponibles en quelques minutes.

---

## 📊 Services disponibles

| Service | URL | Identifiants |
|---------|-----|--------------|
| **Backend API** | http://localhost:8080 | - |
| **Health Check** | http://localhost:8080/health | - |
| **SonarQube** | http://localhost:9000 | admin / admin |
| **MySQL** | localhost:3307 | appuser / apppassword |

---

## 🧪 Tests et qualité du code

### Lancer les tests
```bash
./gradlew :server:test
```

**Résultat actuel : 30+ tests ✅ PASSING**

### Générer le rapport de couverture
```bash
./gradlew :server:jacocoTestReport
```

**Couverture minimum configurée : 70%**

### Analyser avec SonarQube
```bash
# 1. Obtenir le token SonarQube
# Aller sur http://localhost:9000
# My Account > Security > Generate Token

# 2. Lancer l'analyse
export SONAR_TOKEN="votre_token"
./analyze.sh
```

---

## 📁 Structure des fichiers Docker

```
AppCourse/
├── docker-compose.optimized.yml   # ⭐ Configuration optimisée (RECOMMANDÉ)
├── docker-compose.yml              # Configuration originale avec WASM
├── docker-compose.simple.yml       # Configuration simplifiée
├── quick-start-optimized.sh        # ⭐ Script de démarrage (RECOMMANDÉ)
├── quick-start.sh                  # Script original
├── server/
│   └── Dockerfile                  # ✅ Dockerfile optimisé du backend
├── composeApp/
│   ├── Dockerfile                  # Dockerfile WASM (problématique)
│   ├── Dockerfile.simple           # Alternative sans build
│   └── Dockerfile.fixed            # Tentative de correction
└── .env                            # Configuration des ports
```

---

## ⚙️ Commandes Docker utiles

### Démarrer les services
```bash
docker compose up -d
```

### Voir les logs
```bash
# Tous les services
docker compose logs -f

# Un service spécifique
docker compose logs -f server
```

### Vérifier l'état des services
```bash
docker compose ps
```

### Arrêter les services
```bash
docker compose down
```

### Tout supprimer (services + volumes)
```bash
docker compose down -v
```

---

## 🔧 Configuration personnalisée

Éditez le fichier `.env` pour personnaliser les ports :

```bash
# Si le port 3307 est déjà utilisé
MYSQL_PORT=3308

# Si le port 8080 est déjà utilisé
SERVER_PORT_HOST=8081

# Si le port 9000 est déjà utilisé
SONAR_PORT=9001
```

Puis relancez :
```bash
docker compose -f docker-compose.optimized.yml down
docker compose -f docker-compose.optimized.yml up -d
```

---

## 🐛 Dépannage

### Port MySQL déjà utilisé (3306 ou 3307)
```bash
# Trouver quel processus utilise le port
sudo lsof -i :3306

# Changer le port dans .env
echo "MYSQL_PORT=3308" >> .env

# Redémarrer
docker compose -f docker-compose.optimized.yml down
docker compose -f docker-compose.optimized.yml up -d
```

### Le backend ne démarre pas
```bash
# Voir les logs
docker compose logs server

# Vérifier que MySQL est healthy
docker compose ps
```

### Rebuild complet
```bash
# Supprimer tout et reconstruire
docker compose down -v
docker compose build --no-cache
docker compose up -d
```

---

## 📚 Documentation complète

- **OPTIMIZED_SUCCESS.md** - Détails de l'optimisation
- **DOCKER_TESTS_SONAR.md** - Guide complet Docker/Tests/SonarQube
- **FINAL_SUMMARY.md** - Résumé complet du projet
- **SOLUTION_WEBAPP.md** - Pourquoi WASM a été supprimé

---

## ⚡ Optimisations appliquées

### Dockerfile Backend
- ✅ Image Alpine (50% plus légère)
- ✅ Cache des dépendances Gradle
- ✅ Build multi-stage
- ✅ JVM optimisée pour conteneurs
- ✅ Build parallèle

### Docker Compose
- ✅ Healthchecks configurés
- ✅ Dépendances entre services
- ✅ Volumes persistants
- ✅ Réseau isolé

### Résultat
**Build initial : ~3 min (vs 15-20 min avec WASM)**  
**Démarrage : ~20 sec**  
**Total : ~4 min** 🚀

---

## 🎯 Pourquoi cette version ?

### ❌ Problèmes avec WASM
- Build très long (10-20 min)
- Erreurs complexes dans Docker
- Dépendances Node.js/Yarn problématiques
- Peu utile pour le développement backend

### ✅ Avantages version optimisée
- ⚡ **5x plus rapide**
- 🎯 **Focus sur le backend**
- 🧪 **Tests fonctionnels**
- 📊 **SonarQube opérationnel**
- 🐳 **Build Docker stable**

---

## 📞 Support

Consultez les guides de dépannage :
- **FIX_PORT_CONFLICT.md** - Résoudre conflits de ports
- **FIX_DOCKER_COMPOSE.md** - Problèmes Docker Compose v1/v2
- **SOLUTION_WEBAPP.md** - Alternatives pour le frontend

---

**Fait avec ❤️ - Version optimisée pour le développement backend**

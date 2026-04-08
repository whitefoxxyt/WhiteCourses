# 🎯 START HERE - AppCourse Docker

## ✅ Version actuelle : OPTIMISÉE v2.0

**Tout est prêt !** Voici comment démarrer :

---

## 🚀 Démarrage en 1 commande

```bash
./quick-start.sh
```

**Temps : 3-4 minutes** ⚡

---

## 🌐 Accéder aux services

Une fois démarré, ouvrez votre navigateur :

| Service | URL | Compte |
|---------|-----|--------|
| **API Backend** | http://localhost:8080 | - |
| **Health Check** | http://localhost:8080/health | - |
| **SonarQube** | http://localhost:9000 | admin / admin |

---

## 🧪 Lancer les tests

```bash
./gradlew :server:test
```

**Résultat actuel : 30+ tests ✅**

---

## 📊 Analyser le code avec SonarQube

```bash
# 1. Récupérer le token SonarQube
# Allez sur http://localhost:9000 > My Account > Security > Generate Token

# 2. Analyser
export SONAR_TOKEN="votre_token"
./analyze.sh
```

---

## 📚 Documentation

| Fichier | Description |
|---------|-------------|
| **README_OPTIMIZED.md** | ⭐ Guide complet (LIRE EN PREMIER) |
| **OPTIMIZED_SUCCESS.md** | Détails des optimisations |
| **CHANGELOG_OPTIMIZED.md** | Historique des versions |
| **DOCKER_TESTS_SONAR.md** | Guide technique complet |

---

## ⚙️ Commandes utiles

```bash
# Voir les logs
docker compose logs -f

# Vérifier l'état
docker compose ps

# Arrêter
docker compose down
```

---

## ❓ Problèmes ?

### Port déjà utilisé
Éditez `.env` et changez le port :
```bash
MYSQL_PORT=3308
```

### Rebuild complet
```bash
docker compose down -v
./quick-start.sh
```

### Consulter les guides
- **FIX_PORT_CONFLICT.md** - Conflits de ports
- **SOLUTION_WEBAPP.md** - Pourquoi WASM a été supprimé

---

## 🎯 Ce qui fonctionne

✅ MySQL (port 3307)  
✅ Backend Ktor (port 8080)  
✅ SonarQube (port 9000)  
✅ 30+ tests backend  
✅ JaCoCo coverage (70% min)  
✅ Healthchecks  
✅ Build rapide (3-4 min)  

---

## 💡 Pourquoi cette version ?

### ❌ Version précédente (avec WASM)
- Build très long (15-20 min)
- Erreurs Docker complexes
- Instable

### ✅ Version optimisée (actuelle)
- **80% plus rapide**
- Stable et fiable
- Focus backend
- Tous les tests fonctionnent

---

**🚀 Commencez maintenant : `./quick-start.sh`**

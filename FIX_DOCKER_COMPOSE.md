# 🔧 Résolution : Erreur "docker-compose: commande introuvable"

## Le problème

Vous voyez cette erreur :
```
docker-compose: commande introuvable
```

## La cause

Vous avez **Docker Compose v2+** (intégré à Docker) au lieu de l'ancien `docker-compose` standalone.

## ✅ Solutions

### Solution 1 : Utilisez la nouvelle syntaxe (Recommandé)

Remplacez `docker-compose` par `docker compose` (sans tiret) :

```bash
# Au lieu de :
docker-compose up -d

# Utilisez :
docker compose up -d
```

**Toutes les commandes :**
```bash
docker compose up -d           # Démarrer
docker compose down            # Arrêter
docker compose ps              # Statut
docker compose logs -f         # Logs
docker compose build           # Build
docker compose restart server  # Redémarrer un service
```

### Solution 2 : Créez un alias (Si vous préférez l'ancienne syntaxe)

```bash
# Pour Bash
echo 'alias docker-compose="docker compose"' >> ~/.bashrc
source ~/.bashrc

# Pour Zsh
echo 'alias docker-compose="docker compose"' >> ~/.zshrc
source ~/.zshrc

# Maintenant vous pouvez utiliser docker-compose
docker-compose up -d
```

### Solution 3 : Utilisez le script quick-start

Le script détecte automatiquement la version :

```bash
./quick-start.sh
```

## ✨ Pour ce projet

J'ai mis à jour tous les scripts et la documentation pour supporter **les deux versions**.

Relancez simplement :
```bash
./quick-start.sh
```

Le script détectera automatiquement votre version de Docker Compose et utilisera la bonne commande !

## 📚 Pour en savoir plus

- Docker Compose v2+ est intégré à Docker Desktop et aux versions récentes de Docker
- C'est plus rapide et mieux intégré avec Docker CLI
- Syntaxe : `docker compose` au lieu de `docker-compose`

---

## 🐛 Autre problème : Build WebApp échoue

Si vous voyez des erreurs comme :
- `kotlinWasmToolingSetup failed`
- `Process 'Resolving NPM dependencies using yarn' returns 127`

### Cause
Le build WASM nécessite Node.js et Yarn qui doivent être installés dans l'image Docker.

### Solution

Le Dockerfile a été mis à jour pour installer automatiquement Node.js 20 et Yarn.

Reconstruisez l'image :
```bash
docker compose build --no-cache webapp
```

**Note** : Le premier build prend 5-10 minutes (téléchargement des dépendances npm/yarn).

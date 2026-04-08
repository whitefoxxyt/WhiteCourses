# 🚀 GitHub Actions - Guide de démarrage rapide

## ⚡ Configuration en 5 minutes

### 1️⃣ Choisir SonarCloud ou SonarQube

#### Option A : SonarCloud (Recommandé) ⭐

**Avantages :** Gratuit, aucun serveur à gérer, intégration native GitHub

1. Aller sur https://sonarcloud.io
2. Se connecter avec votre compte GitHub
3. Cliquer sur `+` puis `Analyze new project`
4. Sélectionner votre repository `AppCourse`
5. Choisir `With GitHub Actions`
6. Copier le **SONAR_TOKEN** généré

#### Option B : SonarQube local

Utiliser votre instance SonarQube locale (docker-compose.yml) :
1. Démarrer SonarQube : `./quick-start.sh`
2. Aller sur http://localhost:9000
3. Se connecter (admin/admin)
4. Créer un token : `My Account > Security > Generate Token`

---

### 2️⃣ Configurer les secrets GitHub

Dans votre repository GitHub :

1. Aller dans `Settings` > `Secrets and variables` > `Actions`
2. Cliquer sur `New repository secret`
3. Ajouter ces secrets :

**Pour SonarCloud :**
```
Nom : SONAR_TOKEN
Valeur : <votre_token_sonarcloud>

Nom : SONAR_HOST_URL
Valeur : https://sonarcloud.io
```

**Pour SonarQube local :**
```
Nom : SONAR_TOKEN
Valeur : <votre_token_sonarqube_local>

Nom : SONAR_HOST_URL
Valeur : http://votre-serveur-sonarqube:9000
```

---

### 3️⃣ Mettre à jour sonar-project.properties (SonarCloud uniquement)

Si vous utilisez SonarCloud, éditez `sonar-project.properties` :

```properties
sonar.projectKey=votre-organisation_AppCourse
sonar.organization=votre-organisation
```

Remplacez par les valeurs fournies par SonarCloud.

---

### 4️⃣ Pousser le code

```bash
git add .github/workflows/ci.yml
git add sonar-project.properties
git commit -m "ci: Add GitHub Actions workflow with SonarQube"
git push origin main
```

---

### 5️⃣ Vérifier l'exécution

1. Aller dans l'onglet **Actions** de votre repository GitHub
2. Voir le workflow `CI - Tests & SonarQube Analysis` en cours
3. Attendre que toutes les étapes passent ✅

---

## 📊 Ce qui est automatisé

À chaque push sur `main` ou `develop`, ou à chaque Pull Request :

1. ✅ **Build** du projet Gradle
2. ✅ **Tests** unitaires (30+ tests)
3. ✅ **Coverage** JaCoCo (minimum 70%)
4. ✅ **Analyse SonarQube** (bugs, vulnérabilités, code smells)
5. ✅ **Quality Gate** vérification
6. ✅ **Rapports** uploadés comme artifacts

---

## 🏷️ Ajouter des badges (optionnel)

### Badge SonarCloud

Ajouter dans votre `README.md` :

```markdown
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=VOTRE_PROJECT_KEY&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=VOTRE_PROJECT_KEY)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=VOTRE_PROJECT_KEY&metric=coverage)](https://sonarcloud.io/summary/new_code?id=VOTRE_PROJECT_KEY)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=VOTRE_PROJECT_KEY&metric=bugs)](https://sonarcloud.io/summary/new_code?id=VOTRE_PROJECT_KEY)
```

Remplacez `VOTRE_PROJECT_KEY` par votre clé de projet.

### Badge GitHub Actions

```markdown
[![CI](https://github.com/VOTRE_USER/AppCourse/actions/workflows/ci.yml/badge.svg)](https://github.com/VOTRE_USER/AppCourse/actions/workflows/ci.yml)
```

---

## 🐛 Problèmes courants

### ❌ "Error: Invalid token"

**Solution :** Vérifier que :
1. Le secret `SONAR_TOKEN` est bien configuré dans GitHub
2. Le token n'a pas expiré
3. Le token a les bonnes permissions

### ❌ "Quality Gate failed"

**Solution :**
1. Vérifier les métriques sur SonarQube/SonarCloud
2. Corriger les bugs/vulnérabilités détectés
3. Ajouter des tests pour améliorer la couverture

### ❌ "Tests failed"

**Solution :**
1. Vérifier les logs du workflow
2. Exécuter les tests localement : `./gradlew :server:test`
3. Corriger les tests qui échouent

---

## 📚 Documentation complète

Voir `.github/workflows/README.md` pour plus de détails.

---

**Temps de configuration : ~5 minutes**  
**Résultat : CI/CD automatisé avec analyse de qualité** ✅

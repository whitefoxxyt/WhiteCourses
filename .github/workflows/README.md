# Configuration GitHub Actions pour SonarQube

## 🎯 Vue d'ensemble

Ce workflow GitHub Actions automatise :
- ✅ Build du projet
- ✅ Exécution des tests
- ✅ Génération du rapport de couverture (JaCoCo)
- ✅ Analyse SonarQube
- ✅ Vérification Quality Gate

---

## 🔧 Configuration requise

### 1. Secrets GitHub à configurer

Allez dans `Settings > Secrets and variables > Actions` de votre repository et ajoutez :

#### Option A : SonarCloud (Recommandé pour GitHub)

```
SONAR_TOKEN=<votre_token_sonarcloud>
SONAR_HOST_URL=https://sonarcloud.io
```

**Comment obtenir le token SonarCloud :**
1. Créer un compte sur https://sonarcloud.io
2. Lier votre repository GitHub
3. Aller dans `My Account > Security > Generate Token`
4. Copier le token dans les secrets GitHub

#### Option B : SonarQube auto-hébergé

```
SONAR_TOKEN=<votre_token_sonarqube>
SONAR_HOST_URL=https://votre-sonarqube.com
```

---

## 🚀 Fonctionnement du workflow

### Déclenchement
- Push sur `main` ou `develop`
- Pull Request ouverte ou mise à jour

### Étapes

1. **Checkout** - Récupère le code
2. **Setup JDK** - Configure Java 17
3. **Build** - Compile le projet
4. **Tests** - Execute les tests avec MySQL
5. **Coverage** - Génère le rapport JaCoCo
6. **SonarQube** - Analyse le code
7. **Quality Gate** - Vérifie les seuils
8. **Artifacts** - Upload les rapports

---

## 📊 Services utilisés

### MySQL Service
Un conteneur MySQL est démarré automatiquement pour les tests :
- Database: `appcourse`
- User: `appuser`
- Password: `apppassword`
- Port: `3306`

### Cache Gradle
Le cache Gradle est automatiquement géré pour accélérer les builds.

---

## 🔍 SonarQube vs SonarCloud

### SonarCloud (Recommandé pour CI/CD)

✅ **Avantages :**
- Gratuit pour projets open source
- Intégration native avec GitHub
- Pas de serveur à gérer
- Quality Gate automatique
- Badges pour README

❌ **Inconvénients :**
- Nécessite compte SonarCloud
- Données sur cloud public

### SonarQube auto-hébergé

✅ **Avantages :**
- Contrôle total
- Données en local
- Configuration personnalisée

❌ **Inconvénients :**
- Serveur à maintenir
- Doit être accessible depuis GitHub
- Configuration réseau requise

---

## 📝 Configuration SonarCloud (Étapes détaillées)

### 1. Créer un compte SonarCloud

1. Aller sur https://sonarcloud.io
2. Se connecter avec GitHub
3. Autoriser SonarCloud

### 2. Importer le projet

1. Cliquer sur `+` > `Analyze new project`
2. Sélectionner votre repository `AppCourse`
3. Choisir `With GitHub Actions`

### 3. Configurer le projet

SonarCloud génère automatiquement :
- La clé du projet
- Le token d'authentification
- Les instructions de configuration

### 4. Ajouter le token aux secrets GitHub

1. Copier le token généré
2. Aller dans `Settings > Secrets and variables > Actions`
3. Cliquer `New repository secret`
4. Nom : `SONAR_TOKEN`
5. Valeur : coller le token
6. Cliquer `Add secret`

### 5. Ajouter l'URL SonarCloud

1. Cliquer `New repository secret`
2. Nom : `SONAR_HOST_URL`
3. Valeur : `https://sonarcloud.io`
4. Cliquer `Add secret`

### 6. Mettre à jour sonar-project.properties

```properties
sonar.projectKey=votre-organisation_AppCourse
sonar.organization=votre-organisation

# Paths
sonar.sources=server/src/main/kotlin,shared/src/commonMain/kotlin
sonar.tests=server/src/test/kotlin
sonar.java.binaries=server/build/classes
sonar.coverage.jacoco.xmlReportPaths=server/build/reports/jacoco/test/jacocoTestReport.xml

# Exclusions
sonar.exclusions=**/build/**,**/resources/**,**/test/**
sonar.test.exclusions=**/test/**

# Java version
sonar.java.source=17
```

---

## ✅ Vérifier que tout fonctionne

### 1. Pousser le code

```bash
git add .github/workflows/ci.yml
git add sonar-project.properties
git commit -m "Add GitHub Actions CI with SonarQube"
git push
```

### 2. Vérifier l'exécution

1. Aller dans l'onglet `Actions` de votre repository
2. Voir le workflow en cours d'exécution
3. Vérifier que toutes les étapes passent ✅

### 3. Voir les résultats SonarQube

1. Aller sur https://sonarcloud.io
2. Ouvrir votre projet
3. Voir les métriques de qualité

---

## 🏷️ Badge SonarCloud

Ajouter à votre README.md :

```markdown
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=votre-org_AppCourse&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=votre-org_AppCourse)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=votre-org_AppCourse&metric=coverage)](https://sonarcloud.io/summary/new_code?id=votre-org_AppCourse)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=votre-org_AppCourse&metric=bugs)](https://sonarcloud.io/summary/new_code?id=votre-org_AppCourse)
```

Remplacez `votre-org_AppCourse` par votre clé de projet réelle.

---

## 🐛 Dépannage

### Le workflow échoue sur "SonarQube Scan"

**Vérifier :**
1. Le secret `SONAR_TOKEN` est bien configuré
2. Le secret `SONAR_HOST_URL` est correct
3. Le projet existe sur SonarCloud
4. Le token n'a pas expiré

### Les tests échouent

**Vérifier :**
1. MySQL service démarre correctement
2. Les variables d'environnement DB_* sont correctes
3. Les migrations de DB sont appliquées

### La couverture est insuffisante

**Action :**
1. Ajouter plus de tests
2. Ajuster le seuil dans `server/build.gradle.kts`
3. Vérifier que tous les fichiers sont inclus

---

## 📊 Métriques surveillées

### Quality Gate par défaut
- ✅ Couverture > 80%
- ✅ Code Smells < 5
- ✅ Bugs = 0
- ✅ Vulnerabilities = 0
- ✅ Security Hotspots reviewés
- ✅ Duplications < 3%

### Personnalisation
Vous pouvez créer votre propre Quality Gate sur SonarCloud :
1. `Quality Gates` > `Create`
2. Définir vos seuils personnalisés
3. Assigner au projet

---

## 🚀 Améliorations futures

### Ajouter des notifications
```yaml
- name: Send Slack notification
  if: failure()
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

### Matrix builds
```yaml
strategy:
  matrix:
    java: [17, 21]
```

### Parallel jobs
```yaml
jobs:
  test:
    # Tests uniquement
  sonar:
    needs: test
    # SonarQube uniquement
```

---

## 📚 Ressources

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [SonarCloud Documentation](https://docs.sonarcloud.io/)
- [SonarQube Gradle Plugin](https://docs.sonarqube.org/latest/analysis/scan/sonarscanner-for-gradle/)

---

**Date de création : 2026-04-08**  
**Status : ✅ Prêt à utiliser**

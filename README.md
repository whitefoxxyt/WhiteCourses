This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM), Server.

## ⚠️ Note importante : Docker Compose

Si vous voyez l'erreur `docker-compose: commande introuvable`, consultez [FIX_DOCKER_COMPOSE.md](./FIX_DOCKER_COMPOSE.md).

**TL;DR:** Utilisez `docker compose` (sans tiret) au lieu de `docker-compose`.

## 🐳 Docker, Tests & SonarQube

⚠️ **Note importante** : Le build WASM dans Docker peut être long (10-15 min). Consultez [QUICK_START_OPTIONS.md](./QUICK_START_OPTIONS.md) pour les options de démarrage rapide.

Pour une documentation complète sur Docker, les tests et SonarQube, consultez [DOCKER_TESTS_SONAR.md](./DOCKER_TESTS_SONAR.md).

### 🚀 Option 1 : Démarrage rapide (Recommandé)

```bash
# Build WebApp localement (3-5 min)
./build-webapp-local.sh

# Démarrer tous les services
docker compose -f docker-compose.simple.yml up -d
```

### 🐢 Option 2 : Build complet dans Docker (10-15 min)

```bash
# Configuration
cp .env.example .env

# Démarrer tous les services (build autonome)
docker compose up -d

# Vérifier le statut
docker compose ps
```

### ⚡ Option 3 : Backend uniquement (le plus rapide)

```bash
# Démarrer MySQL, Server et SonarQube uniquement
docker compose up -d mysql server sonarqube sonarqube-db
```

**Services disponibles :**
- Frontend Web : http://localhost:3000
- Backend API : http://localhost:8080
- SonarQube : http://localhost:9000

### Tests et Couverture

```bash
# Tests backend + couverture
./gradlew :server:test :server:jacocoTestReport

# Tests frontend
./gradlew :composeApp:allTests

# Analyse SonarQube complète
./analyze.sh [SONAR_TOKEN]
```

---

## 📁 Project Structure

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/server](./server/src/main/kotlin) is for the Ktor server application.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](./shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

Database setup for the server (`MaListe` schema):
- [`server/README-db.md`](./server/README-db.md)

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
    ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

### API MVP contract (`shared` <-> `server`)

 Base path: `/api`

 Observability endpoints:
 - `GET /health`
   - Success `200`: `OK`

 - `GET /api/listes/{listId}?magasinId={magasinId}`
   - Success `200`: `List<ProduitItem>`
   - Validation error `400`: plain error message
 - `PATCH /api/listes/item/{itemId}/etat?achete={true|false}`
   - Success `200`: `{ "itemId": Int, "estAchete": Boolean }`
   - Not found `404`: `item introuvable`
   - Validation error `400`: plain error message

The contract paths and parameters are centralized in:
- `shared/src/commonMain/kotlin/fr/white/appcourse/Constants.kt`
- `shared/src/commonMain/kotlin/fr/white/appcourse/network/ShoppingApiContract.kt`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).
## 🔧 Troubleshooting Rapide

- **`docker-compose: commande introuvable`** → [FIX_DOCKER_COMPOSE.md](./FIX_DOCKER_COMPOSE.md)
- **Port déjà utilisé (3306, 8080, etc.)** → [FIX_PORT_CONFLICT.md](./FIX_PORT_CONFLICT.md)
- **Build WASM trop lent** → [QUICK_START_OPTIONS.md](./QUICK_START_OPTIONS.md)
- **Guide complet** → [DOCKER_TESTS_SONAR.md](./DOCKER_TESTS_SONAR.md)

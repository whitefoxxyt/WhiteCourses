plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.sonarqube) apply true
}

sonar {
    properties {
        property("sonar.projectKey", "appcourse")
        property("sonar.projectName", "AppCourse")
        property("sonar.projectVersion", "1.0.0")
        property("sonar.host.url", System.getenv("SONAR_HOST_URL") ?: "http://localhost:9000")
        property("sonar.token", System.getenv("SONAR_TOKEN") ?: "")
        
        // Source directories
        property("sonar.sources", "server/src/main/kotlin,composeApp/src/commonMain/kotlin,shared/src/commonMain/kotlin")
        property("sonar.tests", "server/src/test/kotlin,composeApp/src/commonTest/kotlin")
        
        // Java version
        property("sonar.java.source", "17")
        property("sonar.java.target", "17")
        
        // Code coverage
        property("sonar.coverage.jacoco.xmlReportPaths", "server/build/reports/jacoco/test/jacocoTestReport.xml")
        
        // Exclusions
        property("sonar.exclusions", "**/build/**,**/generated/**,**/*.kts")
        property("sonar.coverage.exclusions", "**/test/**,**/androidTest/**,**/build/**,**/generated/**")
        
        // Quality gates
        property("sonar.qualitygate.wait", "true")
    }
}
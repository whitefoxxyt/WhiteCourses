plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
    jacoco
}

group = "fr.white.appcourse"
version = "1.0.0"
application {
    mainClass.set("fr.white.appcourse.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation("io.ktor:ktor-server-status-pages-jvm:3.3.3")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.3.3")
    implementation("com.mysql:mysql-connector-j:8.4.0")
    
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.mockk)
    testImplementation(libs.h2)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.testcontainers.junit)
}

// JaCoCo configuration for code coverage
jacoco {
    toolVersion = "0.8.12"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}
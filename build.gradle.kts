import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").version("1.3.71")
    id("com.diffplug.gradle.spotless") version "3.28.1"
    id("com.google.cloud.tools.jib").version("2.1.0")
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation("io.prometheus:simpleclient_hotspot:0.8.0")
    implementation("io.prometheus:simpleclient_log4j2:0.8.0")
    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-core:1.3.1")
    implementation("io.ktor:ktor-server-netty:1.3.1")
    implementation("io.ktor:ktor-metrics-micrometer:1.3.1")
    implementation("io.micrometer:micrometer-registry-prometheus:1.3.0")
    implementation("org.logevents:logevents:0.1.22")
    implementation("org.postgresql:postgresql:42.2.8")
    implementation("org.flywaydb:flyway-core:6.0.6")
    implementation("com.natpryce:konfig:1.6.10.0")
    testImplementation(kotlin("test-junit5"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.1")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    testImplementation("org.assertj:assertj-core:3.15.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

spotless {
    kotlin {
        ktlint("0.36.0")
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("0.36.0")
    }
}

tasks.named("jar") {
    dependsOn("test")
}

tasks.named("compileKotlin") {
    dependsOn("spotlessCheck")
}
tasks.named("spotlessCheck") {
    dependsOn("spotlessApply")
}

import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").version("1.3.71")
    application
    id("com.diffplug.gradle.spotless") version "3.28.1"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(platform(kotlin("bom")))
    implementation("io.prometheus:simpleclient_hotspot:0.8.0")
    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-core:1.3.1")
    implementation("io.ktor:ktor-server-netty:1.3.1")
    implementation("org.logevents:logevents:0.1.22")
    testImplementation(kotlin("test-junit5"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.1")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    testImplementation("org.assertj:assertj-core:3.15.0")
}

application {
    // Define the main class for the application
    mainClassName = "com.chriswk.sudoku.AppKt"
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

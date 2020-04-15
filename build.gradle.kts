import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").version("1.3.71")
    kotlin("plugin.serialization").version("1.3.71")
    id("com.diffplug.gradle.spotless") version "3.28.1"
    id("com.hpe.kraal") version "0.0.15" // kraal version - for makeRelease.sh
}

repositories {
    jcenter()
    mavenCentral()
}

val ktor_version = "1.3.1"
val testcontainersVersion = "1.13.0"

dependencies {
    implementation(platform(kotlin("bom")))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")

    implementation("io.prometheus:simpleclient_hotspot:0.8.0")
    implementation("io.prometheus:simpleclient_log4j2:0.8.0")
    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-metrics-micrometer:$ktor_version")
    implementation("io.micrometer:micrometer-registry-prometheus:1.3.0")
    implementation("org.logevents:logevents:0.1.29")
    implementation("org.postgresql:postgresql:42.2.9")
    implementation("org.flywaydb:flyway-core:6.3.3")
    implementation("com.natpryce:konfig:1.6.10.0")
    implementation("de.huxhorn.sulky:de.huxhorn.sulky.ulid:8.2.0")
    implementation("com.zaxxer:HikariCP:3.4.2")
    implementation("com.github.seratch:kotliquery:1.3.1")
    testImplementation(kotlin("test-junit5"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.1")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    testImplementation("org.assertj:assertj-core:3.15.0")
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
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

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xuse-experimental=kotlin.Experimental", "-progressive")
        // disable -Werror with: ./gradlew -PwarningsAsErrors=false
        allWarningsAsErrors = project.findProperty("warningsAsErrors") != "false"
        jvmTarget = "1.8"
    }
}

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

val fatjar by tasks.creating(Jar::class) {
    from(kraal.outputZipTrees) {
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }

    manifest {
        attributes("Main-Class" to "com.chriswk.sudoku.AppKt")
    }
    destinationDirectory.set(project.buildDir.resolve("fatjar"))
    archiveFileName.set("sudoku-backend.jar")
}
tasks.named("assemble") {
    dependsOn(fatjar)
}

val stage by tasks.creating {
    dependsOn("clean", "assemble")
}


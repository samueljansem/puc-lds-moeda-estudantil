plugins {
    id("io.micronaut.application") version "4.5.5"
    id("com.gradleup.shadow") version "8.3.6"
    id("io.micronaut.aot") version "4.5.5"
}

version = "0.1"
group = "br.puc.moedaestudantil"



repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.rabbitmq:micronaut-rabbitmq")
    implementation("io.micronaut.security:micronaut-security-session")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.micronaut.views:micronaut-views-fieldset")
    implementation("io.micronaut.views:micronaut-views-thymeleaf")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("com.resend:resend-java:4.4.0")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.yaml:snakeyaml")
    testImplementation("io.micronaut:micronaut-http-client")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}



application {
    mainClass = "br.puc.moedaestudantil.Application"
}

java {
    // Pino a toolchain em 21 para que compilação e testes rodem sempre nessa
    // versão, independentemente do JAVA_HOME do ambiente (que pode apontar
    // para um JDK mais novo). Sem isso, micronaut-test pula testes silenciosamente.
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}




graalvmNative.toolchainDetection = false
graalvmNative {
    binaries {
        all {
            buildArgs.add("-H:+SharedArenaSupport")
        }
    }
}




micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("br.puc.moedaestudantil.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }

}

tasks.named<io.micronaut.gradle.docker.MicronautDockerfile>("dockerfile") {

    baseImage = "eclipse-temurin:25-jre"
}



tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "21"
}





// https://docs.gradle.org/current/userguide/upgrading_major_version_9.html#test_task_fails_when_no_tests_are_discovered
tasks.withType<AbstractTestTask>().configureEach {
    failOnNoDiscoveredTests = false
}





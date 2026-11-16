import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kraken)
    alias(libs.plugins.graalvm.native)
}

group = "net.mythicisland.spark"
version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    maven("https://repo.lucko.me/")
}

val shade: Configuration = configurations.create("shade")

configurations.compileOnly {
    extendsFrom(shade)
}

dependencies {
    compileOnly(libs.minestom)
    compileOnly(libs.slf4j.api)
    implementation(libs.guava)
    shade(libs.spark.common) {
        exclude(group = "me.lucko", module = "bytesocks-java-client")
    }
    shade(libs.bytesocks.java.client.j11)

    testImplementation(files(tasks.shadowJar))
    testImplementation(libs.minestom)
    testImplementation(libs.kotlin.test)
    testImplementation(platform(libs.junit.bom))
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
        languageVersion = KotlinVersion.KOTLIN_2_4
        apiVersion = KotlinVersion.KOTLIN_2_4
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveClassifier = "plain"
}

tasks.shadowJar {
    archiveClassifier = ""
    configurations.set(listOf(shade))

    dependencies {
        exclude(dependency("net.kyori:^(?!adventure-text-feature-pagination).+$"))
    }

    val lib = "net.mythicisland.spark.minestom.lib"
    relocate("net.kyori.adventure.text.feature.pagination", "$lib.adventure.pagination")
    relocate("net.bytebuddy", "$lib.bytebuddy")
    relocate("com.google.protobuf", "$lib.protobuf")
    relocate("org.objectweb.asm", "$lib.asm")
    relocate("one.profiler", "$lib.asyncprofiler")
    relocate("me.lucko.bytesocks.client", "$lib.bytesocks")

    exclude("module-info.class")
    exclude("META-INF/maven/**")
    exclude("META-INF/proguard/**")
    exclude("META-INF/LICENSE")
    exclude("META-INF/NOTICE")
    exclude("**/*.proto")
    exclude("**/*.proto.bin")
    exclude("linux-arm64/**")
    exclude("linux-x64/**")
    exclude("macos/**")
}

graalvmNative {
    toolchainDetection = false

    metadataRepository {
        enabled = true
    }

    agent {
        defaultMode = "standard"
        callerFilterFiles.from("src/test/native-image-agent-filter.json")
        accessFilterFiles.from("src/test/native-image-agent-filter.json")
    }

    binaries.named("test") {
        javaLauncher = javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(25)
            nativeImageCapable = true
        }
        resources.autodetect()
        buildArgs.addAll(
            "--enable-url-protocols=http,https",
            "-H:+ReportExceptionStackTraces",
        )
    }
}

maven {
    url = "https://github.com/mythicisland/spark-minestom"
    description = "Minestom platform implementation of spark"

    repositories {
        register("releases") {
            url.set("https://repo.mythicisland.club/releases")
            username.set(System.getenv("MYTHICISLAND_USERNAME"))
            password.set(System.getenv("MYTHICISLAND_PASSWORD"))
        }
        register("snapshots") {
            url.set("https://repo.mythicisland.club/snapshots")
            username.set(System.getenv("MYTHICISLAND_USERNAME"))
            password.set(System.getenv("MYTHICISLAND_PASSWORD"))
        }
    }
}

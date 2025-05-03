import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import java.io.FileInputStream
import java.util.*

val localProperties = loadLocalProps()

version = "1.0.0"
group = "io.github.tomwyr"
description = "A type-safe Godot node tree representation in Kotlin"

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.25"
    id("com.gradle.plugin-publish") version "1.2.1"
    id("com.adarshr.test-logger") version "4.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    implementation("net.java.dev.jna:jna:5.17.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    testImplementation(kotlin("test"))
}

configure<TestLoggerExtension> {
    theme = ThemeType.MOCHA
    showCauses = true
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website = "https://github.com/tomwyr/godot-kotlin-tree"
    vcsUrl = "https://github.com/tomwyr/godot-kotlin-tree.git"

    bindProp("gradle.publish.key")
    bindProp("gradle.publish.secret")

    plugins {
        create("godot-kotlin-tree") {
            id = "io.github.tomwyr.godot-kotlin-tree"
            displayName = "Godot Kotlin Tree"
            description = "A type-safe Godot node tree representation in Kotlin"
            tags = listOf("godot", "kotlin", "node", "tree")
            implementationClass = "com.tomwyr.GodotKotlinTree"
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

fun bindProp(key: String) {
    val value = localProperties[key] as? String ?: throw Exception("Expected local property $key not set")
    System.setProperty(key, value)
}

fun loadLocalProps() = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

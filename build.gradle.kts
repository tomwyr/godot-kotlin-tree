plugins {
    id("com.gradle.plugin-publish") version "1.2.1"
    kotlin("jvm") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website = "https://github.com/tomwyr/godot-kotlin-tree"
    vcsUrl = "https://github.com/tomwyr/godot-kotlin-tree.git"

    plugins {
        create("godot-node-tree") {
            id = "io.github.tomwyr.godot-node-tree"
            group = "io.github.tomwyr"
            version = "1.0.0"
            displayName = "Godot Kotlin Tree"
            description = "A type-safe Godot node tree representation in Kotlin."
            tags = listOf("godot", "kotlin", "node", "tree")
            implementationClass = "com.tomwyr.GodotNodeTree"
        }
    }
}

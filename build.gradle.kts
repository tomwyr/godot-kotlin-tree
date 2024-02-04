plugins {
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
}

gradlePlugin {
    plugins {
        create("godot-node-tree") {
            id = "com.tomwyr.godot-node-tree"
            group = "com.tomwyr"
            version = "1.0.0"
            implementationClass = "com.tomwyr.GodotNodeTree"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

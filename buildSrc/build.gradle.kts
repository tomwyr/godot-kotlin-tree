plugins {
    `java-gradle-plugin`
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
            implementationClass = "com.tomwyr.GodotNodeTree"
        }
    }
}

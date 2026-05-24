import godot.entrygenerator.settings.RegistrationFileLayoutMode
import godot.gradle.GodotLanguage

plugins {
    id("com.utopia-rise.godot-kotlin-jvm") version "0.16.1-4.6.3"
    // Remove version to build against plugin's local version.
    id("io.github.tomwyr.godot-kotlin-tree") version "1.2.0"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

godot {
    // --------- Setup ---------

    // JVM source languages that participate in the Godot Kotlin/JVM build pipeline.
    languages.set(setOf(GodotLanguage.KOTLIN, GodotLanguage.JAVA, GodotLanguage.SCALA))

    // Override the toolchain or language versions only when you need something newer than the defaults.
    // javaVersion.set(17)
    // kotlinVersion.set("2.2.0")
    // scalaVersion.set("3.6.3")

    // Only change this if the Gradle project is not the Godot project root containing project.godot.
    // godotProjectDirectory.set(file("."))

    // Directory where .gdj registration files are generated and synchronized.
    registrationFilesDirectory.set(projectDir.resolve("scripts"))

    // HIERARCHICAL mirrors package folders under registrationFilesDirectory.
    registrationFilesLayoutMode.set(RegistrationFileLayoutMode.HIERARCHICAL)

    // Keep the default short registered class names instead of using fully qualified names.
    // registrationNameMode.set(RegisteredNameMode.SIMPLE_NAME)

    // Enables the coroutine support library for Godot lifecycle integration.
    //isGodotCoroutinesEnabled.set(true)
}

godotNodeTree {
    // projectPath = "../dodge-the-creeps"
    packageName = "com.example.game"
}

sourceSets {
    main {
        kotlin.srcDir("build/generated/godotNodeTree/kotlin")
    }
}

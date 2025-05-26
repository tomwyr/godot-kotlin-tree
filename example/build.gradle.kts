plugins {
    id("com.utopia-rise.godot-kotlin-jvm") version "0.13.1-4.4.1"
    id("io.github.tomwyr.godot-kotlin-tree") version "1.0.0"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

godot {
    // ---------Setup-----------------

    // the script registration which you'll attach to nodes are generated into this directory
    registrationFileBaseDir.set(projectDir.resolve("gdj"))

	// Create .gdj files from all JVM scripts
	isRegistrationFileGenerationEnabled.set(true)

    // defines whether the script registration files should be generated hierarchically according to the classes package path or flattened into `registrationFileBaseDir`
    //isRegistrationFileHierarchyEnabled.set(true)

    // defines whether your scripts should be registered with their fqName or their simple name (can help with resolving script name conflicts)
    //isFqNameRegistrationEnabled.set(false)
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

plugins {
    kotlin("jvm") version "1.9.0" apply false
    id("com.utopia-rise.godot-kotlin-jvm") version "0.8.1-4.2.0"
    id("io.github.tomwyr.godot-kotlin-tree") version "1.0.0"
}

kotlin {
    jvmToolchain(20)
}

repositories {
    mavenCentral()
}

godot {
    registrationFileBaseDir.set(projectDir.resolve("dodge_the_creeps"))
    isRegistrationFileHierarchyEnabled.set(false)
    isAndroidExportEnabled.set(false)
    d8ToolPath.set(File("${System.getenv("ANDROID_SDK_ROOT")}/build-tools/31.0.0/d8"))
    androidCompileSdkDir.set(File("${System.getenv("ANDROID_SDK_ROOT")}/platforms/android-30"))
    isGraalNativeImageExportEnabled.set(false)
    graalVmDirectory.set(File("${System.getenv("GRAALVM_HOME")}"))
    windowsDeveloperVCVarsPath.set(File("${System.getenv("VC_VARS_PATH")}"))
    isIOSExportEnabled.set(false)
}

godotNodeTree {
    projectPath = "../dodge-the-creeps"
    packageName = "com.example.game"
}

sourceSets {
    main {
        kotlin.srcDir("build/generated/godotNodeTree/kotlin")
    }
}

plugins {
    id("com.utopia-rise.godot-kotlin-jvm") version "0.8.1-4.2.0"
    kotlin("jvm") version "1.9.0"
    id("com.google.devtools.ksp") version "1.9.0-1.0.13"
}

dependencies {
    implementation(project(":annotations"))
    ksp(project(":processor"))
}

ksp {
    arg("godotProjectPath", "dodge_the_creeps")
}

kotlin {
    jvmToolchain(20)
}

godot {
    registrationFileBaseDir.set(projectDir.resolve("gdj").also { it.mkdirs() })
    isAndroidExportEnabled.set(false)
    d8ToolPath.set(File("${System.getenv("ANDROID_SDK_ROOT")}/build-tools/31.0.0/d8"))
    androidCompileSdkDir.set(File("${System.getenv("ANDROID_SDK_ROOT")}/platforms/android-30"))
    isGraalNativeImageExportEnabled.set(false)
    graalVmDirectory.set(File("${System.getenv("GRAALVM_HOME")}"))
    windowsDeveloperVCVarsPath.set(File("${System.getenv("VC_VARS_PATH")}"))
    isIOSExportEnabled.set(false)
}

package com.tomwyr

import com.tomwyr.core.Log
import com.tomwyr.core.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class GodotNodeTree : Plugin<Project> {
    override fun apply(project: Project) {
        initLog(project)
        configure(project)
        addSourceSet(project)
    }
}

open class GodotNodeTreeConfig(
    var projectPath: String? = null,
    var packageName: String? = null,
    )

private fun initLog(project: Project) {
    val logger = project.logger
    Log.logger = object : Logger {
        override fun debug(message: String) = logger.debug(message)
        override fun info(message: String) = logger.info(message)
        override fun warn(message: String) = logger.warn(message)
    }
}

private fun configure(project: Project) {
    val config = project.extensions.create("godotNodeTree", GodotNodeTreeConfig::class.java)
    project.tasks.register("generateNodeTree") { task ->
        task.doLast {
            generate(project, config)
        }
    }
}

private fun addSourceSet(project: Project) {
    val pluginExt = project.extensions.findByType(KotlinJvmProjectExtension::class.java)
    val sourceSet = pluginExt?.sourceSets?.getByName("main")?.kotlin
    sourceSet?.srcDirs("build/generated/godotNodeTree/kotlin")
}

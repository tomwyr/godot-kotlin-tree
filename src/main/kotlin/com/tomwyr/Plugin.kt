package com.tomwyr

import com.tomwyr.common.Log
import com.tomwyr.common.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class GodotNodeTree : Plugin<Project> {
    override fun apply(project: Project) {
        initLog(project)
        registerTask(project)
        addSourceSet(project)
    }

    private fun initLog(project: Project) {
        Log.logger = Logger.stdOut(project)
    }

    private fun registerTask(project: Project) {
        val config = project.extensions.create("godotNodeTree", GodotNodeTreeConfig::class.java)
        project.tasks.register("generateNodeTree") { task ->
            task.doLast {
                GenerateTreeCommand().run(project, config)
            }
        }
    }

    private fun addSourceSet(project: Project) {
        val pluginExt = project.extensions.findByType(KotlinJvmProjectExtension::class.java)
        val sourceSet = pluginExt?.sourceSets?.getByName("main")?.kotlin
        sourceSet?.srcDirs("build/generated/godotNodeTree/kotlin")
    }
}

open class GodotNodeTreeConfig(
        var projectPath: String? = null,
        var packageName: String? = null,
)

package com.tomwyr

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class GodotNodeTree : Plugin<Project> {
    override fun apply(project: Project) {
        registerTask(project)
        addSourceSet(project)
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

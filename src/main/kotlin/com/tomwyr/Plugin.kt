package com.tomwyr

import com.tomwyr.command.GenerateTreeCommand
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class GodotKotlinTree : Plugin<Project> {
    override fun apply(project: Project) {
        registerTask(project)
        addSourceSet(project)
    }

    private fun registerTask(project: Project) {
        val input = project.extensions.create("godotNodeTree", GodotKotlinTreeInput::class.java)
        project.tasks.register("generateNodeTree") { task ->
            task.doLast {
                GenerateTreeCommand.from(project, input).run()
            }
        }
    }

    private fun addSourceSet(project: Project) {
        val pluginExt = project.extensions.findByType(KotlinJvmProjectExtension::class.java)
        val sourceSet = pluginExt?.sourceSets?.getByName("main")?.kotlin
        sourceSet?.srcDirs("build/generated/godotNodeTree/kotlin")
    }
}

open class GodotKotlinTreeInput(
    var projectPath: String? = null,
    var validateProjectPath: Boolean = true,
    var packageName: String? = null,
)

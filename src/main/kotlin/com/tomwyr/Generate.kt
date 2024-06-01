package com.tomwyr

import com.tomwyr.generator.NodeTreeGenerator
import com.tomwyr.utils.GodotKotlinProject
import com.tomwyr.utils.getOutputPath
import com.tomwyr.utils.getProjectPath
import com.tomwyr.core.Log
import org.gradle.api.Project

fun generate(project: Project, config: GodotNodeTreeConfig) {
    val godotProject = getGodotProject(project, config)
    val treeInfo = NodeTreeGenerator().generate(godotProject)
    Log.nodeTreeGenerated(treeInfo)
}

private fun getGodotProject(project: Project, config: GodotNodeTreeConfig): GodotKotlinProject {
    val targetPackage = config.packageName
    if (targetPackage != null) {
        Log.targetPackage(targetPackage)
    }

    val rootPath = project.projectDir.absolutePath
    Log.kotlinProjectPath(rootPath)

    val projectRelativePath = config.projectPath
    if (projectRelativePath != null) {
        Log.godotCustomProjectPath(projectRelativePath)
    }

    val projectPath = getProjectPath(rootPath, projectRelativePath)
    Log.godotProjectPath(projectPath)

    val outputPath = getOutputPath(rootPath, targetPackage)
    Log.kotlinOutputPath(outputPath)

    return GodotKotlinProject(projectPath, outputPath, targetPackage)
}

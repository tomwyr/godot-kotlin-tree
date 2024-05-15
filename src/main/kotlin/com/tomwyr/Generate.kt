package com.tomwyr

import com.tomwyr.core.Log
import com.tomwyr.core.NodeTreeGenerator
import com.tomwyr.core.getOutputPath
import com.tomwyr.core.getProjectPath
import org.gradle.api.Project
import java.io.File
import java.io.OutputStream

fun generate(project: Project, config: GodotNodeTreeConfig) {
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

    val treeInfo = NodeTreeGenerator().generate(
            projectPath,
            targetPackage,
            createFile = { createOutputFile(outputPath) },
    )
    Log.nodeTreeGenerated(treeInfo)
}

private fun createOutputFile(dirPath: String): OutputStream {
    return File(dirPath).run {
        parentFile.mkdirs()
        createNewFile()
        outputStream()
    }
}

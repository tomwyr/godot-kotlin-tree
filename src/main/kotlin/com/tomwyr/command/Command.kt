package com.tomwyr.command

import com.tomwyr.GodotKotlinTreeInput
import com.tomwyr.common.InvalidGodotProject
import com.tomwyr.ffi.generateNodeTree
import org.gradle.api.Project
import java.io.File
import java.nio.file.Paths

class GenerateTreeCommand(
    val libPath: String,
    val projectPath: String,
    val outputPath: String,
    val packageName: String?,
) {
    fun run() {
        val tree = generateNodeTree(libPath = libPath, projectPath = projectPath)
        val content = NodeTreeRenderer().render(packageName, tree)
        NodeTreeWriter().write(content, outputPath)
    }

    companion object Factory {
        fun from(project: Project, input: GodotKotlinTreeInput): GenerateTreeCommand {
            val rootPath = project.projectDir.absolutePath
            val libPath = "GodotNodeTreeCore"
            val projectPath = getProjectPath(rootPath = rootPath, relativePath = input.projectPath)
            val outputPath = getOutputPath(rootPath = rootPath, packageName = input.packageName)

            return GenerateTreeCommand(
                libPath = libPath,
                projectPath = projectPath,
                outputPath = outputPath,
                packageName = input.packageName
            )
        }

        private fun getProjectPath(rootPath: String, relativePath: String?): String {
            val fileName = "project.godot"
            val path = Paths.get(rootPath, relativePath ?: "", fileName)
            return path.toString()
        }

        private fun getOutputPath(rootPath: String, packageName: String?): String {
            val buildPath = listOf("build", "generated", "godotNodeTree", "kotlin").toTypedArray()
            val packagePath = packageName?.split(".")?.toTypedArray() ?: emptyArray()
            val fileName = "GodotNodeTree.kt"
            return Paths.get(rootPath, *buildPath, *packagePath, fileName).toString()
        }
    }
}

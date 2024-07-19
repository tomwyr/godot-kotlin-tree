package com.tomwyr.utils

import com.tomwyr.GodotNodeTreeConfig
import com.tomwyr.common.InvalidGodotProject
import com.tomwyr.common.Log
import com.tomwyr.common.SceneData
import java.io.File
import java.nio.file.FileSystems

class GodotKotlinProject(
    private val projectPath: String,
    private val outputPath: String,
    val targetPackage: String?,
) {
    fun readScenes(): List<SceneData> {
        return File(projectPath).walkTopDown()
            .filter { it.path.endsWith(".tscn") }
            .map {
                val name = it.nameWithoutExtension
                    .split('_', '-')
                    .joinToString("") { segment -> segment.capitalize() }
                SceneData(name, it.readText())
            }
            .sortedBy { it.name }
            .toList()
    }

    fun writeNodeTree(content: String) {
        val file = File(outputPath).apply {
            parentFile.mkdirs()
            createNewFile()
        }
        file.outputStream().apply {
            write(content.toByteArray())
            close()
        }
    }


    companion object Factory {
        fun create(rootPath: String, config: GodotNodeTreeConfig): GodotKotlinProject {
            val targetPackage = config.packageName
            if (targetPackage != null) {
                Log.targetPackage(targetPackage)
            }

            Log.kotlinProjectPath(rootPath)

            val projectRelativePath = config.projectPath
            if (projectRelativePath != null) {
                Log.godotCustomProjectPath(projectRelativePath)
            }

            val projectPath = getProjectPath(rootPath, projectRelativePath)
            projectPath ?: throw InvalidGodotProject()
            Log.godotProjectPath(projectPath)

            val outputPath = getOutputPath(rootPath, targetPackage)
            Log.kotlinOutputPath(outputPath)

            return GodotKotlinProject(projectPath, outputPath, targetPackage)
        }

        private fun getProjectPath(rootPath: String, relativePath: String?): String? {
            val path = StringBuffer().apply {
                append(rootPath.removeSuffix(sep))
                append(sep)
                if (relativePath != null) {
                    append(relativePath.removeSuffix(sep))
                }
            }.toString()

            val fileName = "project.godot"
            val projectFile = File("$path/$fileName".sysSepd)

            return path.takeIf { projectFile.exists() }
        }

        private fun getOutputPath(rootPath: String, targetPackage: String?): String {
            val buildPath = "build/generated/godotNodeTree/kotlin".sysSepd
            val fileName = "GodotNodeTree.kt"
            val packagePath = targetPackage?.replace(".", sep)

            return StringBuffer().apply {
                append("$rootPath/$buildPath")
                if (packagePath != null) append("/$packagePath")
                append("/$fileName")
            }.toString().sysSepd
        }

        private val String.sysSepd: String
            get() = replace("/", sep)

        private val sep = FileSystems.getDefault().separator
    }
}

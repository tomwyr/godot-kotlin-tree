package com.tomwyr.utils

import java.io.File

class SceneData(val name: String, val content: String)

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
}

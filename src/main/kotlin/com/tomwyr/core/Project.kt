package com.tomwyr.core

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
            .map { SceneData(it.nameWithoutExtension, it.readText()) }
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

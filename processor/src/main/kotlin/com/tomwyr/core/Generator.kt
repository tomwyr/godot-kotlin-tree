package com.tomwyr.core

import java.io.File
import java.io.OutputStream

class NodeTreeGenerator(
        private val renderer: NodeTreeRenderer,
        private val parser: SceneNodesParser,
) {
    fun generate(projectPath: String, targetPackage: String, createFile: () -> OutputStream) {
        val scenes = readScenes(projectPath)
        val content = renderer.render(targetPackage, scenes)

        createFile().run {
            write(content.toByteArray())
            close()
        }
    }

    private fun readScenes(projectPath: String): List<Scene> {
        val sceneFiles = File(projectPath).walkTopDown().filter { it.path.endsWith(".tscn") }

        return sceneFiles.map { file ->
            val name = file.nameWithoutExtension
            val root = parser.parse(file.readText())
            Scene(name, root)
        }.toList()
    }
}

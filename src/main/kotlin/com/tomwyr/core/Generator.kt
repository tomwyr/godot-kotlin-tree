package com.tomwyr.core

import java.io.File
import java.io.OutputStream

class NodeTreeGenerator(
        private val parser: SceneNodesParser = SceneNodesParser(),
        private val renderer: NodeTreeRenderer = NodeTreeRenderer(),
) {
    fun generate(projectPath: String, targetPackage: String?, createFile: () -> OutputStream): NodeTreeStats {
        Log.readingScenes()
        val scenes = readScenes(projectPath)

        Log.renderingNodeTree()
        val content = renderer.render(targetPackage, scenes)

        Log.savingResult()
        createFile().run {
            write(content.toByteArray())
            close()
        }
        Log.resultSaved()

        return NodeTreeStats(scenes)
    }

    private fun readScenes(projectPath: String): List<Scene> {
        val sceneFiles = File(projectPath).walkTopDown().filter { it.path.endsWith(".tscn") }.toList()
        Log.scenesFound(sceneFiles)

        return sceneFiles.map { file ->
            Log.parsingScene(file)
            val name = file.nameWithoutExtension
            val root = parser.parse(file.readText())
            Scene(name, root)
        }
    }
}

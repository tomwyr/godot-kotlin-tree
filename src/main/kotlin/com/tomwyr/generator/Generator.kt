package com.tomwyr.generator

import com.tomwyr.utils.GodotKotlinProject
import com.tomwyr.core.Log
import com.tomwyr.core.NodeTreeInfo
import com.tomwyr.core.Scene

class NodeTreeGenerator(
    private val parser: SceneNodesParser = SceneNodesParser(),
    private val renderer: NodeTreeRenderer = NodeTreeRenderer(),
) {
    fun generate(project: GodotKotlinProject): NodeTreeInfo {
        Log.readingScenes()
        val scenesData = project.readScenes()
        Log.scenesFound(scenesData)

        val scenes = scenesData.map { data ->
            Log.parsingScene(data)
            val root = parser.parse(data.content)
            Scene(data.name, root)
        }.toList()

        Log.renderingNodeTree()
        val content = renderer.render(project.targetPackage, scenes)

        Log.savingResult()
        project.writeNodeTree(content)
        Log.resultSaved()

        return NodeTreeInfo(scenes)
    }
}

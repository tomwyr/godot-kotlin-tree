package com.tomwyr.generator

import com.tomwyr.common.Log
import com.tomwyr.common.NodeTreeInfo
import com.tomwyr.common.Scene
import com.tomwyr.utils.GodotKotlinProject

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
            val root = parser.parse(data)
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

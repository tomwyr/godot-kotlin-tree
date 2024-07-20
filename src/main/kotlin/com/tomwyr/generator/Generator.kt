package com.tomwyr.generator

import com.tomwyr.common.NodeTreeInfo
import com.tomwyr.common.Scene
import com.tomwyr.utils.GodotKotlinProject

class NodeTreeGenerator(
    private val parser: SceneNodesParser = SceneNodesParser(),
    private val renderer: NodeTreeRenderer = NodeTreeRenderer(),
) {
    fun generate(project: GodotKotlinProject): NodeTreeInfo {
        val scenesData = project.readScenes()
        val scenes = scenesData.map { data ->
            val root = parser.parse(data)
            Scene(data.name, root)
        }.toList()
        val content = renderer.render(project.targetPackage, scenes)

        project.writeNodeTree(content)

        return NodeTreeInfo(scenes)
    }
}

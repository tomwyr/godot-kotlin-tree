package com.tomwyr.core

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

package com.tomwyr.common

import kotlin.math.max

class SceneData(val name: String, val content: String)

data class Scene(val name: String, val root: Node) {
    val nodesCount: Int = root.flatten().size
    val nodesDepth: Int = root.longestPath().size
}

sealed class Node {
    abstract val name: String
    abstract fun flatten(): List<Node>
    abstract fun longestPath(): List<Node>
}

data class ParentNode(
    override val name: String,
    val type: String,
    val children: List<Node>,
) : Node() {
    override fun flatten(): List<Node> {
        return children.flatMap { it.flatten() } + this
    }

    override fun longestPath(): List<Node> {
        return children.map { it.longestPath() }.maxByOrNull { it.size }.orEmpty() + this
    }
}

data class LeafNode(
    override val name: String,
    val type: String,
) : Node() {
    override fun flatten(): List<Node> = listOf(this)

    override fun longestPath(): List<Node> = listOf(this)
}

data class NestedScene(
    override val name: String,
    val scene: String,
) : Node() {
    override fun flatten(): List<Node> = listOf(this)

    override fun longestPath(): List<Node> = listOf(this)
}

class NodeTreeInfo(
        val nodes: Int,
        val depth: Int,
        val scenes: Int,
) {
    companion object {
        operator fun invoke(scenes: List<Scene>): NodeTreeInfo {
            var nodes = 0
            var depth = 0

            for (scene in scenes) {
                nodes += scene.nodesCount
                depth = max(depth, scene.nodesDepth)
            }

            return NodeTreeInfo(
                    nodes = nodes,
                    depth = depth,
                    scenes = scenes.size,
            )
        }
    }
}

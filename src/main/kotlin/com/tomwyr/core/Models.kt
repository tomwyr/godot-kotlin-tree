package com.tomwyr.core

data class Scene(
    val name: String,
    val root: Node,
) {
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

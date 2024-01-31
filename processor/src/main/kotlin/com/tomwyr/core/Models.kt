package com.tomwyr.core

data class Scene(
        val name: String,
        val root: Node,
) {
    val nodesCount: Int = root.flatten().size
    val nodesDepth: Int = root.longestPath().size
}

data class Node(
        val name: String,
        val type: String,
        val children: List<Node>,
) {
    fun flatten(): List<Node> {
        return children.flatMap { it.flatten() } + this
    }

    fun longestPath(): List<Node> {
        return children.map { it.longestPath() }.maxByOrNull { it.size }.orEmpty() + this
    }
}

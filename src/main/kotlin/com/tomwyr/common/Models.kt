package com.tomwyr.common

import kotlinx.serialization.Serializable

@Serializable
class NodeParams(
    val name: String,
    val type: String?,
    val instance: String?,
    val parent: String?,
)

@Serializable
class NodeTree(val scenes: List<Scene>)

@Serializable
class Scene(val name: String, val root: Node)

@Serializable
sealed class Node {
    abstract val name: String
}

@Serializable
class ParentNode(
    override val name: String,
    val type: String,
    val children: List<Node>,
) : Node()

@Serializable
class LeafNode(
    override val name: String,
    val type: String,
) : Node()

@Serializable
class NestedScene(
    override val name: String,
    val scene: String,
) : Node()

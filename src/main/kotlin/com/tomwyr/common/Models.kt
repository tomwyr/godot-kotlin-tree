@file:OptIn(ExperimentalSerializationApi::class)

package com.tomwyr.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

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
@JsonClassDiscriminator("nodeType")
sealed class Node {
    abstract val name: String
}

@Serializable
@SerialName("parentNode")
class ParentNode(
    override val name: String,
    val type: String,
    val children: List<Node>,
) : Node()

@Serializable
@SerialName("leafNode")
class LeafNode(
    override val name: String,
    val type: String,
) : Node()

@Serializable
@SerialName("nestedScene")
class NestedScene(
    override val name: String,
    val scene: String,
) : Node()

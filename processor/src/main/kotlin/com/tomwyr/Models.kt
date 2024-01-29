package com.tomwyr

data class Scene(
        val name: String,
        val root: Node,
)

data class Node(
        val name: String,
        val type: String,
        val children: List<Node>,
)

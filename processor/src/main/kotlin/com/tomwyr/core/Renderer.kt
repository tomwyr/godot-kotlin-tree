package com.tomwyr.core

class NodeTreeRenderer {
    fun render(packageName: String, scenes: List<Scene>): String {
        val imports = """
        |package $packageName
        |
        |import godot.*
        |import godot.core.NodePath
        |import kotlin.reflect.KProperty
        """.trimMargin()

        val sceneTrees = scenes.map { renderNode(it.root, "/root") }.joinLines(spacing = 2).ident()

        val nodeTree = """
        |object GDTree {
        |    $sceneTrees
        |}
        """.trimMargin()

        val nodeRef = """
        |open class NodeRef<T : Node>(
        |        private val path: String,
        |        private val type: String,
        |) {
        |    private lateinit var host: Node
        |
        |    private val reference by lazy {
        |        val node = host.getNode(NodePath(path)) ?: throw NodeNotFoundException(path)
        |        @Suppress("UNCHECKED_CAST")
        |        (node as? T) ?: throw NodeInvalidTypeException(type)
        |        node
        |    }
        |
        |    operator fun getValue(thisRef: Node, property: KProperty<*>): T {
        |        host = thisRef
        |        return reference
        |    }
        |}
        |
        |class NodeNotFoundException(expectedPath: String) : Exception("Node not found under given path ${'$'}expectedPath")
        |
        |class NodeInvalidTypeException(expectedType: String?) : Exception("Node is not an instance of ${'$'}expectedType")
        """.trimMargin()

        return """
        |$imports
        |
        |$nodeTree
        |
        |$nodeRef
        |
        """.trimMargin()
    }

    private fun renderNode(node: Node, parentPath: String): String {
        return if (node.children.isNotEmpty()) {
            val path = "$parentPath/${node.name}"
            val children = node.children.map { renderNode(it, path) }.joinLines().ident()

            """
            |object ${node.name} : NodeRef<${node.type}>("$path", "${node.type}") {
            |    $children
            |}
            """.trimMargin()
        } else {
            """
            |val ${node.name} = NodeRef<${node.type}>("$parentPath/${node.name}", "${node.type}")
            """.trimMargin()
        }
    }
}

private fun String.ident(times: Int = 1) = lineSequence().joinToString("\n" + "    ".repeat(times))

private fun Iterable<String>.joinLines(spacing: Int = 1): String = joinToString("\n".repeat(spacing))

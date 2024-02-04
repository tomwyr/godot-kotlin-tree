package com.tomwyr.core

class NodeTreeRenderer {
    fun render(packageName: String?, scenes: List<Scene>): String {
        val `package` = packageName?.let { "package $it" }

        val imports = """
        |import godot.*
        |import godot.core.NodePath
        |import kotlin.reflect.KProperty
        """.trimMargin()

        val sceneTrees = scenes.map { renderNode(it.root, "/root") }.joinLines(spacing = 2).indentLine()

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
        |    operator fun getValue(thisRef: Node, property: KProperty<*>): T {
        |        val node = thisRef.getNode(NodePath(path)) ?: throw NodeNotFoundException(path)
        |        @Suppress("UNCHECKED_CAST")
        |        (node as? T) ?: throw NodeInvalidTypeException(type)
        |        return node
        |    }
        |}
        |
        |class NodeNotFoundException(expectedPath: String) : Exception("Node not found under given path ${'$'}expectedPath")
        |
        |class NodeInvalidTypeException(expectedType: String?) : Exception("Node is not an instance of ${'$'}expectedType")
        """.trimMargin()

        return listOfNotNull(`package`, imports, nodeTree, nodeRef)
                .joinLines(spacing = 2).plus("\n")
    }

    private fun renderNode(node: Node, parentPath: String): String {
        val nodePath = "$parentPath/${node.name}"
        Log.renderingNode(node, nodePath)

        return if (node.children.isNotEmpty()) {
            val children = node.children.map { renderNode(it, nodePath) }.joinLines().indentLine()

            """
            |object ${node.name} : NodeRef<${node.type}>("$nodePath", "${node.type}") {
            |    $children
            |}
            """.trimMargin()
        } else {
            """
            |val ${node.name} = NodeRef<${node.type}>("$nodePath", "${node.type}")
            """.trimMargin()
        }
    }
}

private fun Iterable<String>.joinLines(spacing: Int = 1): String = joinToString("\n".repeat(spacing))

private fun String.indentLine(times: Int = 1) = lineSequence().joinToString("\n" + "    ".repeat(times))

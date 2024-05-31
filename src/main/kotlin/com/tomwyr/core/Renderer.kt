package com.tomwyr.core

class NodeTreeRenderer {
    fun render(packageName: String?, scenes: List<Scene>): String {
        val `package` = packageName?.let { "package $it" }
        val imports = renderImports()
        val nodeTree = renderNodeTree(scenes)
        val sceneNodes = scenes.map { renderScene(it) }.joinLines(spacing = 2)
        val nodeRef = renderNodeRef()

        return listOfNotNull(`package`, imports, nodeTree, sceneNodes, nodeRef)
            .joinLines(spacing = 2).plus("\n")
    }

    private fun renderImports(): String {
        return """
        |import godot.*
        |import godot.core.NodePath
        |import kotlin.reflect.KProperty
        """.trimMargin()
    }

    private fun renderNodeTree(scenes: List<Scene>): String {
        val rootNodes = scenes.map { """val ${it.name} = ${it.name}Scene("/root")""" }.joinLines().indentLine()

        return """
        |object GDTree {
        |    $rootNodes
        |}
        """.trimMargin()
    }

    private fun renderScene(scene: Scene): String {
        return when (val root = scene.root) {
            is SceneNode -> {
                val nodePath = "\$path/${root.name}"

                val header = """
                |class ${scene.name}Scene(path: String) : NodeRef<${root.type}>("$nodePath", "${root.type}")
                """.trimMargin()

                if (root.children.isNotEmpty()) {
                    val children = root.children.map { renderNode(it, nodePath) }.joinLines().indentLine()

                    """
                    |$header {
                    |    $children
                    |}
                    """.trimMargin()
                } else {
                    header
                }
            }

            is NestedScene -> {
                """
                |class ${scene.name}Scene(path: String) : ${root.scene}Scene(path)
                """.trimMargin()
            }
        }
    }

    private fun renderNode(node: Node, parentPath: String): String {
        val nodePath = "$parentPath/${node.name}"
        val symbolName = node.name
            .split("\\s+".toRegex())
            .mapIndexed { index: Int, s: String -> if (index == 0) s else s.capitalize() }
            .joinToString("")

        Log.renderingNode(node, nodePath)

        return when (node) {
            is SceneNode -> if (node.children.isNotEmpty()) {
                val children = node.children.map { renderNode(it, nodePath) }.joinLines().indentLine()

                """
                |object $symbolName : NodeRef<${node.type}>("$nodePath", "${node.type}") {
                |    $children
                |}
                """.trimMargin()
            } else {
                """
                |val $symbolName = NodeRef<${node.type}>("$nodePath", "${node.type}")
                """.trimMargin()
            }

            is NestedScene -> {
                """
                |val $symbolName = ${node.scene}Scene("$nodePath")
                """.trimMargin()
            }
        }
    }

    private fun renderNodeRef(): String {
        return """
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
    }
}

private fun Iterable<String>.joinLines(spacing: Int = 1): String = joinToString("\n".repeat(spacing))

private fun String.indentLine(times: Int = 1) = lineSequence().joinToString("\n" + "    ".repeat(times))

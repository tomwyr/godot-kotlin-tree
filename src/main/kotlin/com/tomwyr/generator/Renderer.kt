package com.tomwyr.generator

import com.tomwyr.common.*
import com.tomwyr.utils.capitalize

class NodeTreeRenderer {
    fun render(packageName: String?, scenes: List<Scene>): String {
        val header = renderHeader(packageName)
        val nodeTree = renderNodeTree(scenes)
        val sceneNodes = scenes.map { renderScene(it) }.joinLines(spacing = 2)
        val types = renderTypes()

        return listOf(header, nodeTree, sceneNodes, types)
            .joinLines(spacing = 2).trimBlankLines().plus("\n")
    }

    private fun renderHeader(packageName: String?): String {
        val packageOrEmpty = packageName?.let { "package $it" } ?: ""

        return """
        |$packageOrEmpty
        |
        |import godot.*
        |import godot.core.NodePath
        |import kotlin.reflect.KProperty
        """.trimMargin().trim()
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
        val nodePath = "\$path/${scene.root.name}"

        return when (val root = scene.root) {
            is ParentNode -> renderParentNode(
                node = root,
                nodePath = nodePath,
                className = "${scene.name}Scene",
                nestedClass = false,
            )

            is LeafNode -> """
            |class ${scene.name}Scene(private val path: String) : NodeRef<${root.type}>("$nodePath", "${root.type}")
            """.trimMargin()

            is NestedScene -> """
            |class ${scene.name}Scene(private val path: String) : ${root.scene}Scene(path)
            """.trimMargin()
        }
    }

    private fun renderNode(node: Node, parentPath: String): RenderNodeResult {
        val nodePath = "$parentPath/${node.name}"
        val symbolName = node.name
            .split("\\s+".toRegex())
            .mapIndexed { index: Int, s: String -> if (index == 0) s else s.capitalize() }
            .joinToString("")

        val field = when (node) {
            is ParentNode -> """
            |val $symbolName = ${symbolName}Tree()
            """.trimMargin()

            is LeafNode -> """
            |val $symbolName = NodeRef<${node.type}>("$nodePath", "${node.type}")
            """.trimMargin()

            is NestedScene -> """
            |val $symbolName = ${node.scene}Scene("$nodePath")
            """.trimMargin()
        }

        val nestedClass = when (node) {
            is ParentNode -> renderParentNode(
                node = node,
                nodePath = "$parentPath/${node.name}",
                className = "${symbolName}Tree",
                nestedClass = true,
            )

            else -> null
        }

        return RenderNodeResult(field, nestedClass)
    }

    private fun renderParentNode(
        node: ParentNode,
        nodePath: String,
        className: String,
        nestedClass: Boolean,
    ): String {
        val (classType, constructor) = when (nestedClass) {
            true -> "inner class" to ""
            false -> "class" to "(private val path: String)"
        }
        val header = """
        |$classType $className$constructor : NodeRef<${node.type}>("$nodePath", "${node.type}")
        """.trimMargin()

        val children = node.children.map { child -> renderNode(child, nodePath) }
        val fields = children.map { it.field }.joinLines().indentLine()
        val nestedClasses = children.mapNotNull { it.nestedClass }.joinLines(spacing = 2).indentLine()

        val body = """
        |    $fields
        |
        |    $nestedClasses
        """.trimMargin().trimEnd()

        return """
        |$header {
        |$body
        |}
        """.trimMargin()
    }

    private fun renderTypes(): String {
        return """
        |open class NodeRef<T : Node>(
        |    private val path: String,
        |    private val type: String,
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

private fun String.indentLine(times: Int = 1): String = lineSequence().joinToString("\n" + "    ".repeat(times))

private fun String.trimBlankLines(): String = lineSequence().joinToString("\n") { it.ifBlank { "" } }

private data class RenderNodeResult(val field: String, val nestedClass: String?)

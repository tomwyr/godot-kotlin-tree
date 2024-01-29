package com.tomwyr

class NodeTreeRenderer {
    fun render(packageName: String, scenes: List<Scene>): String {
        val imports = """
        |import godot.*
        |import godot.core.NodePath
        |import godot.global.GD
        |import kotlin.reflect.KClass
        """.trimMargin()

        val treeExtension = """
        |fun GD.Tree(node: Node): GDTree = nodeTree(node)
        """.trimMargin()

        val nodeExtension = """
        |private inline fun <reified T : Node> Node.ref(path: String): T {
        |    val node = getNode(NodePath(path)) ?: throw NodeNotFoundException(path)
        |    (node as? T) ?: throw NodeInvalidTypeException(T::class)
        |    return node
        |}
        |
        |class NodeNotFoundException(path: String) : Exception("Node not found under given path ${'$'}path")
        |
        |class NodeInvalidTypeException(type: KClass<*>) : Exception("Node is not an instance of ${'$'}{type.simpleName}")
        """.trimMargin()

        val sceneObjects = scenes.joinToString("\n\n") { scene ->
            val nodes = scene.nodes.joinToString("\n") { node ->
                """
                |        override val ${node.name}: ${node.type} = node.ref("/root/${node.name}")
                """.trimMargin()
            }

            """
            |    override val ${scene.name} = object : ${scene.name}Tree {
            |$nodes
            |    }
            """.trimMargin()
        }
        
        val rootObject = """
        |private fun nodeTree(node: Node): GDTree = object : GDTree {
        |$sceneObjects
        |}
        """.trimMargin()

        val rootInterface = run {
            val nodes = scenes.joinToString("\n") { scene ->
                """
                |    val ${scene.name}: ${scene.name}Tree
                """.trimMargin()
            }

            """
            |interface GDTree {
            |$nodes
            |}
            """.trimMargin()
        }

        val sceneInterfaces = scenes.joinToString("\n\n") { scene ->
            val nodes = scene.nodes.joinToString("\n") { node ->
                """
                |    val ${node.name}: ${node.type}
                """.trimIndent()
            }

            """
            |interface ${scene.name}Tree {
            |$nodes
            |}
            """.trimMargin()
        }

        return """
        |package $packageName
        |
        |$imports
        |
        |$treeExtension
        |
        |$nodeExtension
        |
        |$rootObject
        |
        |$rootInterface
        |
        |$sceneInterfaces
        |
        """.trimMargin()
    }
}

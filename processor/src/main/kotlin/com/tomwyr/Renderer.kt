package com.tomwyr

class NodeTreeRenderer {
    fun render(packageName: String, scenes: List<Scene>): String {
        val extension = """
        |val GD.Tree: GDTree
        |    get() = nodeTree
        """.trimMargin()

        val sceneObjects = scenes.joinToString("\n\n") { scene ->
            val nodes = scene.nodes.joinToString("\n\t\t") { node ->
                "override val ${node.name} = \"${node.type}\""
            }

            """
            |    override val ${scene.name} = object : ${scene.name}Tree {
            |        $nodes
            |    }
            """.trimMargin()
        }
        
        val rootObject = """
        |private val nodeTree = object : GDTree {
        |$sceneObjects
        |}
        """.trimMargin()

        val rootInterface = run {
            val nodes = scenes.joinToString("\n\t") { scene ->
                "val ${scene.name}: ${scene.name}Tree"
            }

            """
            |interface GDTree {
            |    $nodes
            |}
            """.trimMargin()
        }

        val sceneInterfaces = scenes.joinToString("\n\n") { scene ->
            val nodes = scene.nodes.joinToString("\n\t") { node ->
                "val ${node.name}: String"
            }

            """
            |interface ${scene.name}Tree {
            |    $nodes
            |}
            """.trimMargin()
        }

        return """
        |package $packageName
        |
        |import godot.global.GD
        |
        |$extension
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

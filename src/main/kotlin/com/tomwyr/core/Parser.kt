package com.tomwyr.core

class SceneNodesParser {
    fun parse(sceneFileContent: String): Node {
        return sceneFileContent
                .let(::splitToNodeLines)
                .map(::extractParamsLine)
                .map(::parseParamsLine)
                .mapNotNull(::createNodeParams)
                .let(::createRootNode)
    }

    private fun splitToNodeLines(data: String): List<String> {
        return data.split("\n").filter { line ->
            line.startsWith("[node") && line.endsWith("]")
        }.also(Log::parsedSceneNodes)
    }

    private fun extractParamsLine(line: String): String {
        Log.parsingNode(line)
        val nodePattern = "^\\[node(.*)]$".toRegex()
        val groups = nodePattern.find(line)?.groups?.filterNotNull() ?: emptyList()
        val paramsLine = groups.takeIf { it.size == 2 }?.last()?.value
        return paramsLine ?: throw UnexpectedNodeFormat(line, "entry")
    }

    private fun parseParamsLine(paramsLine: String): Map<String, String> {
        val segments = paramsLine.trim().replace("  ", " ").split(" ")
        return segments.associate(::parseParamSegment)
    }

    private fun parseParamSegment(segment: String): Pair<String, String> {
        val paramPattern = "^(.+)=(.+)$".toRegex()
        val groups = paramPattern.find(segment)?.groups?.filterNotNull() ?: emptyList()
        val keyAndValue = groups.takeIf { it.size == 3 }
        keyAndValue ?: throw UnexpectedNodeFormat(segment, "segment")
        return keyAndValue.drop(1).map { it.value }.let { it[0] to it[1] }
    }

    private fun createNodeParams(params: Map<String, String>): NodeParams? {
        val name = params["name"]
        val type = params["type"]
        val parent = params["parent"]

        if (name == null || type == null) {
            Log.skippingNode()
            return null
        }

        val sanitize = { item: String ->
            if (!item.startsWith('\"') || !item.endsWith('\"')) {
                throw UnexpectedNodeFormat(item, "value")
            }
            item.trim('\"')
        }

        return NodeParams(
                name = sanitize(name),
                type = sanitize(type),
                parent = parent?.let(sanitize),
        )
    }

    private fun createRootNode(params: List<NodeParams>): Node {
        Log.creatingRootNode()
        val childrenByParent = params.groupBy { it.parent }
        val rootParams = params.single { it.parent == null }
        return rootParams.toNode(childrenByParent)
    }
}

private data class NodeParams(
        val name: String,
        val type: String,
        val parent: String?,
) {

    fun toNode(childrenByParent: Map<String?, List<NodeParams>>): Node {
        val childrenKey = when (parent) {
            null -> "."
            "." -> name
            else -> "$parent/$name"
        }

        val children = childrenByParent
                .getOrDefault(childrenKey, emptyList())
                .map { it.toNode(childrenByParent) }

        return Node(name = name, type = type, children = children)
    }
}

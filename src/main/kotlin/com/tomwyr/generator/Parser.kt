package com.tomwyr.generator

import com.tomwyr.core.*
import com.tomwyr.utils.capitalize

class SceneNodesParser {
    fun parse(sceneFileContent: String): Node {
        val scenePathsById = ScenesParser().parse(sceneFileContent)
        return NodesParser(scenePathsById).parse(sceneFileContent)
    }
}

private class ScenesParser {
    fun parse(sceneFileContent: String): Map<String, String> = sceneFileContent
        .let(::splitToSceneLines)
        .map(::parseSceneParams)
        .mapNotNull(::getSceneIdToPath)
        .let(::getScenePathsById)

    private fun splitToSceneLines(data: String): List<String> {
        return data.split("\n").filter { line ->
            line.startsWith("[ext_resource type=\"PackedScene\"") && line.endsWith("]")
        }.also(Log::parsedSceneResources)
    }

    private fun parseSceneParams(line: String): Map<String, String> {
        Log.parsingSceneResource(line)

        val pattern = "^\\[ext_resource (.*)]$".toRegex()
        val groups = pattern.find(line)?.groups?.filterNotNull() ?: emptyList()
        val paramsLine = groups.takeIf { it.size == 2 }?.last()?.value
        paramsLine ?: throw UnexpectedResourceFormat(line)

        val paramPattern = "\\w+=\".+?\"".toRegex()
        val segments = paramPattern.findAll(paramsLine).map { it.value }.toList()
        return segments.associate(::parseParamSegment)
    }


    private fun getSceneIdToPath(params: Map<String, String>): Pair<String, String>? {
        val id = params.extract("id")
        val path = params.extract("path")

        if (id == null || path == null) {
            Log.skippingSceneResource()
            return null
        }

        return id to path
    }

    private fun getScenePathsById(sceneIdsToPaths: List<Pair<String, String>>): Map<String, String> {
        val duplicates = sceneIdsToPaths
            .groupBy { it.first }
            .filter { it.value.size > 1 }
            .mapValues { it.value.map { idToPath -> idToPath.second } }

        if (duplicates.isNotEmpty()) {
            throw DuplicatedSceneResources(duplicates)
        }

        return sceneIdsToPaths.toMap()
    }
}

private class NodesParser(val scenePathsById: Map<String, String>) {
    fun parse(sceneFileContent: String): Node = sceneFileContent
        .let(::splitToNodeLines)
        .map(::parseNodeParams)
        .mapNotNull(::createNodeParams)
        .let(::createRootNode)


    private fun splitToNodeLines(data: String): List<String> {
        return data.split("\n").filter { line ->
            line.startsWith("[node") && line.endsWith("]")
        }.also(Log::parsedSceneNodes)
    }

    private fun parseNodeParams(line: String): Map<String, String> {
        Log.parsingNode(line)

        val pattern = "^\\[node (.*)]$".toRegex()
        val groups = pattern.find(line)?.groups?.filterNotNull() ?: emptyList()
        val paramsLine = groups.takeIf { it.size == 2 }?.last()?.value
        paramsLine ?: throw UnexpectedNodeFormat(line)

        val paramPattern = "\\w+=(\".+?\"|\\w+\\(\".+\"\\))".toRegex()
        val segments = paramPattern.findAll(paramsLine).map { it.value }.toList()
        return segments.associate(::parseParamSegment)
    }


    private fun createNodeParams(params: Map<String, String>): NodeParams? {
        val name = params.extract("name")
        val type = params.extract("type")
        val instance = params.extractResource("instance")
        val parent = params.extract("parent")

        if (name == null || (type == null && instance == null) || (type != null && instance != null)) {
            Log.skippingNode()
            return null
        }

        return NodeParams(name = name, type = type, instance = instance, parent = parent)
    }

    private fun createRootNode(params: List<NodeParams>): Node {
        Log.creatingRootNode()
        val childrenByParent = params.groupBy { it.parent }
        val rootParams = params.single { it.parent == null }
        return rootParams.toNode(childrenByParent, scenePathsById)
    }
}

data class NodeParams(
    val name: String,
    val type: String?,
    val instance: String?,
    val parent: String?,
) {
    fun toNode(childrenByParent: Map<String?, List<NodeParams>>, scenePathsById: Map<String, String>): Node {
        return when {
            type != null && instance == null -> {
                val childrenKey = when (parent) {
                    null -> "."
                    "." -> name
                    else -> "$parent/$name"
                }

                val children = childrenByParent
                    .getOrDefault(childrenKey, emptyList())
                    .map { it.toNode(childrenByParent, scenePathsById) }

                if (children.isNotEmpty()) {
                    ParentNode(name = name, type = type, children = children)
                } else {
                    LeafNode(name = name, type = type)
                }
            }

            type == null && instance != null -> {
                val pattern = "^res://(.*).tscn$".toRegex()
                val scenePath = scenePathsById[instance] ?: throw UnexpectedSceneResource(instance)
                val scene = pattern.find(scenePath)?.groupValues?.getOrNull(1)?.capitalize()
                scene ?: throw UnexpectedNodeFormat(scenePath)
                NestedScene(name = name, scene = scene)
            }

            else -> throw UnexpectedNodeParameters(this)
        }
    }
}

private fun parseParamSegment(segment: String): Pair<String, String> {
    val paramPattern = "^(.+)=(.+)$".toRegex()
    val groups = paramPattern.find(segment)?.groups?.filterNotNull() ?: emptyList()
    return groups.map { it.value }.let { it[1] to it[2] }
}

private fun Map<String, String>.extract(key: String): String? {
    return this[key]?.trim('\"')
}

private fun Map<String, String>.extractResource(key: String): String? {
    return this[key]?.removePrefix("ExtResource(\"")?.removeSuffix("\")")
}

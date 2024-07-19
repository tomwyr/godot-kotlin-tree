package com.tomwyr.generator

import com.tomwyr.common.*
import com.tomwyr.utils.capitalize

class SceneNodesParser {
    fun parse(sceneData: SceneData): Node {
        val scenePathsById = ScenesParser().parse(sceneData)
        return NodesParser(scenePathsById).parse(sceneData)
    }
}

private class ScenesParser {
    fun parse(sceneData: SceneData): Map<String, String> {
        Log.parsingScenePaths()
        return splitToEntries(sceneData.content, "ext_resource")
            .map(::parseEntryParams)
            .mapNotNull(::extractSceneIdToPath)
            .let(::getScenePathsById)
    }

    private fun extractSceneIdToPath(params: Map<String, String>): Pair<String, String>? {
        val id = params["id"]
        val path = params["path"]

        if (id == null || path == null) {
            Log.skippingSceneResource(params)
            return null
        }

        return id to path
    }

    private fun getScenePathsById(sceneIdsToPaths: List<Pair<String, String>>): Map<String, String> {
        val duplicates = sceneIdsToPaths
            .groupBy({ it.first }, { it.second })
            .filter { it.value.size > 1 }

        if (duplicates.isNotEmpty()) {
            Log.duplicatedSceneResources(duplicates)
        }

        return sceneIdsToPaths.toMap()
    }
}

private class NodesParser(val scenePathsById: Map<String, String>) {
    fun parse(sceneData: SceneData): Node {
        Log.parsingSceneNodes()
        return splitToEntries(sceneData.content, "node")
            .map(::parseEntryParams)
            .mapNotNull(::extractNodeParams)
            .let { createRootNode(sceneData.name, it) }
    }


    private fun extractNodeParams(params: Map<String, String>): NodeParams? {
        val name = params["name"]
        val type = params["type"]
        val instance = params["instance"]
        val parent = params["parent"]

        if (name == null || (type == null && instance == null) || (type != null && instance != null)) {
            Log.skippingNode(params)
            return null
        }

        return NodeParams(name = name, type = type, instance = instance, parent = parent)
    }

    private fun createRootNode(sceneName: String, params: List<NodeParams>): Node {
        Log.creatingRootNode()
        val childrenByParent = params.groupBy { it.parent }
        val rootParams = params.firstOrNull { it.parent == null } ?: throw ParentNodeNotFound(sceneName)
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
                val scene = scenePathsById[instance]?.let(::parseSceneName)?.capitalize()
                scene ?: throw UnexpectedSceneResource(instance)
                NestedScene(name = name, scene = scene)
            }

            else -> throw UnexpectedNodeParameters(this)
        }
    }
}

private fun splitToEntries(data: String, entryType: String): List<String> {
    Log.splittingEntries(entryType)
    val pattern = """\[${entryType} .*]""".toRegex()
    return pattern.findAll(data).mapNotNull { it.groupValues.firstOrNull() }.toList()
}

private fun parseEntryParams(entry: String): Map<String, String> {
    Log.parsingEntryParams(entry)
    val pattern = """(?:(\w+)=(?:\w+\("(.+)"\)|"(.+?)"))+""".toRegex()
    return pattern.findAll(entry).associate { match ->
        val (key, argumentValue, plainValue) = match.destructured
        val value = argumentValue.ifEmpty { plainValue }
        key to value
    }
}

private fun parseSceneName(scenePath: String): String? {
    Log.parsingEntryParams(scenePath)
    val pattern = """^res://(.*).tscn$""".toRegex()
    return pattern.find(scenePath)?.groupValues?.getOrNull(1)
}

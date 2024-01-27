package com.tomwyr

import com.google.devtools.ksp.processing.KSPLogger

class SceneNodesParser(private val logger: KSPLogger) {
    fun parse(projectFileContent: String): List<Node> {
        return projectFileContent
                .let(::splitToNodeLines)
                .map(::extractParamsLine)
                .map(::parseParamsLine)
                .mapNotNull(::createNodeEntry)
    }

    private fun splitToNodeLines(data: String): List<String> {
        return data.split("\n").filter { line ->
            line.startsWith("[node") && line.endsWith("]")
        }
    }

    private fun extractParamsLine(line: String): String {
        logger.warn("Parsing $line")
        val nodePattern = "^\\[node(.*)]$".toRegex()
        val groups = nodePattern.find(line)?.groups?.filterNotNull() ?: emptyList()
        val paramsLine = groups.takeIf { it.size == 2 }?.last()?.value
        return paramsLine ?: throw UnexpectedNodeFormat(line)
    }

    private fun parseParamsLine(paramsLine: String): Map<String, String> {
        val segments = paramsLine.replace("  ", " ").split(" ")
        return segments.mapNotNull(::parseParamSegment).toMap()
    }

    private fun parseParamSegment(segment: String): Pair<String, String>? {
        val paramPattern = "^(.+)=(.+)$".toRegex()
        val groups = paramPattern.find(segment)?.groups?.filterNotNull() ?: emptyList()
        val keyAndValue = groups.takeIf { it.size == 3 }?.drop(1)?.map { it.value }
        return keyAndValue?.let { it[0] to it[1] }
    }

    private fun createNodeEntry(params: Map<String, String>): Node? {
        val name = params["name"] ?: return null
        val type = params["type"] ?: return null

        val sanitize = { item: String ->            
            if (!item.startsWith('\"') || !item.endsWith('\"')) {
                throw UnexpectedNodeFormat(item)
            }
            item.trim('\"')
        }

        return Node(sanitize(name), sanitize(type))
    }
}

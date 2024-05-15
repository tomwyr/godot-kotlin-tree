package com.tomwyr.core

import kotlin.math.max

class NodeTreeInfo(
        val nodes: Int,
        val depth: Int,
        val scenes: Int,
) {
    companion object {
        operator fun invoke(scenes: List<Scene>): NodeTreeInfo {
            var nodes = 0
            var depth = 0

            for (scene in scenes) {
                nodes += scene.nodesCount
                depth = max(depth, scene.nodesDepth)
            }

            return NodeTreeInfo(
                    nodes = nodes,
                    depth = depth,
                    scenes = scenes.size,
            )
        }
    }
}

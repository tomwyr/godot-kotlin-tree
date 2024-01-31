package com.tomwyr.core

import java.io.File

interface Logger {
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
}

object Log : ProcessorLog, ParserLog, RendererLog, GeneratorLog, Logger {
    lateinit var logger: Logger

    override fun debug(message: String) = logger.debug(message)
    override fun info(message: String) = logger.info(message)
    override fun warn(message: String) = logger.warn(message)
}

interface ProcessorLog : Logger {
    fun multipleSymbolsAnnotated() {
        warn("More than a single annotation found in the project")
        warn("Generating node tree for the first symbol found")
    }

    fun kotlinProjectPath(rootPath: String) {
        info("Kotlin project found under $rootPath")
    }

    fun godotCustomProjectPath(projectRelativePath: String) {
        info("Custom path set as the Godot project relative path $projectRelativePath")
    }

    fun godotProjectPath(projectPath: String) {
        info("Godot project found under $projectPath")
    }

    fun targetPackage(targetPackage: String) {
        info("Package for which the node tree will be generated identified as $targetPackage")
    }

    fun nodeTreeGenerated(stats: NodeTreeStats) {
        info("Node tree generated successfully!")
        info("Scenes number: ${stats.scenes}")
        info("Nodes total: ${stats.nodes}")
        info("Tree depth: ${stats.depth}")
    }
}

interface ParserLog : Logger {
    fun parsedSceneNodes(nodes: List<String>) {
        info("${nodes.size} nodes found in the scene")
    }

    fun parsingNode(line: String) {
        debug("Parsing node $line")
    }

    fun skippingNode() {
        info("Skipping node missing at least one of the required keys")
    }

    fun creatingRootNode() {
        info("Creating node tree structure for the extracted nodes")
    }
}

interface RendererLog : Logger {
    fun renderingNode(node: Node, nodePath: String) {
        debug("Rendering $nodePath node of type ${node.type}")
    }
}

interface GeneratorLog : Logger {
    fun readingScenes() {
        info("Reading scenes from the Godot project")
    }

    fun scenesFound(sceneFiles: List<File>) {
        info("${sceneFiles.size} scene files found")
    }

    fun parsingScene(file: File) {
        info("Parsing scene ${file.name}")
    }

    fun renderingNodeTree() {
        info("Rendering node tree for the found scene(s)")
    }

    fun savingResult() {
        info("Writing generated code to the output file")
    }

    fun resultSaved() {
        info("Output file saved")
    }
}

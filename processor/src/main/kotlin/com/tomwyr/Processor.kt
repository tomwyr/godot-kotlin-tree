package com.tomwyr

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.validate
import java.io.File

class GodotNodeTreeProcessor(
        private val logger: KSPLogger,
        private val generator: CodeGenerator,
        private val options: Map<String, String>,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {

        val symbols = resolver.getSymbolsWithAnnotation("com.tomwyr.GodotNodeTree")
        val symbol = symbols.firstOrNull() ?: return emptyList()
        val symbolPath = (symbol.location as? FileLocation)?.filePath

        symbolPath ?: throw UnknownAnnotationLocationException()

        val rootPath = symbolPath.split("/src/")
                .takeIf { it.size > 1 }
                ?.let { it.toMutableList().apply { removeLast() } }
                ?.joinToString("")

        val godotProjectPath = options["godotProjectPath"]
        val godotProjectAbsolutePath = rootPath?.let {
            var path = it.removeSuffix("/")
            path += "/"
            if (godotProjectPath != null) {
                path += godotProjectPath.removePrefix("/")
            }
            path
        }.takeIf { dir -> File("$dir/project.godot").exists() }

        godotProjectAbsolutePath ?: throw InvalidGodotProjectException()

        val sceneFiles = File(godotProjectAbsolutePath).walkTopDown().filter { it.path.endsWith(".tscn") }

        val scenes = sceneFiles.map { file ->
            val name = file.nameWithoutExtension
            val nodes = SceneNodesParser(logger).parse(file.readText())
            Scene(name, nodes)
        }.toList()

        scenes.forEach {
            logger.warn("[${it.name}]")
            logger.warn(it.nodes.joinToString("\n") { "${it.name} ${it.type}" })
            logger.warn("\n")
        }


        return symbols.filter { !it.validate() }.toList()
    }
}

package com.tomwyr

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.*
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
        val symbolFile = symbol.containingFile ?: throw UnknownAnnotationLocationException()

        symbol.containingFile?.packageName?.asString().let {
            println(it)
        }

        val rootPath = symbolFile.filePath.split("/src/")
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
            val root = SceneNodesParser(logger).parse(file.readText())
            Scene(name, root)
        }.toList()

        val annotationPackage = symbolFile.packageName.asString()

        val content = NodeTreeRenderer().render(annotationPackage, scenes)

        val file = generator.createNewFile(
                dependencies = Dependencies(false, symbolFile),
                packageName = "com.tomwyr",
                fileName = "GodotNodeTree",
        )

        file.run {
            write(content.toByteArray())
            close()
        }

        return symbols.filter { !it.validate() }.toList()
    }
}

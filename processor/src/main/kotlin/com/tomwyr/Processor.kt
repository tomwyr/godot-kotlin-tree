package com.tomwyr

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import com.tomwyr.core.*
import java.io.OutputStream

class GodotNodeTreeProcessor(
        private val logger: KSPLogger,
        private val generator: CodeGenerator,
        private val options: Map<String, String>,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.tomwyr.GodotNodeTree")
        if (!symbols.any()) return emptyList()

        val symbolFile = symbols.first().containingFile ?: throw UnknownAnnotationLocationException()
        generateTree(symbolFile)

        return symbols.filter { !it.validate() }.toList()
    }

    private fun generateTree(symbolFile: KSFile) {
        val rootPath = resolveRootPath(symbolFile.filePath)
        val projectRelativePath = options["godotProjectPath"]
        val targetPackage = symbolFile.packageName.asString()

        val projectPath = getProjectPath(rootPath, projectRelativePath)

        NodeTreeGenerator(
                renderer = NodeTreeRenderer(),
                parser = SceneNodesParser(logger),
        ).generate(projectPath, targetPackage) {
            createOutputFile(symbolFile)
        }
    }

    private fun createOutputFile(symbolFile: KSFile): OutputStream {
        return generator.createNewFile(
                dependencies = Dependencies(false, symbolFile),
                packageName = "com.tomwyr",
                fileName = "GodotNodeTree",
        )
    }

    private fun resolveRootPath(symbolPath: String): String {
        return symbolPath.split("/src/").toMutableList()
                .apply { removeLast() }
                .joinToString("")
    }
}

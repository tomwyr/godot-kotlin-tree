package com.tomwyr

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.validate
import com.tomwyr.core.Log
import com.tomwyr.core.NodeTreeGenerator
import com.tomwyr.core.UnknownAnnotationLocationException
import com.tomwyr.core.getProjectPath
import java.io.OutputStream

class GodotNodeTreeProcessor(
        private val generator: CodeGenerator,
        private val options: Map<String, String>,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.tomwyr.GodotNodeTree")
        if (!symbols.any()) return emptyList()

        if (symbols.count() > 1) {
            Log.multipleSymbolsAnnotated()
        }

        val symbolFile = symbols.first().containingFile
        symbolFile ?: throw UnknownAnnotationLocationException()
        generateTree(symbolFile)

        return symbols.filter { !it.validate() }.toList()
    }

    private fun generateTree(symbolFile: KSFile) {
        val rootPath = resolveRootPath(symbolFile.filePath)
        Log.kotlinProjectPath(rootPath)

        val projectRelativePath = options["godotProjectPath"]
        if (projectRelativePath != null) {
            Log.godotCustomProjectPath(projectRelativePath)
        }

        val targetPackage = symbolFile.packageName.asString()
        Log.targetPackage(targetPackage)

        val projectPath = getProjectPath(rootPath, projectRelativePath)
        Log.godotProjectPath(projectPath)

        val stats = NodeTreeGenerator().generate(
                projectPath,
                targetPackage,
                createFile = { createOutputFile(symbolFile) }
        )
        Log.nodeTreeGenerated(stats)
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

package com.tomwyr.utils

import com.tomwyr.core.InvalidGodotProjectException
import java.io.File
import java.nio.file.FileSystems

fun getProjectPath(rootPath: String, relativePath: String?): String {
    val path = StringBuffer().apply {
        append(rootPath.removeSuffix(sep))
        append(sep)
        if (relativePath != null) {
            append(relativePath.removeSuffix(sep))
        }
    }.toString()

    val projectFilePath = "$path/project.godot".sysSepd
    File(projectFilePath).run {
        if (!exists()) throw InvalidGodotProjectException()
    }

    return path
}

fun getOutputPath(rootPath: String, targetPackage: String?): String {
    val buildPath = "build/generated/godotNodeTree/kotlin".sysSepd
    val fileName = "GodotNodeTree.kt"
    val packagePath = targetPackage?.replace(".", sep)

    return StringBuffer().apply {
        append("$rootPath/$buildPath")
        if (packagePath != null) append("/$packagePath")
        append("/$fileName")
    }.toString().sysSepd
}

private val String.sysSepd: String
    get() = replace("/", sep)

private val sep = FileSystems.getDefault().separator

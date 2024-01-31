package com.tomwyr.core

import java.io.File

fun getProjectPath(rootPath: String, projectRelativePath: String?): String {
    var path = rootPath.removeSuffix("/")
    path += "/"
    if (projectRelativePath != null) {
        path += projectRelativePath.removePrefix("/")
    }

    File("$path/project.godot").run {
        if (!exists()) throw InvalidGodotProjectException()
    }

    return path
}

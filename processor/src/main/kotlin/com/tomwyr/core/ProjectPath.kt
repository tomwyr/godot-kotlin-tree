package com.tomwyr.core

import java.io.File

fun getProjectPath(rootPath: String, projectRelativePath: String?): String {
    var path = rootPath.removeSuffix("/")
    path += "/"
    if (projectRelativePath != null) {
        path += projectRelativePath.removePrefix("/")
    }
    path = "$path/project.godot"

    if (!File(path).exists()) {
        throw InvalidGodotProjectException()
    }

    return path
}

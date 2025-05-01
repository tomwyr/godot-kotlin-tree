package com.tomwyr.common

import kotlinx.serialization.Serializable

sealed class GodotKotlinTreeError : Exception() {
    override fun getLocalizedMessage(): String = when (this) {
        is InvalidGodotProject -> "The project in which GodotNodeTree annotation was used isn't a valid Godot project directory"
        is GeneratorError -> error.localizedMessage
    }
}

class InvalidGodotProject : GodotKotlinTreeError()

class GeneratorError(val error: GodotNodeTreeError) : GodotKotlinTreeError()

@Serializable
sealed class GodotNodeTreeError : Exception() {
    override fun getLocalizedMessage(): String = when (this) {
        is ScanningScenesFailed -> "Unable to scan scene files for project at `$projectPath`."
        is ReadingSceneFailed -> "Unable to read contens of scene at `$scenePath`."
        is UnexpectedNodeParameters -> "A node with unexpected set of parameters encountered: $nodeParams."
        is UnexpectedSceneResource -> "A node pointing to an unknown scene resource encountered with id: $instance."
        is ParentNodeNotFound -> "None of the parsed nodes was identified as the parent node of scene $sceneName."
    }
}

@Serializable
class ScanningScenesFailed(val projectPath: String) : GodotNodeTreeError()

@Serializable
class ReadingSceneFailed(val scenePath: String) : GodotNodeTreeError()

@Serializable
class UnexpectedNodeParameters(val nodeParams: NodeParams) : GodotNodeTreeError()

@Serializable
class UnexpectedSceneResource(val instance: String) : GodotNodeTreeError()

@Serializable
class ParentNodeNotFound(val sceneName: String) : GodotNodeTreeError()

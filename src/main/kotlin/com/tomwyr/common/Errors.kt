@file:OptIn(ExperimentalSerializationApi::class)

package com.tomwyr.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

sealed class GodotKotlinTreeError : Exception() {
    override fun getLocalizedMessage(): String = when (this) {
        is GeneratorError -> error.localizedMessage
    }
}

class GeneratorError(val error: GodotNodeTreeError) : GodotKotlinTreeError()

@Serializable
@JsonClassDiscriminator("errorType")
sealed class GodotNodeTreeError : Exception() {
    override fun getLocalizedMessage(): String = when (this) {
        is InvalidGodotProject -> "Godot project could not be found at path `$projectPath`."
        is ScanningScenesFailed -> "Unable to scan scene files for project at `$projectPath`."
        is ReadingSceneFailed -> "Unable to read contens of scene at `$scenePath`."
        is UnexpectedNodeParameters -> "A node with unexpected set of parameters encountered: $nodeParams."
        is UnexpectedSceneResource -> "A node pointing to an unknown scene resource encountered with id: $instance."
        is ParentNodeNotFound -> "None of the parsed nodes was identified as the parent node of scene $sceneName."
    }
}

@Serializable
@SerialName("invalidGodotProject")
class InvalidGodotProject(val projectPath: String) : GodotNodeTreeError()

@Serializable
@SerialName("scanningScenesFailed")
class ScanningScenesFailed(val projectPath: String) : GodotNodeTreeError()

@Serializable
@SerialName("readingSceneFailed")
class ReadingSceneFailed(val scenePath: String) : GodotNodeTreeError()

@Serializable
@SerialName("unexpectedNodeParameters")
class UnexpectedNodeParameters(val nodeParams: NodeParams) : GodotNodeTreeError()

@Serializable
@SerialName("unexpectedSceneResource")
class UnexpectedSceneResource(val instance: String) : GodotNodeTreeError()

@Serializable
@SerialName("parentNodeNotFound")
class ParentNodeNotFound(val sceneName: String) : GodotNodeTreeError()

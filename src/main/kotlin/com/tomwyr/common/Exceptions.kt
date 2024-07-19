package com.tomwyr.common

import com.tomwyr.generator.NodeParams

sealed class GeneratorException(message: String) : Exception(message)

class InvalidGodotProject :
    GeneratorException("The project in which GodotNodeTree annotation was used isn't a valid Godot project directory")

class UnexpectedNodeParameters(nodeParams: NodeParams) :
    GeneratorException("A node with unexpected set of parameters encountered: $nodeParams")

class UnexpectedSceneResource(instance: String) :
    GeneratorException("A node pointing to an unknown scene resource encountered with id: $instance")

class ParentNodeNotFound(sceneName: String) :
    GeneratorException("None of the parsed nodes was identified as the parent node of scene $sceneName")

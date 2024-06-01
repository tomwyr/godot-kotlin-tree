package com.tomwyr.core

import com.tomwyr.generator.NodeParams

class InvalidGodotProjectException :
    Exception("The project in which GodotNodeTree annotation was used isn't a valid Godot project directory")

class UnexpectedNodeFormat(nodeLine: String) :
    Exception("A node with unexpected format encountered: $nodeLine")

class UnexpectedNodeParameters(nodeParams: NodeParams) :
    Exception("A node with unexpected set of parameters encountered: $nodeParams")

class UnexpectedResourceFormat(resourceLine: String) :
    Exception("A resource with unexpected format encountered: $resourceLine")

class UnexpectedSceneResource(instance: String) :
    Exception("A node pointing to an unknown scene resource encountered with id: $instance")

class DuplicatedSceneResources(duplicates: Map<String, List<String>>) :
    Exception("Found multiple scenes for following resource ids: $duplicates")

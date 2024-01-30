package com.tomwyr.core

class UnknownAnnotationLocationException : Exception("The location of GodotNodeTree annotation couldn't be resolved")

class InvalidGodotProjectException :
        Exception("The project in which GodotNodeTree annotation was used isn't a valid Godot project directory")

class UnexpectedNodeFormat(input: String) : Exception("A node group with unexpected format encountered: $input")

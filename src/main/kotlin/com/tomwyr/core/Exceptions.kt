package com.tomwyr.core

class InvalidGodotProjectException :
        Exception("The project in which GodotNodeTree annotation was used isn't a valid Godot project directory")

class UnexpectedNodeFormat(input: String, element: String) : Exception("A node with unexpected $element format encountered: $input")

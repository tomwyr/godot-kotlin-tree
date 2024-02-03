package com.example.game

import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction

@RegisterClass
class Simple : Node() {
    private val startButton by GDTree.HUD.StartButton

    @RegisterFunction
    override fun _ready() {
        super._ready()
        startButton.text = "Tree Button"
    }
}

package com.example.game

import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.api.Node2D
import godot.common.util.PI
import godot.core.Color
import kotlin.math.sin

@RegisterClass
class ColorAnimator : Node2D() {
	val colorRect by GDTree.Main.ColorRect

	val colorFrom = Color.red
	val colorTo = Color.blue

	var colorPeriod = 0.0

	@RegisterFunction
	override fun _process(delta: Double) {
		colorPeriod = (colorPeriod + delta * 2) % (2 * PI)
		val progress = sin(colorPeriod) / 2 + 0.5
		colorRect.color = colorFrom.lerp(colorTo, progress)
	}
}

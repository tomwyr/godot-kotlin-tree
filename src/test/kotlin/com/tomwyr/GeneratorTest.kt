package com.tomwyr

import com.tomwyr.command.GenerateTreeCommand
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneratorTest {
    @Test
    fun `single scene with nested nodes`() {
        test("physics-test", "com.physics.test")
    }

    @Test
    fun `spaces in node names`() {
        test("simple", "com.simple.game")
    }

    @Test
    fun `multiple independent scenes`() {
        test("scene-changer", "com.scenes.changer")
    }

    @Test
    fun `scene with nested scenes`() {
        test("waypoints", "com.waypoints.test")
    }

    @Test
    fun `multiple scenes`() {
        test("dodge-the-creeps", "com.example.game")
    }
}

fun test(testCase: String, packageName: String) {
    try {
        val command = setUpTestCommand(testCase, packageName)
        command.run()
        assertOutputsEqual(testCase)
    } finally {
        cleanUpGeneratedOutput(testCase)
    }
}

const val basePath = "src/test/resources/"

fun setUpTestCommand(testCase: String, packageName: String): GenerateTreeCommand {
    return GenerateTreeCommand(
        libPath = "libGodotNodeTreeCore.dylib",
        projectPath = "$basePath/$testCase/scenes",
        outputPath = "$basePath/$testCase/Actual",
        packageName = packageName,
    )
}

fun assertOutputsEqual(testCase: String) {
    val expected = File("$basePath/$testCase/Expected").readText(Charsets.UTF_8)
    val actual = File("$basePath/$testCase/Actual").readText(Charsets.UTF_8)
    assertEquals(expected, actual)
}

fun cleanUpGeneratedOutput(testCase: String) {
    File("$basePath/$testCase/Actual").run {
        if (exists()) delete()
    }
}

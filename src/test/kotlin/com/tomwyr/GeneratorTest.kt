package com.tomwyr

import com.tomwyr.core.GodotKotlinProject
import com.tomwyr.core.Log
import com.tomwyr.core.Logger
import com.tomwyr.core.NodeTreeGenerator
import java.io.File
import kotlin.test.BeforeTest
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
    fun `multiple scenes`() {
        test("dodge-the-creeps", "com.example.game")
    }

    @BeforeTest
    fun setUpTestLogger() {
        Log.logger = object : Logger {
            override fun debug(message: String) {}
            override fun info(message: String) {}
            override fun warn(message: String) {}
        }
    }
}

fun test(testCase: String, targetPackage: String) {
    try {
        val project = setUpTestProject(testCase, targetPackage)
        NodeTreeGenerator().generate(project)
        assertOutputsEqual(testCase)
    } finally {
        cleanUpGeneratedOutput(testCase)
    }
}

const val basePath = "src/test/resources/"

fun setUpTestProject(testCase: String, targetPackage: String): GodotKotlinProject {
    return GodotKotlinProject(
        "$basePath/$testCase/scenes",
        "$basePath/$testCase/Actual",
        targetPackage,
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

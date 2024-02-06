# Godot Kotlin Tree

Godot Kotlin Tree enhances development of Godot games using Kotlin bindings by generating a statically typed object mapping the Godot project nodes to Kotlin.

In short, instead of referencing node with a string path and casting:

```kotlin
getNode(NodePath("/root/Path/To/Some/Nested/Area/Node")) as Area2D
```

the same can be achieved using generated typed fields:

```kotlin
GDTree.Scene.Path.To.Some.Nested.Area.Node
```

The references are generated automatically based on the project and scenes declarations meaning that any modifications to the node tree structure can be easily tracked in Kotlin sources. Rebuilding Kotlin project after node paths change will result in compile-time errors that otherwise could become difficult to debug runtime errors.

For more information about developing Godot games using Kotlin, head to [Godot Kotlin JVM](https://godot-kotl.in/en/stable/) website.

## Setup

### Kotlin

Configure plugin in the `build.gradle.kts` file:

```kotlin
// Add plugin dependency
plugins {
  id("com.tomwyr.godot-node-tree") version "1.0.0"
}

// Include generated code in the project sources
sourceSets {
    main {
        kotlin.srcDir("build/generated/godotNodeTree/kotlin")
    }
}

// Optional configuration
godotNodeTree {
    // Package that the generated code should belong to
    packageName = "your.package.name"
    // Path to Godot project relative to Kotlin project root
    projectPath = "godot/project/path"
}
```

Add plugin repository declaration to the `settings.gradle.kts` file:
```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
```

### Godot

No additional setup of the Godot project is needed.

## Usage

Create a scene with nodes in Godot Editor and run `generateNodeTree` Gradle task:

The task will scan your Godot project files and generate node tree representing the scene:

```kotlin
// build/generated/godotNodeTree/kotlin/com/example/game/GodotNodeTree.kt
object GDTree {
    object Main : NodeRef<Node>("/root/Main", "Node") {
        val ColorRect = NodeRef<ColorRect>("/root/Main/ColorRect", "ColorRect")
        val ColorAnimator = NodeRef<Node2D>("/root/Main/ColorAnimator", "Node2D")
        //...
    }
}
```

Reference the generated tree from within `Node` classes:

```kotlin
@RegisterClass
class ColorAnimator : Node2D() {
    val colorRect by GDTree.Main.ColorRect

    @RegisterFunction
    override fun _process(delta: Double) {
        super._process(delta)
        colorRect.color = calcColorForDelta(delta)
    }

    //...
}
```

Run project:

## Compatibility

The plugin works with the following Kotlin/JVM bindigns and Godot engine versions:

_Note: the end part of bindings version is also the compatible engine version._

| godot-kotlin-tree | godot-kotlin-jvm |
| ----------------- | ---------------- |
| 1.0.0             | 0.8.1-4.2.0      |

Other pairs of versions may also work but their compatibility has never been tested and proper behavior of the plugin isn't guaranteed.

## Contributing

Every kind of help aiming to improve quality and add new functionalities is welcome. Feel free to:

- Open an issue to request new features, report bugs, ask for help.
- Open a pull request to propose changes, fix bugs, improve documentation.
- Tell others about this project.

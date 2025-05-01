package com.tomwyr.ffi

import com.sun.jna.Library
import com.sun.jna.Pointer
import com.sun.jna.Native
import com.tomwyr.common.GeneratorError
import com.tomwyr.common.GodotNodeTreeError
import com.tomwyr.common.NodeTree

fun generateNodeTree(libPath: String, projectPath: String): NodeTree {
  val lib = Native.load(libPath, GodotNodeTreeLib::class.java)
  val resultPtr = lib.generateNodeTree(projectPath)
  val result = Result.fromJson<NodeTree, GodotNodeTreeError>(resultPtr.getString(0))
  return when (result) {
    is Ok -> result.value
    is Err -> throw GeneratorError(result.value)
  }
}

interface GodotNodeTreeLib : Library {
  fun generateNodeTree(projectPath: String): Pointer
}

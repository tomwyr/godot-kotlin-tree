package com.tomwyr.command

import java.io.File

class NodeTreeWriter {
    fun write(content: String, path: String) {
        val file = File(path).apply {
            parentFile.mkdirs()
            createNewFile()
        }
        file.outputStream().apply {
            write(content.toByteArray())
            close()
        }
    }
}

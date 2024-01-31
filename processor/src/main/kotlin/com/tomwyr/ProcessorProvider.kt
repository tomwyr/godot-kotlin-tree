package com.tomwyr

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tomwyr.core.Log
import com.tomwyr.core.Logger

class GodotNodeTreeProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        with(environment) {
            initLog(logger)
            return GodotNodeTreeProcessor(codeGenerator, options)
        }
    }
}

fun initLog(logger: KSPLogger) {
    Log.logger = object : Logger {
        override fun debug(message: String) = logger.logging(message)
        override fun info(message: String) = logger.info(message)
        override fun warn(message: String) = logger.warn(message)
    }
}

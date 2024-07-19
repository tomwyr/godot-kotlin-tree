package com.tomwyr

import com.tomwyr.common.Log
import com.tomwyr.generator.NodeTreeGenerator
import com.tomwyr.utils.GodotKotlinProject
import org.gradle.api.Project

class GenerateTreeCommand {
    fun run(project: Project, config: GodotNodeTreeConfig) {
        val projectPath = project.projectDir.absolutePath
        val godotProject = GodotKotlinProject.create(projectPath, config)
        val treeInfo = NodeTreeGenerator().generate(godotProject)
        Log.nodeTreeGenerated(treeInfo)
    }
}

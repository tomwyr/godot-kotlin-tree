import java.io.FileInputStream
import java.util.*

version = "1.0.0"
group = "io.github.tomwyr"
description = "A type-safe Godot node tree representation in Kotlin"

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.gradle.plugin-publish") version "1.2.1"
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website = "https://github.com/tomwyr/godot-kotlin-tree"
    vcsUrl = "https://github.com/tomwyr/godot-kotlin-tree.git"

    setGradlePublishProperties()

    plugins {
        create("godot-kotlin-tree") {
            id = "io.github.tomwyr.godot-kotlin-tree"
            displayName = "Godot Kotlin Tree"
            description = "A type-safe Godot node tree representation in Kotlin"
            tags = listOf("godot", "kotlin", "node", "tree")
            implementationClass = "com.tomwyr.GodotNodeTree"
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            artifactId = "godot-kotlin-tree"
            from(components["java"])

            pom {
                name = "Godot Kotlin Tree"
                description = "A type-safe Godot node tree representation in Kotlin"
                url = "https://github.com/tomwyr/godot-kotlin-tree"

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://gist.github.com/tomwyr/2edea68a06b67d69b2a80964a3ff6f34"
                    }
                }

                developers {
                    developer {
                        id = "tomwyr"
                        name = "Tomasz Wyrowiński"
                        url = "https://github.com/tomwyr"
                    }
                }

                scm {
                    url = "https://github.com/tomwyr/godot-kotlin-tree"
                    connection = "scm:git:git://github.com/tomwyr/godot-kotlin-tree.git"
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/content/repositories/releases/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (isSnapshot) snapshotsRepoUrl else releasesRepoUrl)

            credentials {
                username = localProperties["maven.repo.username"] as String
                password = localProperties["maven.repo.password"] as String
            }
        }
    }
}

signing {
    val signingKey = localProperties["maven.signing.key"] as String
    val signingPassword = localProperties["maven.signing.password"] as String
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["plugin"])
}

val isSnapshot: Boolean
    get() = version.toString().endsWith("SNAPSHOT")

fun setGradlePublishProperties() {
    val properties = Properties().apply {
        load(project.rootProject.file("local.properties").reader())
    }
    val bindProperty = { key: String ->
        System.setProperty(key, properties.getProperty(key))
    }

    bindProperty("gradle.publish.key")
    bindProperty("gradle.publish.secret")
}

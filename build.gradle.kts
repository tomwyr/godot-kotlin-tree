import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import java.io.FileInputStream
import java.util.*

val localProperties = loadLocalProps()

version = "1.0.0"
group = "io.github.tomwyr"
description = "A type-safe Godot node tree representation in Kotlin"

plugins {
    kotlin("jvm") version "1.9.20"
    id("com.gradle.plugin-publish") version "1.2.1"
    id("com.adarshr.test-logger") version "4.0.0"
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    testImplementation(kotlin("test"))
}

configure<TestLoggerExtension> {
    theme = ThemeType.MOCHA
    showCauses = true
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website = "https://github.com/tomwyr/godot-kotlin-tree"
    vcsUrl = "https://github.com/tomwyr/godot-kotlin-tree.git"

    fun bindProp(key: String) = System.setProperty(key, prop(key))
    bindProp("gradle.publish.key")
    bindProp("gradle.publish.secret")

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
                username = prop("maven.repo.username")
                password = prop("maven.repo.password")
            }
        }
    }
}

signing {
    isRequired = true
    val signingKey = prop("maven.signing.key").replace("\\n", "\n")
    val signingPassword = prop("maven.signing.password")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["plugin"])
}

val isSnapshot: Boolean
    get() = version.toString().endsWith("SNAPSHOT")

fun loadLocalProps() = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

fun prop(key: String): String {
    return localProperties[key] as? String ?: throw Exception("Expected local property $key not set")
}

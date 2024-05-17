import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType

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

    envToProp("GRADLE_PUBLISH_KEY", "gradle.publish.key")
    envToProp("GRADLE_PUBLISH_SECRET", "gradle.publish.secret")

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
                username = env("MAVEN_REPO_USERNAME")
                password = env("MAVEN_REPO_PASSWORD")
            }
        }
    }
}

signing {
    val signingKey = env("MAVEN_SIGNING_KEY")
    val signingPassword = env("MAVEN_SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["plugin"])
}

val isSnapshot: Boolean
    get() = version.toString().endsWith("SNAPSHOT")

fun env(key: String): String? = System.getenv(key)

fun envToProp(envKey: String, propKey: String, defaultValue: String = "") {
    System.setProperty(propKey, System.getenv(envKey) ?: defaultValue)
}

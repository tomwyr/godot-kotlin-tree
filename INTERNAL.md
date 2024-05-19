# Internal

The following documentation provides additional knowledge about the project that is helpful to understand and/or develop the plugin.

# Local properties

The following properties should be set locally in the `local.properties` file in order to support build and publishing tasks.

| Property               | Usage |
| ---------------------- | ----- |
| gradle.publish.key     | Gradle API key. Required when publishing to the Gradle Plugin Portal using the `plugin-publish` plugin. |
| gradle.publish.secret  | Gradle API secret. Required when publishing to the Gradle Plugin Portal using the `plugin-publish` plugin. |
| maven.repo.username    | Maven repository username. Required when publishing to Maven repository using the `maven-publish` plugin. |
| maven.repo.password    | Maven repository password. Required when publishing to Maven repository using the `maven-publish` plugin. |
| maven.signing.key      | ASCII armored secret key. Required to sign the artifact using the `maven-publish` plugin. |
| maven.signing.password | Passphrase of the secret key. Required to sign the artifact using the `maven-publish` plugin. |

# Publishing

To publish a new version of the plugin to the plugin repositories, bump the `version` property in `build.gradle.kts` and run:
- `publishPlugins` to upload the plugin to Gradle Plugin Repository,
- `publishPluginPublicationToMavenRepository` to upload the plugin to Maven Central Repository.

# Pre-release tests

Testing changes before publishing built artifacts to the repositories can be done by uploading a snapshot version of the plugin.
To do so, add `-SNAPSHOT` suffix to the next release version and upload it to Maven repository with the `publishPluginPublicationToMavenRepository` Gradle task.

In the test project, configure the plugin using the legacy `apply` method as shown below:
```
buildscript {
    repositories {
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }

    dependencies {
        classpath("io.github.tomwyr:godot-kotlin-tree:X.Y.Z-SNAPSHOT")
    }
}

apply<GodotNodeTree>()
```

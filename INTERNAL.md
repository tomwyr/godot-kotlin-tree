# Internal

The following documentation provides additional knowledge about the project that is helpful to understand and/or develop the plugin.

# Local properties

The following properties should be set locally in the `local.properties` file in order to support build and publishing tasks.

| Property               | Usage |
| ---------------------- | ----- |
| gradle.publish.key     | Gradle API key. Required when publishing to the Gradle Plugin Portal using the `plugin-publish` plugin. |
| gradle.publish.secret  | Gradle API secret. Required when publishing to the Gradle Plugin Portal using the `plugin-publish` plugin. |

# Publishing

To publish a new version of the plugin to the plugin repositories, bump the `version` property in `build.gradle.kts` and run:
- `publishPlugins` to upload the plugin to Gradle Plugin Repository,

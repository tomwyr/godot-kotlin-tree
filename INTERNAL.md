# Internal

The following documentation provides additional knowledge about the project that is helpful to understand and/or develop the plugin.

# Local properties

The following properties should be set locally in the `local.properties` file in order to support build and development capabilities.

| Property               | Usage |
| ---------------------- | ----- |
| gradle.publish.key     | Gradle API key. Required when publishing to the Gradle Plugin Portal using the `plugin-publish` plugin. |
| gradle.publish.secret  | Gradle API secret. Required when publishing to the Gradle Plugin Portal using the `plugin-publish` plugin. |
| maven.repo.username    | Maven repository username. Required when publishing to Maven repository using the `maven-publish` plugin. |
| maven.repo.password    | Maven repository password. Required when publishing to Maven repository using the `maven-publish` plugin. |
| maven.signing.key      | ASCII armored secret key. Required to sign the artifact using the `maven-publish` plugin. |
| maven.signing.password | Passphrase of the secret key. Required to sign the artifact using the `maven-publish` plugin. |

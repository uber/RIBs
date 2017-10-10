# Presidio IntelliJ Plugin

## Build Instructions

You can build/test/project on the intellij plugin with buck, but to actually deploy it and test a local version in the ide, you need to deploy via gradle

To run unit tests locally:
* Run `./buckw test //apps/presidio/tooling/presidio-intellij-plugin:test_main`

To test the plugin locally:
* Run `./gradlew :apps:presidio:tooling:presidio-intellij-plugin:runIde -Dintellij.gradle=true -Dokbuck.wrapper=true`

To install the plugin locally:
* Run `./gradlew :apps:presidio:tooling:presidio-intellij-plugin:buildPlugin -Dintellij.gradle=true -Dokbuck.wrapper=true`
* Install the jar file generated in `build/project-build/apps/presidio/tooling/presidio-intellij-plugin/build/libs`.
* Make sure you've installed the correct jar. If you install the wrong jar you will see runtime crashes.

To push an update to this plugin:
* Bump the version inside plugin.xml
* Create a tag for the new version. Ex, run `git tag "presidio-intellij-plugin-v1.4.0"`
* Push the commit
* Push the tags. Ex, run `git push origin presidio-intellij-plugin-v1.4.0`
* Open "release-production-android-presidio-intellij-plugin" in ci-mobile and build your tag. Ie, build with "v1.4.0".

To force engineers to bump the version of your plugin:
* Update .idea/.requiredPresidioPluginVersion

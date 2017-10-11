# RIB IntelliJ Plugin

## Installation Instructions

In Intellij, open Plugins > Install Plugins From Disk. Then install the [jar](https://github.com/uber/RIBs/raw/master/android/tooling/rib-intellij-plugin/deploy/rib-intellij-plugin.jar).

## Build Instructions

To install the plugin locally:
* Run `./gradlew :tooling:rib-intellij-plugin:buildPlugin -Dorg.gradle.configureondemand=true -Dbuild.intellijplugin=true`
* Install the jar file generated in under `build`
* Make sure you've installed the correct jar. If you install the wrong jar you will see runtime crashes.

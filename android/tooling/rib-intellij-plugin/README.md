# RIB IntelliJ Plugin

We have created an IntelliJ plugin to generate RIBs scaffolding, making RIB usage and adoption easier. The scaffolded classes have RIB structures in place. Test setup with mocks for the classes that should have business logic are also generated and ready to use.

After installing the plugin, RIBs can be added with the `New -> New RIB...` command. This generates:
- Scaffolding: [RIBName]Builder, [RIBName]Interactor, [RIBName]Router and [RIBName]View
- Test classes for unit testing: [RIBName]InteractorTest, [RIBName]RouterTest

RIBs can be generated with or without Presenter and View classes.

![The RIB IntelliJ Plugin](rib-tooling-1.png)



## Installation Instructions

In Intellij, open `IntelliJ IDEA > Preferences > Plugins` and select `Install Plugins From Disk`. Then install the [RIBs plugin jar](https://raw.githubusercontent.com/uber/RIBs/android-tooling-tutorial/android/tooling/rib-intellij-plugin/rib-tooling-2.png). After this, the plugin will appear in the `New` menu.

![Adding a new RIB from IntelliJ, after having installed the plugin](rib-tooling-2.png)

## Build Instructions

To install the plugin locally:
* Run `./gradlew :tooling:rib-intellij-plugin:buildPlugin -Dorg.gradle.configureondemand=true -Dbuild.intellijplugin=true`
* Install the jar file generated within `build`
* Make sure you've installed the correct jar. If you install the wrong jar, you could see runtime crashes.

# RIBs Xcode Templates

We have created Xcode templates to generate RIBs scaffolding and test scaffolding, making RIBs usage and adoption easier. The scaffolded classes have RIBs wired up, ready to add business logic to them.

### Generating RIB Classes

After installing templates, RIBs can be added with the `New File...` command in Xcode. This will generate:

- [RIBName]Builder
- [RIBName]Interactor
- [RIBName]Router
- [RIBName]ViewController class (optional)

RIBs can be generated with or without owning corresponding views.

![The RIBs Xcode Template](https://github.com/uber/ribs/blob/assets/ios/tooling/ios-rib-tooling-1.png)

- **RIBs with view** generation does not generate a separate presenter class, only a `Presentable` interface, which the ViewController implements. This is to keep things simple and practical. In the majority of our use cases we have not found the need to create a standalone Presenter class to communicate with the View.
- **Pure logic RIBs** often manipulate the view hierarchy in some way. In the router generated in these cases, a `cleanupViews` method is added where cleanup code should be implemented, once the RIB goes away. This method is not generated for RIBs with views, as in those cases, the parent will most likely detach the child and no manual cleanup would be needed.

### Generating RIB Test Classes

Choose `RIB Unit Tests` in your test target to generate the scaffolding for Router and Interactor unit tests. Mocks are  not generated with this template: you will have to create and set these up yourself for the unit tests.

## Installation Instructions

Run the `install-xcode.template.sh` shell script to copy the templates to the Xcode templates folder. Once you have successfully copied the templates, when adding a new file in Xcode, the RIBs group will show up.

![RIBs in Xcode](https://github.com/uber/ribs/blob/assets/ios/tooling/ios-rib-tooling-2.png).

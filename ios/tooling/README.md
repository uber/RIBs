# RIB Xcode Templates

We have created Xcode templates to generate RIBs scaffolding, making RIB usage and adoption easier. The scaffolded classes have RIBs wired up, ready to add business logic to them.

After installing templates, RIBs can be added with the `New File...` command in Xcode. This generates:
- [RIBName]Builder, [RIBName]Interactor, [RIBName]Router classes in all classes
-  and [RIBName]ViewController class

RIBs can be generated with or without owning corresponding views.

![The RIBs Xcode Template](ios-rib-tooling-1.png)

- **RIBs with view** generation does not generate a separate presenter class, only a `Presentable` interface, which the ViewController implements. This is to keep things simple and practical. In the majority of our use cases we have not found the need to create a standalone Presenter class to communicate with the View.
- **Pure logic RIBs** often manipulate the view hierarchy in some way. In the router generated in these cases, a `cleanupViews` method is added where cleanup code should be implemented, once the RIB goes away. This method is not generated for RIBs with views, as in those cases, the parent will most likely detach the child and no manual cleanup would be needed.


## Installation Instructions

Run the `install-xcode.template.sh` shell script to copy the templates to the Xcode templates folder. Once you have successfully copied the templates, when adding a new file in Xcode, the RIBs group will show up.

![RIBs in Xcode](ios-rib-tooling-2.png).

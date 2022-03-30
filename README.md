<p align="center">
<img src="https://github.com/uber/ribs/blob/assets/rib_horizontal_black.png" width="60%" height="60%" alt="RIBs"/>
</p>

[![Build Status](https://travis-ci.org/uber/RIBs.svg?branch=master&style=flat-square)](https://travis-ci.org/uber/RIBs)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Cocoapods Compatible](https://img.shields.io/cocoapods/v/RIBs.svg)](https://cocoapods.org/pods/ribs)
[![Carthage Compatible](https://img.shields.io/badge/Carthage-compatible-4BC51D.svg)](https://github.com/Carthage/Carthage)

RIBs is the cross-platform architecture framework behind many mobile apps at Uber. The name RIBs is short for Router, Interactor and Builder, which are core components of this architecture. This framework is designed for mobile apps with a large number of engineers and nested states.

The RIBs architecture provides:
* **Shared architecture across iOS and Android.** Build cross-platform apps that have similar architecture, enabling iOS and Android teams to cross-review business logic code.
* **Testability and Isolation.** Classes must be easy to unit test and reason about in isolation. Individual RIB classes have distinct responsibilities like: routing, business, view logic, creation. Plus, most RIB logic is decoupled from child RIB logic. This makes RIB classes easy to test and reason about independently.
* **Tooling for developer productivity.** RIBs come with IDE tooling around code generation, memory leak detection, static analysis and runtime integrations - all which improve developer productivity for large teams or small.
* **An architecture that scales.** This architecture has proven to scale to hundreds of engineers working on the same codebase and apps with hundreds of RIBs.


## Documentation
To get started with RIBs, please refer to the [RIBs documentation](https://github.com/uber/RIBs/wiki). This describes key concepts on RIBs, from what they are for, their structure and common use cases.

To get more hands on with RIBs, we have written a [series of tutorials](https://github.com/uber/RIBs/wiki) that run you through the main aspects of the architecture with hands-on examples.

To read about the backstory on why we created RIBs, see [this blog post](https://eng.uber.com/new-rider-app/) we wrote when releasing RIBs in production the first time and see [this short video](https://www.youtube.com/watch?v=Q5cTT0M0YXg) where we discussed how the RIBs architecture works.

#### What is the difference between RIBs and MV*/VIPER?

MVC, MVP, MVI, MVVM and VIPER are architecture patterns. RIBs is a framework. What differentiates RIBs from frameworks based on MV*/VIPER is:

- **Business logic drives the app, not the view tree**. Unlike with MV*/VIPER, a RIB does not have to have a view. This means that the app hierarchy is driven by the business logic, not the view tree.
- **Independent business logic and view trees**. RIBs decouple how the business logic scopes are structured from view hierarchies. This allows the application to have a deep business logic tree, isolating business logic nodes, while maintaining a shallow view hierarchy making layouts, animations and transitions easy.

There are some other novel things about RIBs. However, these could also be implemented with other MV*/VIPER frameworks. These are:
- **Cross-platform approach**, allowing iOS and Android architecture to stay in sync.
- **Tooling for easier adoption** on larger apps or teams. Tooling we are open sourcing includes IDE plugins for code generation and static code analysis.
- **Strong opinions about how state should be communicated**, using DI and Rx. Each RIB defines its dependencies and what dependencies it needs from its parent. Parent components that fulfill a child’s parent dependencies are provided to child Builders as a constructor dependency to allow for hierarchical DI scoping. This means that information is communicated via these dependencies up and down the tree.

## Usage

1. Clone this repository
2. Integrate using your preferred installation mechanism

For usage of the tooling built around RIBs, please see the [Tooling section](https://github.com/uber/RIBs/wiki#rib-tooling) in our documentation.

## Installation for Android

To integrate the recommended minimum setup for RIBs add the following to your `build.gradle`:

```gradle
dependencies {
  annotationProcessor 'com.uber.rib:rib-compiler-test:0.12.2'
  implementation 'com.uber.rib:rib-android:0.12.2'
  testImplementation 'com.uber.rib:rib-test:0.12.2'
}
```

## Installation for iOS
#### CocoaPods

To integrate RIBs into your project add the following to your `Podfile`:

```ruby
pod 'RIBs', '~> 0.9'
```

#### Carthage

To integrate RIBs into your project using Carthage add the following to your `Cartfile`:

```ruby
github "uber/RIBs" ~> 0.9
```

## Related projects

If you like RIBs, check out other related open source projects from our team:
- [Needle](https://github.com/uber/needle): a compile-time safe Swift dependency injection framework.
- [Motif](https://github.com/uber/motif): An abstract on top of Dagger offering simpler APIs for nested scopes.
- [Swift Concurrency](https://github.com/uber/swift-concurrency): a set of concurrency utility classes used by Uber, inspired by the equivalent [java.util.concurrent](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html) package classes.
- [Swift Abstract Class](https://github.com/uber/swift-abstract-class): a light-weight library along with an executable that enables compile-time safe abstract class development for Swift projects.
- [Swift Common](https://github.com/uber/swift-common): common libraries used by this set of Swift open source projects.

## License

    Copyright (C) 2017 Uber Technologies

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

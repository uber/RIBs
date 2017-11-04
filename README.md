<p align="center">
<img src="https://github.com/uber/ribs/blob/assets/rib_horizontal_black.png" width="450" height="196" alt="RIBs"/>
</p>

[![Build Status](https://travis-ci.org/uber/RIBs.svg?branch=fix_travis)](https://travis-ci.org/uber/RIBs)

RIBs is the cross-platform architecture behind many mobile apps at Uber. This architecture framework is designed for mobile apps with a large number of engineers and nested states.

The RIBs architecture provides:
* **Shared architecture across iOS and Android.** Build cross-platform apps that have similar architecture, enabling iOS and Android teams to cross-review business logic code.
* **Testability and Isolation.** Classes must be easy to unit test and reason about in isolation. Individual RIB classes have distinct responsibilities like: routing, business, view logic, creation. Plus, most RIB logic is decoupled from child RIB logic. This makes RIB classes easy to test and reason about independently.
* **Tooling for developer productivity.** RIBs come with IDE tooling around code generation, memory leak detection, static analysis and runtime integrations - all which improve developer productivity for large teams or small.
* **An architecture that scales.** This architecture has proven to scale to hundreds of engineers working on the same codebase and apps with hundreds of RIBs.


## Documentation
To get started with RIBs, please refer to the [RIBs documentation](https://github.com/uber/RIBs/wiki). This describes key concepts on RIBs, from what they are for, their structure and common use cases.

To get more hands on with RIBs, we have written a [series of tutorials](https://github.com/uber/RIBs/wiki) that run you through the main aspects of the architecture with hands-on examples.

## Usage

1. Clone this repository 
2. Integrate using your preferred installation mechanism

For usage of the tooling built around RIBs, please see the [Tooling section](https://github.com/uber/RIBs/wiki#rib-tooling) in our documentation.

## Installation for Android

The integrate the recommended minimum setup for RIBs add the following to your `build.gradle`:

```gradle
dependencies {
  annotationProcessor 'com.uber.rib:rib-compiler-test:0.9.0'
  compile 'com.uber.rib:rib-android:0.9.0'
  testCompile 'com.uber.rib:rib-test-utils:0.9.0'
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

## Contributions

We'd love for you to contribute to our open source projects. Before we can accept your contributions, we kindly ask you to sign our [Uber Contributor License Agreement](https://docs.google.com/a/uber.com/forms/d/1pAwS_-dA1KhPlfxzYLBqK6rsSWwRwH95OCCZrcsY5rk/viewform).

- If you **find a bug**, please open an issue or submit a fix via a pull request.
- If you **have a feature request**, please open an issue or submit an implementation via a pull request
- If you **want to contribute**, please submit a pull request.

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

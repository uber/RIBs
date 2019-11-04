// swift-tools-version:5.1
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "RIBs",
    platforms: [
        .iOS(.v8)
    ],
    products: [
        .library(name: "RIBs", targets: ["RIBs"]),
    ],
    dependencies: [
        .package(url: "https://github.com/ReactiveX/RxSwift", from: "5.0.0")
    ],
    targets: [
        .target(name: "RIBs", dependencies: ["RxSwift", "RxRelay"], path: "ios/RIBs")
    ]
)

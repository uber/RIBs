// swift-tools-version:5.1
import PackageDescription

let package = Package(
    name: "RIBs",
    platforms: [
        .iOS(.v9),
    ],
    products: [
        .library(name: "RIBs", targets: ["RIBs"]),
    ],
    dependencies: [
        .package(url: "https://github.com/ReactiveX/RxSwift", from: "6.5.0"),
    ],
    targets: [
        .target(
            name: "RIBs",
            dependencies: ["RxSwift", "RxRelay"],
            path: "ios/RIBs"
        ),
        .testTarget(
            name: "RIBsTests",
            dependencies: ["RIBs"],
            path: "ios/RIBsTests"
        ),
    ]
)

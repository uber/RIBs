// swift-tools-version:4.2
//
//  Package.swift
//  RIBs
//
//  Created by kor45cw on 02/10/2019.
//  Copyright © 2019 Uber Technologies. All rights reserved.
//

import PackageDescription

let package = Package(name: "RIBs",
                      platforms: [.iOS(.v10)],
                      products: [.library(name: "RIBs", targets: ["RIBs"])],
                      dependencies: [
                          .package(url: "https://github.com/ReactiveX/RxSwift.git", from: "4.0.0")
                      ],
                      targets: [.target(name: "RIBs", path: "RIBs"), dependencies: ["RxSwift", "RxCocoa"]],
                      swiftLanguageVersions: [.v4, .v5])

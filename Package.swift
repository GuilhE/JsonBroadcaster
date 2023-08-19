// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "broadcast-handler-ios",
    platforms: [.iOS(.v13), .macOS(.v10_15), .tvOS(.v13), .watchOS(.v6)],
    products: [
        .library(
            name: "broadcast-handler-ios",
            targets: ["broadcast-handler-ios"]
        ),
    ],
    targets: [
        .target(
            name: "broadcast-handler-ios",
            path: "broadcast-handler-ios"
        )
    ]
)
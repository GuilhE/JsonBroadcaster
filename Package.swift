// swift-tools-version:5.3
import PackageDescription

let package = Package(
    name: "JsonBroadcasterHandler",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "JsonBroadcasterHandler",
            targets: ["JsonBroadcasterHandler"]
        ),
    ],
    targets: [
        .target(
            name: "JsonBroadcasterHandler",
            path: "JsonBroadcasterHandler/"
        )
    ],
    swiftLanguageVersions: [.v5]
)

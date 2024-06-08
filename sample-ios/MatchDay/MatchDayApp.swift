import SwiftUI

@main
struct MatchDayApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    
    var body: some Scene {
        WindowGroup {
            MatchScreen()
        }
    }
}

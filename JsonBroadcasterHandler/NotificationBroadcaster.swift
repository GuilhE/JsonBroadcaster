import UserNotifications

extension Notification.Name {
    static let uiStateChangeBroadcast = Notification.Name("UIStateChangeBroadcast")
}

public struct NotificationBroadcaster {
    public static func broadcast(_ notification: UNNotification) -> Void {
        NotificationCenter.default.post(
            name: Notification.Name.uiStateChangeBroadcast,
            object: nil,
            userInfo: notification.request.content.userInfo)
    }
}

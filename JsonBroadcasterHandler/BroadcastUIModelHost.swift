import Foundation
import Combine

private let payloadKey = "payload"

public class BroadcastUIModelHost<T: Codable> {
    private var cancellables: Set<AnyCancellable> = []
    private var currentState: T
    private var stateChangeCallback: (T) -> Void
    
    public init(_ initialState: T, stateChangeCallback: @escaping (T) -> Void) {
        self.currentState = initialState
        self.stateChangeCallback = stateChangeCallback
        
        NotificationCenter.default
            .publisher(for: Notification.Name.uiStateChangeBroadcast, object: nil)
            .compactMap { notification in
                return (notification.userInfo?[payloadKey] as? String)?.data(using: .utf8)
            }
            .compactMap { jsonData in
                do {
                    return try JSONDecoder().decode(T.self, from: jsonData)
                } catch {
                    print("> BroadcastUiModelHost: Error decoding JSON: \(error)")
                    return nil
                }
            }
            .replaceError(with: currentState)
            .receive(on: DispatchQueue.main)
            .sink { [weak self] newState in
                self?.currentState = newState
                self?.stateChangeCallback(newState)
            }
            .store(in: &cancellables)
    }
    
    deinit {
        cancellables.forEach { c in c.cancel() }
    }
}

import SwiftUI
import JsonBroadcasterHandler

class MatchViewModel: ObservableObject {
    private var uiModelHost: BroadcastUIModelHost<MatchUiState>!
    @Published var state: MatchUiState = MatchUiState(home: Team(country:"PRT", flag:"🇵🇹"), away: Team(country:"BRA", flag:"🇧🇷"))
    
    init() {
        uiModelHost = BroadcastUIModelHost(state) { [weak self] newState in
            self?.state = newState
        }
    }
}

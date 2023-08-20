import Foundation

struct Team: Codable {
    let country: String
    let flag: String
}

struct MatchUiState: Codable {
    let home: Team
    let away: Team
    var homeGoals: Int = 0
    var awayGoals: Int = 0
    private var started: Bool = false
    private var running: Bool = false
    private var finished: Bool = false
    
    var ongoing: Bool { started && running && !finished }
    var paused: Bool { started && !running && !finished }
    var ended: Bool { started && finished }
    
    init(home: Team, away: Team) {
        self.home = home
        self.away = away
    }
    
    private mutating func updateGameState(started: Bool, running: Bool, finished: Bool) -> MatchUiState {
        var newState = self
        newState.started = started
        newState.running = running
        newState.finished = finished
        return newState
    }
    
    mutating func startGame() -> MatchUiState {
        return updateGameState(started: true, running: true, finished: false)
    }
    
    mutating func pausedGame() -> MatchUiState {
        return updateGameState(started: true, running: false, finished: false)
    }
    
    mutating func endGame() -> MatchUiState {
        return updateGameState(started: false, running: false, finished: true)
    }
    
    func lineup() -> String {
        return "\(home.country) \(home.flag) vs \(away.flag) \(away.country)"
    }
    
    func score() -> String {
        return "\(homeGoals) - \(awayGoals)"
    }
}

import SwiftUI

struct MatchScreen: View {
    @StateObject private var viewModel = MatchViewModel()
    @State private var alpha: CGFloat = 1.0
    @State private var gameLabel = ""
    @State private var gameStatus = ""
    @State private var ongoing = false
    
    var body: some View {
        ZStack {
            Color("Background")
            VStack(spacing: 5) {
                Image("Cup")
                    .resizable()
                    .frame(width: 180.0, height: 180.0)
                Spacer().frame(height: 20)
                Text(gameLabel)
                    .font(.title)
                Text(gameStatus)
                    .font(.caption)
                    .opacity(alpha)
                    .onAppear {
                        Timer.scheduledTimer(withTimeInterval: 0.5, repeats: true) { _ in
                            withAnimation {
                                if(ongoing) {
                                    alpha = alpha == 0.0 ? 1.0 : 0.0
                                } else {
                                    alpha = 1.0
                                }
                            }
                        }
                    }
            }
        }
        .onReceive(viewModel.$state) { new in
            gameLabel = getLabel(new)
            gameStatus = getStatus(new).uppercased()
            ongoing = new.ongoing
        }
        .ignoresSafeArea()
    }
    
    private func getLabel(_ uiState: MatchUiState) -> String {
        return (uiState.ongoing || uiState.paused || uiState.ended) ?
        "\(uiState.home.flag) \(uiState.score()) \(uiState.away.flag)" : uiState.lineup()
    }
    
    private func getStatus(_ uiState: MatchUiState) -> String {
        if uiState.paused {
            return "Paused"
        } else if uiState.ended {
            return "Ended"
        } else if uiState.ongoing {
            return "Running"
        } else {
            return "Pre match"
        }
    }
}

struct MatchView_Previews: PreviewProvider {
    static var previews: some View {
        MatchScreen()
    }
}

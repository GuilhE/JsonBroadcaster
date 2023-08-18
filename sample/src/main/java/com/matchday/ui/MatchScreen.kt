package com.matchday.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.matchday.R

@Composable
fun MatchScreen(viewModel: MatchViewModel = hiltViewModel()) {
    with(viewModel.uiState.collectAsStateWithLifecycle().value) {
        Match(this)
    }

//    var uiState: MatchUiState by remember { mutableStateOf(MatchUiState(home = Team("PRT", "🇵🇹"), away = Team("BRA", "🇧🇷"))) }
//    LaunchedEffect(Unit) {
//        val host = object : BroadcastUiModelHost<MatchUiState>(this, MatchUiState.serializer()) {
//            override fun updateState(new: MatchUiState) {
//                uiState = new
//            }
//        }
//    }
//    Match(uiState)
}

@Composable
private fun Match(uiState: MatchUiState) {
    val label: String = remember(uiState) {
        when {
            uiState.ongoing || uiState.paused || uiState.ended -> "${uiState.home.flag} ${uiState.score()} ${uiState.away.flag}"
            else -> uiState.lineup()
        }
    }
    val status: String = remember(uiState) {
        when {
            uiState.paused -> "Paused"
            uiState.ended -> "Ended"
            uiState.ongoing -> "Running"
            else -> "Pre match"
        }
    }
    val alpha by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(easing = LinearEasing, durationMillis = 500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(Modifier.background(Color(0xFFE2E1E1))) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_playstore),
                contentDescription = null
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                modifier = Modifier.alpha(if (uiState.ongoing) alpha else 1f),
                text = status.uppercase(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
private fun MatchPreview() {
    var uiState by remember {
        mutableStateOf(
            MatchUiState(
                home = Team("PRT", "🇵🇹"),
                away = Team("BRA", "🇧🇷")
            )
        )
    }

    Box {
        Match(uiState)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { uiState = uiState.startGame() }) {
                Text(text = "Start")
            }
            Button(onClick = { uiState = uiState.pausedGame() }) {
                Text(text = "Pause")
            }
            Button(onClick = { uiState = uiState.endGame() }) {
                Text(text = "End")
            }
        }
    }
}
package com.matchday.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.broadcast.handler.BroadcastUiModelHost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MatchViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MatchUiState(home = Team("PRT", "🇵🇹"), away = Team("BRA", "🇧🇷")))
    val uiState: StateFlow<MatchUiState> = _uiState

    private val host = object : BroadcastUiModelHost<MatchUiState>(viewModelScope, MatchUiState.serializer()) {
        override fun updateState(new: MatchUiState) {
            _uiState.update { new }
        }
    }
}
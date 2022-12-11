package com.broadcast.handler

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

internal interface BroadcastUiModelBehavior<T> {
    val uiState: StateFlow<T>
    fun updateUiState(state: T)
}

internal object BroadcastUiModelHolder : BroadcastUiModelBehavior<String> {
    private val mutableUiState = MutableStateFlow("")
    override val uiState: StateFlow<String> = mutableUiState

    override fun updateUiState(state: String) {
        mutableUiState.update { state }
    }
}

abstract class BroadcastUiModelHost<T>(scope: CoroutineScope, deserializer: DeserializationStrategy<T>) {
    private val json = Json { ignoreUnknownKeys = true }

    init {
        scope.launch {
            BroadcastUiModelHolder.uiState.collect { model ->
                if (model.isNotBlank()) {
                    try {
                        updateState(json.decodeFromString(deserializer, model))
                    } catch (e: Exception) {
                        println(e)
                    }
                }
            }
        }
    }

    abstract fun updateState(new: T)
}
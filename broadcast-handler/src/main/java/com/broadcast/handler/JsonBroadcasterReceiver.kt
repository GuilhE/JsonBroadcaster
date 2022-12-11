package com.broadcast.handler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

internal class JsonBroadcasterReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        intent.getStringExtra(PARAMETER)?.let { BroadcastUiModelHolder.updateUiState(it) }
    }

    companion object {
        private const val PARAMETER = "extra"
    }
}
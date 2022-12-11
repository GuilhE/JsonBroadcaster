package com.matchday.ui

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class MatchUiState(
    val home: Team,
    val away: Team,
    val homeGoals: Int = 0,
    val awayGoals: Int = 0,
    private val started: Boolean = false,
    private val running: Boolean = false,
    private val finished: Boolean = false
) : Parcelable {

    @IgnoredOnParcel
    val ongoing: Boolean = started && running && !finished
    @IgnoredOnParcel
    val paused: Boolean = started && !running && !finished
    @IgnoredOnParcel
    val ended: Boolean = started && finished

    fun startGame(): MatchUiState = copy(started = true, running = true, finished = false)
    fun pausedGame(): MatchUiState = copy(started = true, running = false, finished = false)
    fun endGame(): MatchUiState = copy(started = false, running = false, finished = true)

    fun lineup() = "${home.country} ${home.flag} vs ${away.flag} ${away.country}"
    fun score() = "$homeGoals - $awayGoals"
}

@Serializable
@Parcelize
data class Team(
    val country: String,
    val flag: String
) : Parcelable
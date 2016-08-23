package models

import org.joda.time.DateTime

/**
 * Created by william on 8/19/16.
 */
data class GameResult (
    val LeaguePlayerName1: String,
    val LeaguePlayerName2: String,
    val Result: Int
)
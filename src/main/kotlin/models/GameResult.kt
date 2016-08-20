package models

import org.joda.time.DateTime

/**
 * Created by william on 8/19/16.
 */
data class GameResult (
    val LeagueId: Int,
    val LeaguePlayer1Info: String,
    val LeaguePlayer2Info: String,
    val Result: Int,
    val GameDate: DateTime
)
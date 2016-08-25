package models.contracts

/**
 * Created by william on 8/19/16.
 */
data class GameResultCommand(
    val LeaguePlayerName1: String,
    val LeaguePlayerName2: String,
    val Result: Int
)
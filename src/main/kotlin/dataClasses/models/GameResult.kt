package dataClasses.models

import org.joda.time.DateTime

/**
 * Created by william on 8/24/16.
 */
data class GameResult(
    val LeagueId: Int,
    val FirstLeaguePlayerId: Int,
    val SecondLeaguePlayerId: Int,
    val Result: Result,
    val GameDate: DateTime
)
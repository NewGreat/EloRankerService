package dataClasses.daos

import org.joda.time.DateTime

/**
 * Created by william on 8/25/16.
 */
data class GameResultDao(
    val GameResultId: Int,
    val LeagueId: Int,
    val FirstLeaguePlayerId: Int,
    val SecondLeaguePlayerId: Int,
    val Result: Int,
    val GameDate: DateTime
)
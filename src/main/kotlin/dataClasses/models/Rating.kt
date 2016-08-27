package dataClasses.models

import org.joda.time.DateTime

/**
 * Created by william on 8/19/16.
 */
data class Rating (
    val LeaguePlayerId: Int,
    val GameDate: DateTime,
    val Rating: Int,
    val GamesPlayed: Int
)

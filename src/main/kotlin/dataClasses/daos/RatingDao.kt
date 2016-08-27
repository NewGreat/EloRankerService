package dataClasses.daos

import org.joda.time.DateTime

/**
 * Created by william on 8/25/16.
 */
data class RatingDao (
    val LeaguePlayerId: Int,
    val GameDate: DateTime,
    val Rating: Int,
    val GamesPlayed: Int
)

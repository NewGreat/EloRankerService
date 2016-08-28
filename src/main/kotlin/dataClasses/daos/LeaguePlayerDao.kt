package dataClasses.daos

import org.joda.time.DateTime

/**
 * Created by william on 8/27/16.
 */
data class LeaguePlayerDao (
    val LeaguePlayerId: Int,
    val LeagueId: Int,
    val UserId: Int?,
    val LeaguePlayerName: String,
    val RatingUpdated: DateTime
)
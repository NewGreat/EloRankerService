package dataClasses.models

import org.joda.time.DateTime

/**
 * Created by william on 8/17/16.
 */
data class LeaguePlayer (
    val LeaguePlayerId: Int,
    val LeagueId: Int,
    val UserId: Int?,
    val LeaguePlayerName: String,
    val RatingUpdated: DateTime
)
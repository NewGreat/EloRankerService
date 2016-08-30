package dataClasses.models

import org.joda.time.DateTime

/**
 * Created by william on 8/30/16.
 */
data class Tournament (
    val TournamentId: Int,
    val LeagueId: Int,
    val Abbreviation: String,
    val Name: String,
    val Description: String,
    val StartDate: DateTime,
    val EndDate: DateTime,
    val WinPoints: Double,
    val DrawPoints: Double,
    val LosePoints: Double
)
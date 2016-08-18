package models

/**
 * Created by william on 8/18/16.
 */
data class League (
    // Basic fields
    val LeagueId: Int,
    val Name: String,
    val Description: String,
    // League metadata
    val InitialRating: Int,
    val KFactor: Int,
    val ProvisionalKFactor: Int,
    val NumProvisionalGames: Int
)
package managers

import contracts.AddPlayerCommand
import dataClasses.models.LeaguePlayer
import helpers.ParseDateTime
import org.joda.time.DateTime
import repositories.GetLeague
import repositories.*

/**
 * Created by william on 8/18/16.
 */

fun ConvertToLeaguePlayers(leagueId: Int, addPlayerCommands: List<AddPlayerCommand>) : List<LeaguePlayer> {
    return addPlayerCommands.map {
        LeaguePlayer(
            LeagueId = leagueId,
            LeaguePlayerName = it.LeaguePlayerName,
            UserId = it.UserId,
            RatingUpdated = ParseDateTime(it.JoinDate),
            LeaguePlayerId = 0
        )
    }
}

fun AddLeaguePlayer(leagueId: Int, leaguePlayerName: String, userId: Int?, joinDate: DateTime): Unit {
    val league = GetLeague(leagueId)
    val leaguePlayer = LeaguePlayer(
        LeagueId = leagueId,
        LeaguePlayerName = leaguePlayerName,
        UserId = userId,
        RatingUpdated = joinDate,
        LeaguePlayerId = 0
    )
    InsertLeaguePlayer(leaguePlayer, league.InitialRating)
}

fun AddLeaguePlayers(leagueId: Int, leaguePlayers: List<LeaguePlayer>) : Pair<List<String>,List<String>> {
    var addedLeaguePlayerNames = mutableListOf<String>()
    var failedLeaguePlayerNames = mutableListOf<String>()
    val league = GetLeague(leagueId)
    for (leaguePlayer in leaguePlayers) {
        try {
            val leaguePlayer = LeaguePlayer(
                LeagueId = leagueId,
                LeaguePlayerName = leaguePlayer.LeaguePlayerName,
                UserId = leaguePlayer.UserId,
                RatingUpdated = leaguePlayer.RatingUpdated,
                LeaguePlayerId = 0
            )
            InsertLeaguePlayer(leaguePlayer, league.InitialRating)
            addedLeaguePlayerNames.add(leaguePlayer.LeaguePlayerName)
        } catch (e: Exception) {
            failedLeaguePlayerNames.add(leaguePlayer.LeaguePlayerName)
        }
    }
    return Pair(addedLeaguePlayerNames, failedLeaguePlayerNames)
}
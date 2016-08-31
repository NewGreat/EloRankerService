package managers

import dataClasses.models.*
import repositories.GetGameResultsForTournament
import repositories.GetTournamentInfo
import repositories.InsertTournament

/**
 * Created by william on 8/30/16.
 */

fun CreateTournament(tournament: Tournament) {
    InsertTournament(tournament)
}

fun GetTournamentResults(leagueId: Int, tournamentAbbreviation: String) : List<TournamentPerformance>{
    val tournament = GetTournamentInfo(leagueId, tournamentAbbreviation)
    val gameResults = GetGameResultsForTournament(tournament.TournamentId)

    var leaguePlayerIds = hashSetOf<Int>()
    for(gameResult in gameResults) {
        leaguePlayerIds.add(gameResult.FirstLeaguePlayerId)
        leaguePlayerIds.add(gameResult.SecondLeaguePlayerId)
    }

    val tournamentResults = hashMapOf<Int, TournamentPerformance>()

    for(gameResult in gameResults) {
        val p1Result = tournamentResults.getOrElse(gameResult.FirstLeaguePlayerId) {
            TournamentPerformance(
                tournamentId = tournament.TournamentId,
                leaguePlayerId = gameResult.FirstLeaguePlayerId
            )
        }
        val p2Result = tournamentResults.getOrElse(gameResult.SecondLeaguePlayerId) {
            TournamentPerformance(
                tournamentId = tournament.TournamentId,
                leaguePlayerId = gameResult.SecondLeaguePlayerId
            )
        }
        p1Result.GamesPlayed ++
        p2Result.GamesPlayed++

        when (gameResult.Result) {
            Result.P1_WIN -> {
                p1Result.GamesWon++
                p2Result.GamesLost++
            }
            Result.DRAW -> {
                p1Result.GamesDrawn++
                p2Result.GamesDrawn++
            }
            Result.P1_LOSS -> {
                p1Result.GamesLost++
                p2Result.GamesWon++
            }
        }
        tournamentResults[gameResult.FirstLeaguePlayerId] = p1Result
        tournamentResults[gameResult.SecondLeaguePlayerId] = p2Result
    }

    for((leaguePlayerId, tr) in tournamentResults) {
        tr.TotalPoints = tournament.WinPoints * tr.GamesWon
            + tournament.DrawPoints * tr.GamesDrawn
            + tournament.LosePoints * tr.GamesLost
    }

    val tournamentPerformances = tournamentResults.values
    return tournamentPerformances.sorted()
}
package managers

import contracts.GameResultCommand
import dataClasses.models.*
import helpers.IsPositiveInteger
import org.funktionale.memoization.memoize
import org.joda.time.DateTime
import repositories.*

/**
 * Created by william on 8/18/16.
 */

fun GetLeaguePlayerFromInfo(leagueId: Int, leaguePlayerInfo: String) : LeaguePlayer? {
    return if (IsPositiveInteger(leaguePlayerInfo))
        GetLeaguePlayer(leagueId, leaguePlayerInfo.toInt())
        else GetLeaguePlayer(leagueId, leaguePlayerInfo)
}

fun ConvertToGameResults(leagueId: Int, gameDate: DateTime, gameResultCommands: List<GameResultCommand>) : List<GameResult> {
    var leaguePlayerNames = mutableSetOf<String>()
    val tournamentAbbreviations = mutableSetOf<String>()
    gameResultCommands.forEach { gameResultCommand ->
        if (!leaguePlayerNames.add(gameResultCommand.LeaguePlayerName1))
            throw Exception("Player ${gameResultCommand.LeaguePlayerName1} cannot play two games at the same time")
        if (!leaguePlayerNames.add(gameResultCommand.LeaguePlayerName2))
            throw Exception("Player ${gameResultCommand.LeaguePlayerName2} cannot play two games at the same time")
        tournamentAbbreviations.addAll(gameResultCommand.TournamentAbbreviations)
    }

    val leaguePlayers = GetLeaguePlayers(leagueId, leaguePlayerNames.toList())
    val tournaments = GetTournaments(leagueId, tournamentAbbreviations.toList())
    if (tournaments.size != tournamentAbbreviations.size)
        throw Exception("Not all tournaments could be found")

    val tournamentDict : Map<String, Tournament> = tournaments.associateBy({it.Abbreviation}, {it})

    var leaguePlayerDict = hashMapOf<String, LeaguePlayer>()
    for (leaguePlayer in leaguePlayers) {
        // Throw an exception if a player cannot be found in the league
        if (leaguePlayer == null)
            throw Exception("Not all players could be found in league $leagueId")
        if (leaguePlayer.RatingUpdated >= gameDate)
            throw Exception("Player ${leaguePlayer.LeaguePlayerName} has already played a game on or before $gameDate")
        leaguePlayerDict[leaguePlayer.LeaguePlayerName] = leaguePlayer
    }
    return gameResultCommands.map {
        GameResult(
            GameResultId = 0,
            LeagueId = leagueId,
            FirstLeaguePlayerId = leaguePlayerDict[it.LeaguePlayerName1]!!.LeaguePlayerId,
            SecondLeaguePlayerId = leaguePlayerDict[it.LeaguePlayerName2]!!.LeaguePlayerId,
            Result = Result.FromInt(it.Result),
            GameDate = gameDate,
            Tournaments = it.TournamentAbbreviations.map{
                tournamentDict[it]!!
            }
        )
    }
}

fun RecordLeagueGameResults(gameResults: List<GameResult>): Unit {
    val getLeague = { leagueId: Int -> GetLeague(leagueId)}.memoize()
    gameResults.forEach { RecordGameResult(it, getLeague(it.LeagueId)) }
}

fun RecordGameResult(gameResult: GameResult, league: League): Unit {
    InsertGameResult(gameResult)
    val player1Rating = GetLeaguePlayerRating(gameResult.FirstLeaguePlayerId)
    val player2Rating = GetLeaguePlayerRating(gameResult.SecondLeaguePlayerId)
    val newPlayer1Rating = Rating (
        Rating = CalculateNewRating(player1Rating.Rating, player2Rating.Rating,
            if (player1Rating.GamesPlayed > league.NumProvisionalGames) league.KFactor
            else league.ProvisionalKFactor, GetActualScore(gameResult.Result, true)),
        GamesPlayed = player1Rating.GamesPlayed + 1,
        GameDate = gameResult.GameDate,
        LeaguePlayerId = gameResult.FirstLeaguePlayerId
    )
    val newPlayer2Rating = Rating (
        Rating = CalculateNewRating(player2Rating.Rating, player1Rating.Rating,
            if (player2Rating.GamesPlayed > league.NumProvisionalGames) league.KFactor
            else league.ProvisionalKFactor, GetActualScore(gameResult.Result, false)),
        GamesPlayed = player2Rating.GamesPlayed + 1,
        GameDate = gameResult.GameDate,
        LeaguePlayerId = gameResult.SecondLeaguePlayerId
    )
    UpdateLeaguePlayerRating(newPlayer1Rating)
    UpdateLeaguePlayerRating(newPlayer2Rating)
}

private fun CalculateNewRating(rating: Int, oppRating: Int, kFactor: Int, actualScore: Double) : Int {
    val transformedRating = Math.pow(10.0, rating.toDouble() / 400.0)
    val oppTransformedRating = Math.pow(10.0, oppRating.toDouble() / 400.0)
    val expectedScore = transformedRating / (transformedRating + oppTransformedRating)

    return rating + (kFactor.toDouble() * (actualScore - expectedScore)).toInt()
}

private fun GetActualScore(result: Result, firstPlayer: Boolean) : Double {
    if (firstPlayer) {
        when (result) {
            Result.P1_WIN -> return 1.0
            Result.DRAW -> return 0.5
            Result.P1_LOSS -> return 0.0
            else -> throw Exception("Illegal result")
        }
    } else {
        when (result) {
            Result.P1_WIN -> return 0.0
            Result.DRAW -> return 0.5
            Result.P1_LOSS -> return 1.0
            else -> throw Exception("Illegal result")
        }
    }
}

fun RecalculateRatings(leagueId: Int, gameDate: DateTime) {
    val gameResults = GetGameResultsOnOrAfterDate(leagueId, gameDate)
    DeleteRatingsAfterDate(leagueId, gameDate)
    RecordLeagueGameResults(gameResults)
}
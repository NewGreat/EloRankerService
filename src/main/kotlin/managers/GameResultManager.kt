package managers

import helpers.IsPositiveInteger
import models.*
import models.contracts.GameResultCommand
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
    var leaguePlayerNames = mutableListOf<String>()
    gameResultCommands.forEach { gameResultCommand ->
        leaguePlayerNames.add(gameResultCommand.LeaguePlayerName1)
        leaguePlayerNames.add(gameResultCommand.LeaguePlayerName2)
    }

    val leaguePlayers = GetLeaguePlayers(leagueId, leaguePlayerNames)
    if (leaguePlayers.size != leaguePlayerNames.size)
        throw Exception("Not all players could be found in league $leagueId")

    var leaguePlayerDict = hashMapOf<String, LeaguePlayer>()
    for (leaguePlayer in leaguePlayers) {
        if (leaguePlayer == null)
            throw Exception("Not all players could be found in league $leagueId")
        if (leaguePlayer.RatingUpdated >= gameDate)
            throw Exception("Player ${leaguePlayer.LeaguePlayerName} has already played a game on or before $gameDate")
        if (leaguePlayerDict.containsKey(leaguePlayer.LeaguePlayerName))
            throw Exception("Player ${leaguePlayer.LeaguePlayerName} cannot play two games at the same time")
        leaguePlayerDict[leaguePlayer.LeaguePlayerName] = leaguePlayer
    }
    return gameResultCommands.map {
        GameResult(
            LeagueId = leagueId,
            FirstLeaguePlayerId = leaguePlayerDict[it.LeaguePlayerName1]!!.LeaguePlayerId,
            SecondLeaguePlayerId = leaguePlayerDict[it.LeaguePlayerName2]!!.LeaguePlayerId,
            Result = Result.FromInt(it.Result),
            GameDate = gameDate
        )
    }
}

fun RecordLeagueGameResults(gameResults: List<GameResult>): Unit {
    for(gameResult in gameResults) {
        RecordGameResult(
            GetFullLeagueData(gameResult.LeagueId),
            gameResult.FirstLeaguePlayerId,
            gameResult.SecondLeaguePlayerId,
            gameResult.Result,
            gameResult.GameDate
        )
    }
}

fun RecordGameResult(league: League, leaguePlayerId1: Int, leaguePlayerId2: Int, result: Result, gameDate: DateTime): Unit {
    InsertGameResult(league.LeagueId, leaguePlayerId1, leaguePlayerId2, result, gameDate)
    val player1Rating = GetLeaguePlayerRating(leaguePlayerId1)
    val player2Rating = GetLeaguePlayerRating(leaguePlayerId2)
    val newPlayer1Rating = Rating (
        Rating = CalculateNewRating(player1Rating.Rating, player2Rating.Rating,
            if (player1Rating.GamesPlayed > league.NumProvisionalGames) league.KFactor
            else league.ProvisionalKFactor, GetActualScore(result, true)),
        GamesPlayed = player1Rating.GamesPlayed + 1,
        GameDate = gameDate,
        LeaguePlayerId = leaguePlayerId1
    )
    val newPlayer2Rating = Rating (
        Rating = CalculateNewRating(player2Rating.Rating, player1Rating.Rating,
            if (player2Rating.GamesPlayed > league.NumProvisionalGames) league.KFactor
            else league.ProvisionalKFactor, GetActualScore(result, false)),
        GamesPlayed = player2Rating.GamesPlayed + 1,
        GameDate = gameDate,
        LeaguePlayerId = leaguePlayerId2
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
package managers

import helpers.IsPositiveInteger
import models.*
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

fun RecordLeagueGameResults(leagueId: Int, gameDate: DateTime, gameResults: List<GameResult>): Unit {
    var leaguePlayerNames = mutableListOf<String>()
    gameResults.forEach { gameResult ->
        leaguePlayerNames.add(gameResult.LeaguePlayerName1)
        leaguePlayerNames.add(gameResult.LeaguePlayerName2)
    }

    val leaguePlayers = GetLeaguePlayers(leagueId, leaguePlayerNames)

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

    for(gameResult in gameResults) {
        RecordGameResult(
            GetFullLeagueData(leagueId),
            leaguePlayerDict[gameResult.LeaguePlayerName1]!!.LeaguePlayerId,
            leaguePlayerDict[gameResult.LeaguePlayerName2]!!.LeaguePlayerId,
            Result.FromInt(gameResult.Result),
            gameDate
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
        GamesPlayed = player1Rating.GamesPlayed + 1
    )
    val newPlayer2Rating = Rating (
        Rating = CalculateNewRating(player2Rating.Rating, player1Rating.Rating,
            if (player2Rating.GamesPlayed > league.NumProvisionalGames) league.KFactor
            else league.ProvisionalKFactor, GetActualScore(result, false)),
        GamesPlayed = player2Rating.GamesPlayed + 1
    )
    UpdateLeaguePlayerRating(leaguePlayerId1, gameDate, newPlayer1Rating)
    UpdateLeaguePlayerRating(leaguePlayerId2, gameDate, newPlayer2Rating)
}

fun CalculateNewRating(rating: Int, oppRating: Int, kFactor: Int, actualScore: Double) : Int {
    val transformedRating = Math.pow(10.0, rating.toDouble() / 400.0)
    val oppTransformedRating = Math.pow(10.0, oppRating.toDouble() / 400.0)
    val expectedScore = transformedRating / (transformedRating + oppTransformedRating)

    return rating + (kFactor.toDouble() * (actualScore - expectedScore)).toInt()
}

fun GetActualScore(result: Result, firstPlayer: Boolean) : Double {
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
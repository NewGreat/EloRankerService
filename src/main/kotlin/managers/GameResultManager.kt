package managers

import models.GameResult
import models.Rating
import org.joda.time.DateTime
import repositories.*

/**
 * Created by william on 8/18/16.
 */
fun ValidateGameResults(gameResults: List<GameResult>, gameDate: DateTime) {

}

fun RecordGameResult(leagueId: Int, leaguePlayer1Info: String, leaguePlayer2Info: String, result: Int, gameDate: DateTime): Unit {
    val leaguePlayer1 = GetLeaguePlayer(leagueId, leaguePlayer1Info.toInt())
        ?: GetLeaguePlayer(leagueId, leaguePlayer1Info) ?: throw Exception()
    val leaguePlayer2 = GetLeaguePlayer(leagueId, leaguePlayer2Info.toInt())
        ?: GetLeaguePlayer(leagueId, leaguePlayer2Info) ?: throw Exception()

    return RecordGameResult(leagueId, leaguePlayer1.LeaguePlayerId, leaguePlayer2.LeaguePlayerId, result, gameDate)
}

fun RecordGameResult(leagueId: Int, leaguePlayerId1: Int, leaguePlayerId2: Int, result: Int, gameDate: DateTime): Unit {
    InsertGameResult(leagueId, leaguePlayerId1, leaguePlayerId2, result, gameDate)
    val fullLeagueData = GetFullLeagueData(leagueId)
    val player1Rating = GetLeaguePlayerRating(leaguePlayerId1)
    val player2Rating = GetLeaguePlayerRating(leaguePlayerId2)
    val newPlayer1Rating = Rating (
        Rating = CalculateNewRating(player1Rating.Rating, player2Rating.Rating, if (player1Rating.GamesPlayed > fullLeagueData.NumProvisionalGames) fullLeagueData.KFactor else fullLeagueData.ProvisionalKFactor, GetActualScore(result, true)),
        GamesPlayed = player1Rating.GamesPlayed + 1
    )
    val newPlayer2Rating = Rating (
        Rating = CalculateNewRating(player2Rating.Rating, player1Rating.Rating, if (player2Rating.GamesPlayed > fullLeagueData.NumProvisionalGames) fullLeagueData.KFactor else fullLeagueData.ProvisionalKFactor, GetActualScore(result, false)),
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

fun GetActualScore(result: Int, firstPlayer: Boolean) : Double {
    if (firstPlayer) {
        when (result) {
            1 -> return 1.0
            0 -> return 0.5
            -1 -> return 0.0
            else -> throw Exception("Illegal result")
        }
    } else {
        when (result) {
            1 -> return 0.0
            0 -> return 0.5
            -1 -> return 1.0
            else -> throw Exception("Illegal result")
        }
    }
}
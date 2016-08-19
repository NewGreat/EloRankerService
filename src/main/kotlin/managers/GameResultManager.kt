package managers

import models.Rating
import org.joda.time.DateTime
import repositories.MySqlDataRepository

/**
 * Created by william on 8/18/16.
 */
class GameResultManager {
    private val _dataRepository: MySqlDataRepository = MySqlDataRepository()

    fun RecordGameResult(leagueId: Int, leaguePlayer1Info: String, leaguePlayer2Info: String, result: Int, gameDate: DateTime): Unit {
        val leaguePlayer1 = _dataRepository.GetLeaguePlayer(leagueId, leaguePlayer1Info.toInt())
            ?: _dataRepository.GetLeaguePlayer(leagueId, leaguePlayer1Info) ?: throw Exception()
        val leaguePlayer2 = _dataRepository.GetLeaguePlayer(leagueId, leaguePlayer2Info.toInt())
            ?: _dataRepository.GetLeaguePlayer(leagueId, leaguePlayer2Info) ?: throw Exception()

        _dataRepository.InsertGameResult(leagueId, leaguePlayer1.LeaguePlayerId, leaguePlayer2.LeaguePlayerId, result, gameDate)
        val fullLeagueData = _dataRepository.GetFullLeagueData(leagueId)
        val player1Rating = _dataRepository.GetLeaguePlayerRating(leaguePlayer1.LeaguePlayerId)
        val player2Rating = _dataRepository.GetLeaguePlayerRating(leaguePlayer2.LeaguePlayerId)
        val newPlayer1Rating = Rating (
            Rating = CalculateNewRating(player1Rating.Rating, player2Rating.Rating, if (player1Rating.GamesPlayed > fullLeagueData.NumProvisionalGames) fullLeagueData.KFactor else fullLeagueData.ProvisionalKFactor, GetActualScore(result, true)),
            GamesPlayed = player1Rating.GamesPlayed + 1
        )
        val newPlayer2Rating = Rating (
            Rating = CalculateNewRating(player2Rating.Rating, player1Rating.Rating, if (player2Rating.GamesPlayed > fullLeagueData.NumProvisionalGames) fullLeagueData.KFactor else fullLeagueData.ProvisionalKFactor, GetActualScore(result, false)),
            GamesPlayed = player2Rating.GamesPlayed + 1
        )
        _dataRepository.UpdateLeaguePlayerRating(leaguePlayer1.LeaguePlayerId, gameDate, newPlayer1Rating)
        _dataRepository.UpdateLeaguePlayerRating(leaguePlayer2.LeaguePlayerId, gameDate, newPlayer2Rating)
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
}
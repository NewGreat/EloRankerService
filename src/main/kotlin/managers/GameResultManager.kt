package managers

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
    }
}
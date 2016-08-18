package managers

import org.joda.time.DateTime
import repositories.MySqlDataRepository

/**
 * Created by william on 8/18/16.
 */
class LeaguePlayerManager {
    private val _dataRepository: MySqlDataRepository = MySqlDataRepository()

    fun AddLeaguePlayer(leagueId: Int, leaguePlayerName: String, userId: Int?, joinDate: DateTime): Unit {
        val league = _dataRepository.GetFullLeagueData(leagueId)
        _dataRepository.InsertLeaguePlayer(league.LeagueId, leaguePlayerName, userId, league.InitialRating, joinDate)
    }
}
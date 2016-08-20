package managers

import org.joda.time.DateTime
import repositories.GetFullLeagueData
import repositories.*

/**
 * Created by william on 8/18/16.
 */
fun AddLeaguePlayer(leagueId: Int, leaguePlayerName: String, userId: Int?, joinDate: DateTime): Unit {
    val league = GetFullLeagueData(leagueId)
    InsertLeaguePlayer(league.LeagueId, leaguePlayerName, userId, league.InitialRating, joinDate)
}
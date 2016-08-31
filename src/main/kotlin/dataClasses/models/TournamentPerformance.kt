package dataClasses.models

import kotlin.comparisons.compareValuesBy

/**
 * Created by william on 8/31/16.
 */
class TournamentPerformance(tournamentId: Int, leaguePlayerId: Int) : Comparable<TournamentPerformance>{
    val TournamentId : Int = tournamentId
    val LeaguePlayerId: Int = leaguePlayerId
    var TotalPoints: Double = 0.0
    var GamesPlayed: Int = 0
    var GamesWon: Int = 0
    var GamesDrawn: Int = 0
    var GamesLost: Int = 0

    override fun compareTo(other: TournamentPerformance) = compareValuesBy(this, other, {it.TotalPoints}, {it.GamesWon}, {it.GamesDrawn}, {it.GamesLost})
}
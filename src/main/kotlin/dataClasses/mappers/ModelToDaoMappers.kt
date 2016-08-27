package dataClasses.mappers

import dataClasses.daos.GameResultDao
import dataClasses.models.GameResult
import dataClasses.models.Result

/**
 * Created by william on 8/25/16.
 */

fun ToGameResultDao(gameResult: GameResult) : GameResultDao {
    return GameResultDao(
        LeagueId = gameResult.LeagueId,
        FirstLeaguePlayerId = gameResult.FirstLeaguePlayerId,
        SecondLeaguePlayerId = gameResult.SecondLeaguePlayerId,
        Result = Result.ToInt(gameResult.Result),
        GameDate = gameResult.GameDate
    )
}
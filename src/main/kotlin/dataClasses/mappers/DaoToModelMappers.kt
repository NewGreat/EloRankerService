package dataClasses.mappers

import dataClasses.daos.GameResultDao
import dataClasses.models.GameResult
import dataClasses.models.Result
import org.joda.time.DateTimeZone

/**
 * Created by william on 8/25/16.
 */

fun ToGameResult(gameResultDao: GameResultDao) : GameResult {
    return GameResult (
        LeagueId = gameResultDao.LeagueId,
        FirstLeaguePlayerId = gameResultDao.FirstLeaguePlayerId,
        SecondLeaguePlayerId = gameResultDao.SecondLeaguePlayerId,
        Result = Result.FromInt(gameResultDao.Result),
        GameDate = gameResultDao.GameDate.toDateTime(DateTimeZone.UTC)
    )
}
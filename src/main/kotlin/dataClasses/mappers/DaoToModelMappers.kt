package dataClasses.mappers

import dataClasses.daos.GameResultDao
import dataClasses.daos.RatingDao
import dataClasses.models.GameResult
import dataClasses.models.Rating
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

fun ToRating(ratingDao: RatingDao) : Rating {
    return Rating(
        LeaguePlayerId = ratingDao.LeaguePlayerId,
        GameDate = ratingDao.GameDate.toDateTime(DateTimeZone.UTC),
        Rating = ratingDao.Rating,
        GamesPlayed = ratingDao.GamesPlayed
    )
}
package dataClasses.mappers

import dataClasses.daos.GameResultDao
import dataClasses.daos.LeaguePlayerDao
import dataClasses.daos.RatingDao
import dataClasses.models.GameResult
import dataClasses.models.LeaguePlayer
import dataClasses.models.Rating
import dataClasses.models.Result
import org.joda.time.DateTimeZone

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

fun ToRatingDao(rating: Rating) : RatingDao {
    return RatingDao(
        LeaguePlayerId = rating.LeaguePlayerId,
        GameDate = rating.GameDate,
        Rating = rating.Rating,
        GamesPlayed = rating.GamesPlayed
    )
}

fun ToLeaguePlayerDao(leaguePlayer: LeaguePlayer) : LeaguePlayerDao {
    return LeaguePlayerDao(
        LeaguePlayerId = leaguePlayer.LeaguePlayerId,
        LeagueId = leaguePlayer.LeagueId,
        UserId = leaguePlayer.UserId,
        LeaguePlayerName = leaguePlayer.LeaguePlayerName,
        RatingUpdated = leaguePlayer.RatingUpdated
    )
}
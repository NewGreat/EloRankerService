package dataClasses.mappers

import dataClasses.daos.GameResultDao
import dataClasses.daos.LeaguePlayerDao
import dataClasses.daos.RatingDao
import dataClasses.daos.TournamentDao
import dataClasses.models.*
import org.joda.time.DateTimeZone

/**
 * Created by william on 8/25/16.
 */

fun ToGameResult(gameResultDao: GameResultDao) : GameResult {
    return GameResult (
        GameResultId = gameResultDao.GameResultId,
        LeagueId = gameResultDao.LeagueId,
        FirstLeaguePlayerId = gameResultDao.FirstLeaguePlayerId,
        SecondLeaguePlayerId = gameResultDao.SecondLeaguePlayerId,
        Result = Result.FromInt(gameResultDao.Result),
        GameDate = gameResultDao.GameDate.toDateTime(DateTimeZone.UTC),
        Tournaments = mutableListOf()
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

fun ToLeaguePlayer(leaguePlayerDao: LeaguePlayerDao) : LeaguePlayer {
    return LeaguePlayer(
        LeaguePlayerId = leaguePlayerDao.LeaguePlayerId,
        LeagueId = leaguePlayerDao.LeagueId,
        UserId = leaguePlayerDao.UserId,
        LeaguePlayerName = leaguePlayerDao.LeaguePlayerName,
        RatingUpdated = leaguePlayerDao.RatingUpdated.toDateTime(DateTimeZone.UTC)
    )
}

fun ToTournament(tournamentDao: TournamentDao) : Tournament {
    return Tournament(
        TournamentId = tournamentDao.TournamentId,
        LeagueId = tournamentDao.LeagueId,
        Abbreviation = tournamentDao.Abbreviation,
        Name = tournamentDao.Name,
        Description = tournamentDao.Description,
        StartDate = tournamentDao.StartDate.toDateTime(DateTimeZone.UTC),
        EndDate = tournamentDao.EndDate.toDateTime(DateTimeZone.UTC),
        WinPoints = tournamentDao.WinPoints,
        DrawPoints = tournamentDao.DrawPoints,
        LosePoints = tournamentDao.LosePoints
    )
}
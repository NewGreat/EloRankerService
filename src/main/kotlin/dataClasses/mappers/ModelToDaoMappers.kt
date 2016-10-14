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

fun ToGameResultDao(gameResult: GameResult) : GameResultDao {
    return GameResultDao(
        GameResultId = gameResult.GameResultId,
        LeagueId = gameResult.LeagueId,
        FirstLeaguePlayerId = gameResult.FirstLeaguePlayerId,
        SecondLeaguePlayerId = gameResult.SecondLeaguePlayerId,
        Result = Result.ToInt(gameResult.Result),
        GameDate = gameResult.GameDate,
        Tournaments = gameResult.Tournaments.map{ToTournamentDao(it)}
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

fun ToTournamentDao(tournament: Tournament) : TournamentDao {
    return TournamentDao(
        TournamentId = tournament.TournamentId,
        LeagueId = tournament.LeagueId,
        Abbreviation = tournament.Abbreviation,
        Name = tournament.Name,
        StartDate = tournament.StartDate,
        EndDate = tournament.EndDate,
        Description = tournament.Description,
        WinPoints = tournament.WinPoints,
        DrawPoints = tournament.DrawPoints,
        LosePoints = tournament.LosePoints
    )
}
package repositories

import dataClasses.daos.GameResultDao
import dataClasses.daos.LeaguePlayerDao
import dataClasses.daos.RatingDao
import dataClasses.daos.TournamentDao
import dataClasses.mappers.*
import dataClasses.models.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.sql2o.Query
import org.sql2o.Sql2o
import org.sql2o.converters.Converter
import org.sql2o.converters.joda.DateTimeConverter
import org.sql2o.quirks.NoQuirks

/**
 * Created by william on 8/17/16.
 */

private val SELECT_USER: String = "SELECT UserId, FirstName, LastName, Email FROM User"

private fun CreateDbDriver(): Sql2o {
    val q = NoQuirks(
        hashMapOf(
            Pair(DateTime::class.java, DateTimeConverter(DateTimeZone.getDefault()))
        ) as Map<Class<Any>, Converter<Any>>?
    )
    return Sql2o(DB_URL, DB_USERNAME, DB_PASSWORD, q)
}

fun GetUsers(userIds: List<Int>): List<User> {

    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val userIdsString = userIds.joinToString(separator=",")
    var users = con.createQuery("$SELECT_USER WHERE (FIND_IN_SET(UserId, :pUserIds) > 0);")
        .addParameter("pUserIds", userIdsString)
        .executeAndFetch(User::class.java)
    return users
}

fun GetUser(userId: Int): User {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val user = con.createQuery("$SELECT_USER WHERE UserId = :pUserId;")
        .addParameter("pUserId", userId)
        .executeAndFetchFirst(User::class.java)
    return user
}

fun InsertUser(): Unit {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    con.createQuery("INSERT INTO User (FirstName, LastName, Email) VALUES (:pFirstName, :pLastName, :pEmail);")
        .addParameter("pFirstName", "Jackie")
        .addParameter("pLastName", "Chan")
        .addParameter("pEmail", "jChan@gmail.com")
        .executeUpdate()
}

fun InsertGameResult(gameResult: GameResult) : Unit {
    val gameResultDao = ToGameResultDao(gameResult)
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    con.createQuery("INSERT INTO EloRanker.GameResult (LeagueId, FirstLeaguePlayerId, SecondLeaguePlayerId, Result, GameDate) " +
        "VALUES (:pLeagueId, :pFirstLeaguePlayerId, :pSecondLeaguePlayerId, :pResult, :pGameDate);")
        .addParameter("pLeagueId", gameResultDao.LeagueId)
        .addParameter("pFirstLeaguePlayerId", gameResultDao.FirstLeaguePlayerId)
        .addParameter("pSecondLeaguePlayerId", gameResultDao.SecondLeaguePlayerId)
        .addParameter("pResult", gameResultDao.Result)
        .addParameter("pGameDate", gameResultDao.GameDate)
        .executeUpdate()
}

fun InsertLeaguePlayer(leaguePlayer: LeaguePlayer, initialRating: Int) {
    val sql2o = CreateDbDriver()
    var con = sql2o.beginTransaction()
    try {
        con.createQuery("INSERT INTO EloRanker.LeaguePlayer " +
            "(LeagueId, UserId, LeaguePlayerName, RatingUpdated) VALUES " +
            "(:pLeagueId, :pUserId, :pLeaguePlayerName, :pRatingUpdated)")
            .addParameter("pLeagueId", leaguePlayer.LeagueId)
            .addParameter("pUserId", leaguePlayer.UserId)
            .addParameter("pLeaguePlayerName", leaguePlayer.LeaguePlayerName)
            .addParameter("pRatingUpdated", leaguePlayer.RatingUpdated)
            .executeUpdate()

        val leaguePlayerId = con.createQuery("SELECT LeaguePlayerId FROM EloRanker.LeaguePlayer " +
            "WHERE LeagueId = :pLeagueId AND LeaguePlayerName = :pLeaguePlayerName")
            .addParameter("pLeagueId", leaguePlayer.LeagueId)
            .addParameter("pLeaguePlayerName", leaguePlayer.LeaguePlayerName)
            .executeScalar(Int::class.java)

        con.createQuery("INSERT INTO EloRanker.Rating " +
            "(LeaguePlayerId, GameDate, Rating, GamesPlayed) VALUES " +
            "(:pLeaguePlayerId, :pGameDate, :pRating, :pGamesPlayed)")
            .addParameter("pLeaguePlayerId", leaguePlayerId)
            .addParameter("pGameDate", leaguePlayer.RatingUpdated)
            .addParameter("pRating", initialRating)
            .addParameter("pGamesPlayed", 0)
            .executeUpdate()

        con.commit()
    } catch (e: Exception) {
        con.rollback()
        throw Exception("Could not create player: ${leaguePlayer.LeaguePlayerName}: $e")
    }

}

fun GetLeaguePlayer(leagueId: Int, leaguePlayerName: String): LeaguePlayer? {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val leaguePlayer = con.createQuery("SELECT LeaguePlayerId, LeagueId, UserId, LeaguePlayerName, RatingUpdated " +
        "FROM EloRanker.LeaguePlayer lp " +
        "WHERE LeagueId = :pLeagueId AND LeaguePlayerName = :pLeaguePlayerName")
        .addParameter("pLeagueId", leagueId)
        .addParameter("pLeaguePlayerName", leaguePlayerName)
        .executeAndFetchFirst(LeaguePlayerDao::class.java)
    return ToLeaguePlayer(leaguePlayer)
}

fun GetLeaguePlayer(leagueId: Int, leaguePlayerId: Int): LeaguePlayer? {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val leaguePlayer = con.createQuery("SELECT LeaguePlayerId, LeagueId, UserId, LeaguePlayerName, RatingUpdated " +
        "FROM EloRanker.LeaguePlayer " +
        "WHERE LeagueId = :pLeagueId AND LeaguePlayerId = :pLeaguePlayerId")
        .addParameter("pLeagueId", leagueId)
        .addParameter("pLeaguePlayerId", leaguePlayerId)
        .executeAndFetchFirst(LeaguePlayerDao::class.java)
    return ToLeaguePlayer(leaguePlayer)
}

fun GetLeaguePlayerRating(leaguePlayerId: Int) : Rating {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val ratingDao = con.createQuery("SELECT LeaguePlayerId, GameDate, Rating, GamesPlayed " +
        "FROM EloRanker.Rating " +
        "WHERE LeaguePlayerId = :pLeaguePlayerId " +
        "ORDER BY GameDate DESC LIMIT 1")
        .addParameter("pLeaguePlayerId", leaguePlayerId)
        .executeAndFetchFirst(RatingDao::class.java)
    return ToRating(ratingDao)
}

fun GetRatingsForLeagueOnDate(leagueId: Int, dateTime: DateTime) : List<Rating> {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val ratingDaos = con.createQuery("""
        SELECT r.LeaguePlayerId, r.GameDate, r.Rating, r.GamesPlayed
        FROM EloRanker.Rating r
        JOIN EloRanker.LeaguePlayer lp
        ON r.LeaguePlayerId = lp.LeaguePlayerId
        WHERE r.GameDate = (SELECT MAX(r2.GameDate)
        FROM EloRanker.Rating r2
        JOIN EloRanker.LeaguePlayer lp2
        ON r2.LeaguePlayerId = lp2.LeaguePlayerId
        WHERE lp2.LeagueId = :pLeagueId
        AND lp2.LeaguePlayerId = lp.LeaguePlayerId
        AND r2.GameDate <= :pDateTime);
        """)
        .addParameter("pLeagueId", leagueId)
        .addParameter("pDateTime", dateTime)
        .executeAndFetch(RatingDao::class.java)
    return ratingDaos.map { ToRating(it) }
}

fun DeleteRatingsAfterDate(leagueId: Int, dateTime: DateTime) {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    con.createQuery("""
        DELETE FROM EloRanker.Rating
        WHERE LeagueId = :pLeagueId
        AND GameDate > :pDateTime
        """)
        .addParameter("pLeagueId", leagueId)
        .addParameter("pDateTime", dateTime)
}

fun GetGameResultsOnOrAfterDate(leagueId: Int, dateTime: DateTime) : List<GameResult> {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val gameResultDaos = con.createQuery("""
        SELECT GameResultId, LeagueId, FirstLeaguePlayerId, SecondLeaguePlayerId, Result, GameDate
        FROM EloRanker.GameResult
        WHERE LeagueId = :pLeagueId
        AND GameDate >= :pDateTime;
        """)
        .addParameter("pLeagueId", leagueId)
        .addParameter("pDateTime", dateTime)
        .executeAndFetch(GameResultDao::class.java)
    return gameResultDaos.map { ToGameResult(it)}
}

fun UpdateLeaguePlayerRating(newRating: Rating) {
    val sql2o = CreateDbDriver()
    var con = sql2o.beginTransaction()
    try {
        con.createQuery("INSERT INTO EloRanker.Rating (LeaguePlayerId, GameDate, Rating, GamesPlayed) " +
            "VALUES (:pLeaguePlayerId, :pGameDate, :pRating, :pGamesPlayed)")
            .addParameter("pLeaguePlayerId", newRating.LeaguePlayerId)
            .addParameter("pGameDate", newRating.GameDate)
            .addParameter("pRating", newRating.Rating)
            .addParameter("pGamesPlayed", newRating.GamesPlayed)
            .executeUpdate()

        con.createQuery("UPDATE EloRanker.LeaguePlayer SET RatingUpdated = :pRatingUpdated " +
            "WHERE LeaguePlayerId = :pLeaguePlayerId")
            .addParameter("pRatingUpdated", newRating.GameDate)
            .addParameter("pLeaguePlayerId", newRating.LeaguePlayerId)
            .executeUpdate()

        con.commit()
    } catch (e: Exception) {
        con.rollback()
        throw Exception("Error updating rating for LeaguePlayer: ${newRating.LeaguePlayerId}: $e")
    }
}

fun GetLeague(leagueId: Int): League {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val league = con.createQuery("SELECT l.LeagueId, l.Name, l.Description, " +
        "lm.InitialRating, lm.KFactor, lm.ProvisionalKFactor, lm.NumProvisionalGames " +
        "FROM EloRanker.League l " +
        "JOIN EloRanker.LeagueMetadata lm " +
        "ON l.LeagueId = lm.LeagueId " +
        "WHERE l.LeagueId = :pLeagueId")
        .addParameter("pLeagueId", leagueId)
        .executeAndFetchFirst(League::class.java)
    return league
}

fun GetLeaguePlayers(leagueId: Int, leaguePlayerNames: List<String>) : List<LeaguePlayer?> {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val query = con.createQuery("SELECT LeaguePlayerId, LeagueId, UserId, LeaguePlayerName, RatingUpdated " +
        "FROM EloRanker.LeaguePlayer " +
        "WHERE LeagueId = :pLeagueId " +
        "AND LeaguePlayerName IN (${CreatePSForList(leaguePlayerNames)})")
        .addParameter("pLeagueId", leagueId)
    AddListParameters(query, leaguePlayerNames)
    return query.executeAndFetch(LeaguePlayerDao::class.java).map { ToLeaguePlayer(it) }
}

fun GetTournamentInfo(leagueId: Int, tournamentAbbreviation: String) : Tournament {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val tournamentDao = con.createQuery("""
        SELECT t.TournamentId, t.Abbreviation, t.Name, t.LeagueId, t.Description, t.StartDate, t.EndDate,
        tm.WinPoints, tm.DrawPoints, tm.LosePoints
        FROM EloRanker.Tournament t
        JOIN EloRanker.TournamentMetadata tm
        ON t.TournamentId = tm.TournamentId
        WHERE t.LeagueId = :pLeagueId
        AND t.Abbreviation = :pTournamentAbbreviation
        """)
            .addParameter("pLeagueId", leagueId)
            .addParameter("pTournamentAbbreviation", tournamentAbbreviation)
            .executeAndFetchFirst(TournamentDao::class.java)
    return ToTournament(tournamentDao)
}

fun GetTournamentsForLeague(leagueId: Int) : List<Tournament> {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val tournamentDao = con.createQuery("""
        SELECT t.TournamentId, t.Abbreviation, t.Name, t.LeagueId, t.Description, t.StartDate, t.EndDate,
        tm.WinPoints, tm.DrawPoints, tm.LosePoints
        FROM EloRanker.Tournament t
        JOIN EloRanker.TournamentMetadata tm
        ON t.TournamentId = tm.TournamentId
        WHERE t.LeagueId = :pLeagueId
        """)
        .addParameter("pLeagueId", leagueId)
        .executeAndFetch(TournamentDao::class.java)
    return tournamentDao.map {ToTournament(it)}
}

fun GetGameResultsForTournament(tournamentId: Int) : List<GameResult> {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val gameResultDaos = con.createQuery("""
        SELECT gr.GameResultId, gr.LeagueId, gr.FirstLeaguePlayerId, gr.SecondLeaguePlayerId, gr.Result, gr.GameDate
        FROM EloRanker.GameResult gr
        JOIN EloRanker.TournamentGameResult tgr
        ON gr.GameResultId = tgr.GameResultId
        WHERE tgr.TournamentId = :pTournamentId;
        """)
        .addParameter("pTournamentId", tournamentId)
        .executeAndFetch(GameResultDao::class.java)
    return gameResultDaos.map { ToGameResult(it)}
}

fun InsertTournament(tournament: Tournament) {
    val tournamentDao = ToTournamentDao(tournament)
    val sql2o = CreateDbDriver()
    var con = sql2o.beginTransaction()
    try {
        con.createQuery("""
        INSERT INTO EloRanker.Tournament (Abbreviation, Name, LeagueId, Description, StartDate, EndDate)
        VALUES (:pAbbreviation, :pName, :pLeagueId, :pDescription, :pStartDate, :pEndDate);
        """)
            .addParameter("pAbbreviation", tournamentDao.Abbreviation)
            .addParameter("pName", tournamentDao.Name)
            .addParameter("pLeagueId", tournamentDao.LeagueId)
            .addParameter("pDescription", tournamentDao.Description)
            .addParameter("pStartDate", tournamentDao.StartDate)
            .addParameter("pEndDate", tournamentDao.EndDate)
            .executeUpdate()

        val tournamentId = con.createQuery("SELECT TournamentId FROM EloRanker.Tournament " +
            "WHERE LeagueId = :pLeagueId AND Name = :pName")
            .addParameter("pLeagueId", tournamentDao.LeagueId)
            .addParameter("pName", tournamentDao.Name)
            .executeScalar(Int::class.java)

        con.createQuery("""
        INSERT INTO EloRanker.TournamentMetadata (TournamentId, WinPoints, DrawPoints, LosePoints)
        VALUES (:pTournamentId, :pWinPoints, :pDrawPoints, :pLosePoints);
        """)
            .addParameter("pTournamentId", tournamentId)
            .addParameter("pWinPoints", tournamentDao.WinPoints)
            .addParameter("pDrawPoints", tournamentDao.DrawPoints)
            .addParameter("pLosePoints", tournamentDao.LosePoints)
            .executeUpdate()

        con.commit()
    } catch (e: Exception) {
        con.rollback()
        throw Exception("Error creating tournament ${tournament.Name} for League: ${tournament.LeagueId}: $e")
    }

}

fun CreatePSForList(items: List<Any>) : String {
    return items.mapIndexed { i, any -> ":p$i"}.joinToString(separator=",").toString()
}

fun AddListParameters(query: Query, items: List<Any>) : Unit {
    for (i in 0..items.size-1) {
        query.addParameter("p$i", items[i])
    }
}
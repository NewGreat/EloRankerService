package repositories

import models.*
import org.sql2o.Sql2o
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.sql2o.Query
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

fun InsertGameResult(leagueId: Int, firstLeaguePlayerId: Int, secondLeaguePlayerId: Int, result: Result, gameDate: DateTime): Unit {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    con.createQuery("INSERT INTO EloRanker.GameResult (LeagueId, FirstLeaguePlayerId, SecondLeaguePlayerId, Result, GameDate) " +
        "VALUES (:pLeagueId, :pFirstLeaguePlayerId, :pSecondLeaguePlayerId, :pResult, :pGameDate);")
        .addParameter("pLeagueId", leagueId)
        .addParameter("pFirstLeaguePlayerId", firstLeaguePlayerId)
        .addParameter("pSecondLeaguePlayerId", secondLeaguePlayerId)
        .addParameter("pResult", result.ordinal)
        .addParameter("pGameDate", gameDate)
        .executeUpdate()
}

fun InsertLeaguePlayer(leagueId: Int, leaguePlayerName: String, userId: Int?, initialRating: Int, joinDate: DateTime){
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    con.createQuery("INSERT INTO EloRanker.LeaguePlayer " +
        "(LeagueId, UserId, LeaguePlayerName, RatingUpdated) VALUES " +
        "(:pLeagueId, :pUserId, :pLeaguePlayerName, :pRatingUpdated)")
        .addParameter("pLeagueId", leagueId)
        .addParameter("pUserId", userId)
        .addParameter("pLeaguePlayerName", leaguePlayerName)
        .addParameter("pRatingUpdated", joinDate)
        .executeUpdate()

    val leaguePlayerId = con.createQuery("SELECT LeaguePlayerId FROM EloRanker.LeaguePlayer " +
        "WHERE LeagueId = :pLeagueId AND LeaguePlayerName = :pLeaguePlayerName")
        .addParameter("pLeagueId", leagueId)
        .addParameter("pLeaguePlayerName", leaguePlayerName)
        .executeScalar(Int::class.java)

    con.createQuery("INSERT INTO EloRanker.Rating " +
        "(LeaguePlayerId, GameDate, Rating, GamesPlayed) VALUES " +
        "(:pLeaguePlayerId, :pGameDate, :pRating, :pGamesPlayed)")
        .addParameter("pLeaguePlayerId", leaguePlayerId)
        .addParameter("pGameDate", joinDate)
        .addParameter("pRating", initialRating)
        .addParameter("pGamesPlayed", 0)
        .executeUpdate()
}

fun GetLeaguePlayer(leagueId: Int, leaguePlayerName: String): LeaguePlayer? {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val leaguePlayer = con.createQuery("SELECT LeaguePlayerId, LeagueId, UserId, LeaguePlayerName, RatingUpdated " +
        "FROM EloRanker.LeaguePlayer lp " +
        "WHERE LeagueId = :pLeagueId AND LeaguePlayerName = :pLeaguePlayerName")
        .addParameter("pLeagueId", leagueId)
        .addParameter("pLeaguePlayerName", leaguePlayerName)
        .executeAndFetchFirst(LeaguePlayer::class.java)
    return leaguePlayer
}

fun GetLeaguePlayer(leagueId: Int, leaguePlayerId: Int): LeaguePlayer? {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val leaguePlayer = con.createQuery("SELECT LeaguePlayerId, LeagueId, UserId, LeaguePlayerName, RatingUpdated " +
        "FROM EloRanker.LeaguePlayer " +
        "WHERE LeagueId = :pLeagueId AND LeaguePlayerId = :pLeaguePlayerId")
        .addParameter("pLeagueId", leagueId)
        .addParameter("pLeaguePlayerId", leaguePlayerId)
        .executeAndFetchFirst(LeaguePlayer::class.java)
    return leaguePlayer
}

fun GetLeaguePlayerRating(leaguePlayerId: Int) : Rating {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val rating = con.createQuery("SELECT Rating, GamesPlayed " +
        "FROM EloRanker.Rating " +
        "WHERE LeaguePlayerId = :pLeaguePlayerId " +
        "ORDER BY GameDate DESC LIMIT 1")
        .addParameter("pLeaguePlayerId", leaguePlayerId)
        .executeAndFetchFirst(Rating::class.java)
    return rating
}

fun GetRatingsForLeagueOnDate(leagueId: Int, dateTime: DateTime) : List<Rating> {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    val ratings = con.createQuery("""
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
        .executeAndFetch(Rating::class.java)
    return ratings
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
    val gameResults = con.createQuery("""
        SELECT LeagueId, FirstLeaguePlayerId, SecondLeaguePlayerId, Result, GameDate
        FROM EloRanker.GameResult
        WHERE LeagueId = :pLeagueId
        AND GameDate >= :pGameDate;
        """)
        .addParameter("pLeagueId", leagueId)
        .addParameter("pDateTime", dateTime)
        .executeAndFetch(GameResult::class.java)
    return gameResults
}

fun UpdateLeaguePlayerRating(newRating: Rating) {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
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

}

fun GetFullLeagueData(leagueId: Int): League {
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
    return query.executeAndFetch(LeaguePlayer::class.java)
}

fun CreatePSForList(items: List<Any>) : String {
    return items.mapIndexed { i, any -> ":p$i"}.joinToString(separator=",").toString()
}

fun AddListParameters(query: Query, items: List<Any>) : Unit {
    for (i in 0..items.size-1) {
        query.addParameter("p$i", items[i])
    }
}
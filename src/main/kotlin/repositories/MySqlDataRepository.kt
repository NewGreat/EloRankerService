package repositories

import models.*
import org.sql2o.Sql2o
import org.joda.time.DateTime
import org.sql2o.Query

/**
 * Created by william on 8/17/16.
 */

private val DB_URL: String = "mysql://localhost/EloRanker"
private val DB_USERNAME: String = "root"
private val DB_PASSWORD: String = ""
private val SELECT_USER: String = "SELECT UserId, FirstName, LastName, Email FROM User"

private fun CreateDbDriver(): Sql2o {
    return Sql2o(DB_URL, DB_USERNAME, DB_PASSWORD)
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

fun UpdateLeaguePlayerRating(leaguePlayerId: Int, gameDate: DateTime, newRating: Rating) {
    val sql2o = CreateDbDriver()
    var con = sql2o.open()
    con.createQuery("INSERT INTO EloRanker.Rating (LeaguePlayerId, GameDate, Rating, GamesPlayed) " +
        "VALUES (:pLeaguePlayerId, :pGameDate, :pRating, :pGamesPlayed)")
        .addParameter("pLeaguePlayerId", leaguePlayerId)
        .addParameter("pGameDate", gameDate)
        .addParameter("pRating", newRating.Rating)
        .addParameter("pGamesPlayed", newRating.GamesPlayed)
        .executeUpdate()

    con.createQuery("UPDATE EloRanker.LeaguePlayer SET RatingUpdated = :pRatingUpdated " +
        "WHERE LeaguePlayerId = :pLeaguePlayerId")
        .addParameter("pRatingUpdated", gameDate)
        .addParameter("pLeaguePlayerId", leaguePlayerId)
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
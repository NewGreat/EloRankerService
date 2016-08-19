package repositories

import models.League
import models.LeaguePlayer
import models.Rating
import org.sql2o.Sql2o
import models.User
import org.joda.time.DateTime

/**
 * Created by william on 8/17/16.
 */

class MySqlDataRepository {

    private val DB_URL: String = "mysql://localhost/EloRanker"
    private val DB_USERNAME: String = "root"
    private val DB_PASSWORD: String = ""
    private val SELECT_USER: String = "SELECT UserId, FirstName, LastName, Email FROM User"

    constructor() {
        // Register the driver
        Class.forName("com.mysql.jdbc.Driver");
    }

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

    fun InsertGameResult(leagueId: Int, firstLeaguePlayerId: Int, secondLeaguePlayerId: Int, result: Int, gameDate: DateTime): Unit {
        val sql2o = CreateDbDriver()
        var con = sql2o.open()
        con.createQuery("INSERT INTO EloRanker.GameResult (LeagueId, FirstLeaguePlayerId, SecondLeaguePlayerId, Result, GameDate) " +
            "VALUES (:pLeagueId, :pFirstLeaguePlayerId, :pSecondLeaguePlayerId, :pResult, :pGameDate);")
            .addParameter("pLeagueId", leagueId)
            .addParameter("pFirstLeaguePlayerId", firstLeaguePlayerId)
            .addParameter("pSecondLeaguePlayerId", secondLeaguePlayerId)
            .addParameter("pResult", result)
            .addParameter("pGameDate", gameDate)
            .executeUpdate()
    }

    fun InsertLeaguePlayer(leagueId: Int, leaguePlayerName: String, userId: Int?, initialRating: Int, joinDate: DateTime){
        val sql2o = CreateDbDriver()
        var con = sql2o.open()
        con.createQuery("INSERT INTO EloRanker.LeaguePlayer " +
            "(LeagueId, UserId, LeaguePlayerName) VALUES " +
            "(:pLeagueId, :pUserId, :pLeaguePlayerName)")
            .addParameter("pLeagueId", leagueId)
            .addParameter("pUserId", userId)
            .addParameter("pLeaguePlayerName", leaguePlayerName)
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
        val leaguePlayer = con.createQuery("SELECT LeaguePlayerId, LeagueId, UserId, LeaguePlayerName " +
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
        val leaguePlayer = con.createQuery("SELECT LeaguePlayerId, LeagueId, UserId, LeaguePlayerName " +
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
            "ORDER BY GameDate DESC")
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
}
package server.repositories

import org.sql2o.Sql2o
import server.models.User

/**
 * Created by william on 6/20/16.
 */

class MySqlDataRepository {

    private val DB_URL: String = "mysql://localhost/LinReunion"
    private val DB_USERNAME: String = "root"
    private val DB_PASSWORD: String = "wl557760"
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


}
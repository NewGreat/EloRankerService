package repositories

import org.sql2o.Sql2o

/**
 * Created by william on 6/20/16.
 */

class MySqlDataRepository {

    fun GetUser(userId: Int): Unit {
    }

    fun InsertUser(): Unit {
        Class.forName("com.mysql.jdbc.Driver");
        val sql2o = Sql2o("mysql://localhost/LinReunion", "root", "wl557760")
        var con = sql2o.open()
        con.createQuery("INSERT INTO User (FirstName, LastName, Email) VALUES (:pFirstName, :pLastName, :pEmail);")
            .addParameter("pFirstName", "Jackie")
            .addParameter("pLastName", "Chan")
            .addParameter("pEmail", "jChan@gmail.com")
            .executeUpdate()
    }
}
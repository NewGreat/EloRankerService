package server

import org.wasabi.app.AppServer
import org.wasabi.routing.routeHandler
import server.repositories.MySqlDataRepository

/**
 * Created by william on 6/20/16.
 */
class LinFamilyServer {
    private val _dataRepository: MySqlDataRepository = MySqlDataRepository()

    fun StartServer(): Unit {
        var server = AppServer()

        server.get("/", { response.send("Hello World!") })
        server.get("/user", getUser)
        server.start()
    }

    val getUser = routeHandler {
        val userId = request.queryParams["userId"]?.toInt() ?: throw Exception("UserId cannot be null")
        response.send(_dataRepository.GetUser(userId))
    }
}

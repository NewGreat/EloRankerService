package server

import org.wasabi.app.AppServer
import org.wasabi.interceptors.enableCORSGlobally
import org.wasabi.routing.routeHandler
import server.repositories.MySqlDataRepository

/**
 * Created by william on 6/20/16.
 */
class LinFamilyServer {
    private val _dataRepository: MySqlDataRepository = MySqlDataRepository()

    fun StartServer(): Unit {
        var server = AppServer()
//        server.enableContentNegotiation()
//        server.enableMyAutoOptions()
//        val corsEntry = CORSEntry (
//            path = "*",
//            origins = "*",
//            methods = "GET,HEAD,OPTIONS,POST",
//            headers = "Origin, X-Requested-With, Content-Type, Accept, Access-Control-Allow-Origin",
//            credentials = ""
//        )

        server.get("/", { response.send("Hello World!") })
        server.get("/user", getUser)

        // Enable CORS and create an OPTIONS route for every existing route
        server.enableCORSGlobally()
        server.routes.map {
            it.path
        }.forEach {
            server.options(it, sendCors)
        }
        server.start()
    }

    val sendCors = routeHandler {
        response.addRawHeader("Access-Control-Allow-Origin", "*")
        response.addRawHeader("Access-Control-Allow-Credentials", "true");
        response.addRawHeader("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT");
        response.addRawHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.send("")
    }

    val getUser = routeHandler {
        val userId = request.queryParams["userId"]?.toInt() ?: throw Exception("UserId cannot be null")
        response.send(_dataRepository.GetUser(userId), "application/json")
    }
}

package server

import org.wasabi.app.AppServer
import org.wasabi.interceptors.CORSInterceptor
import org.wasabi.interceptors.enableAutoOptions
import org.wasabi.interceptors.enableCORS
import org.wasabi.interceptors.enableCORSGlobally
import org.wasabi.protocol.http.CORSEntry
import org.wasabi.routing.routeHandler
import server.repositories.MySqlDataRepository

/**
 * Created by william on 6/20/16.
 */
class LinFamilyServer {
    private val _dataRepository: MySqlDataRepository = MySqlDataRepository()

    fun StartServer(): Unit {
        val server = AppServer()
//        server.enableCORSGlobally()
//        server.enableAutoOptions()
//        val corsEntry = CORSEntry (
//            path = "*",
//            origins = "*",
//            methods = "GET, POST, PUT, DELETE, OPTIONS",
//            headers = "Origin, X-Requested-With, Content-Type, Accept, Access-Control-Allow-Origin",
//            credentials = ""
//        )
//        server.enableCORS(arrayListOf(corsEntry))
        server.options("/user", sendCors)
        server.get("/", { response.send("Hello World!") })
        server.get("/user", getUser)
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
        response.addRawHeader("Access-Control-Allow-Origin", "*")
        response.send(_dataRepository.GetUser(userId), "application/json")
    }
}

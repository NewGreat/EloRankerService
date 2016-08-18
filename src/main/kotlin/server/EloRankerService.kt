package server

import io.netty.handler.codec.http.HttpMethod
import managers.GameResultManager
import managers.LeaguePlayerManager
import org.joda.time.format.DateTimeFormat
import org.wasabi.app.AppServer
import org.wasabi.interceptors.enableCORS
import org.wasabi.interceptors.enableCORSGlobally
import org.wasabi.protocol.http.CORSEntry
import org.wasabi.protocol.http.Response
import org.wasabi.routing.routeHandler
import repositories.MySqlDataRepository

/**
 * Created by william on 8/17/16.
 */
class EloRankerService {
    private val _leaguePlayerManager: LeaguePlayerManager = LeaguePlayerManager()
    private val _gameResultManager: GameResultManager = GameResultManager()

    fun StartServer(): Unit {
        var server = AppServer()

        server.get("/", { response.send("Hello World!") })
        server.post("/gameresult/record", RecordGameResult)
        server.post("/player/create", AddLeaguePlayer)

        // Enable CORS and create an OPTIONS route for every existing route
        val corsEntry = CORSEntry (
            path = "*",
            origins = "localhost:3474",
            methods = setOf(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.POST),
            headers = "Origin, X-Requested-With, Content-Type, Accept, Access-Control-Allow-Origin",
            credentials = ""
        )
        server.enableCORS(arrayListOf(corsEntry))
        server.start()
    }

    val AddLeaguePlayer = routeHandler {
        val leagueId = request.bodyParams["leagueId"].toString().toInt()
        val leaguePlayerName = request.bodyParams["leaguePlayerName"].toString()
        val userId = request.bodyParams["userId"]?.toString()?.toInt() ?: null
        val joinDateString = request.bodyParams["joinDate"].toString()
        val formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss")
        val joinDate = formatter.parseDateTime(joinDateString)

        _leaguePlayerManager.AddLeaguePlayer(leagueId, leaguePlayerName, userId, joinDate)
        response.send("Added $leaguePlayerName to League $leagueId", "application/json")
    }

    val RecordGameResult = routeHandler {
        val leagueId = request.bodyParams["leagueId"].toString().toInt()
        val leaguePlayer1Info = request.bodyParams["leagueplayer1"].toString()
        val leaguePlayer2Info = request.bodyParams["leagueplayer2"].toString()
        val result = request.bodyParams["result"].toString().toInt()
        val gameDateString = request.bodyParams["gameDate"].toString()
        val formatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss")
        val gameDate = formatter.parseDateTime(gameDateString)

        _gameResultManager.RecordGameResult(leagueId, leaguePlayer1Info, leaguePlayer2Info, result, gameDate)
        response.send("Recorded game result for League $leagueId", "application/json")
    }


}

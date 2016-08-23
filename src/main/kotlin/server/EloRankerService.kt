package server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import helpers.ParseDateTime
import io.netty.handler.codec.http.HttpMethod
import managers.AddLeaguePlayer
import managers.RecordLeagueGameResults
import models.GameResult
import org.wasabi.app.AppServer
import org.wasabi.interceptors.enableCORS
import org.wasabi.protocol.http.CORSEntry
import org.wasabi.protocol.http.StatusCodes
import org.wasabi.routing.routeHandler

/**
 * Created by william on 8/17/16.
 */

val mapper: ObjectMapper = jacksonObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, false)

fun main(args: Array<String>) {
    StartServer()
}

fun StartServer(): Unit {
    var server = AppServer()

    server.get("/", { response.send("Hello World!") })
    server.post("/games/record", RecordLeagueGameResults)
    server.post("/player/create", AddLeaguePlayer)
    server.exception(Exception::class, {
        response.setStatus(StatusCodes.PreconditionFailed)
        response.send("Error: ${exception.message}")
    })

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
    val leagueId = request.bodyParams["leagueId"] as Int
    val leaguePlayerName = request.bodyParams["leaguePlayerName"] as String
    val userId = request.bodyParams["userId"] as Int?
    val joinDateString = request.bodyParams["joinDate"] as String
    val joinDate = ParseDateTime(joinDateString)

    AddLeaguePlayer(leagueId, leaguePlayerName, userId, joinDate)
    response.send("Added $leaguePlayerName to League $leagueId", "application/json")
}

val RecordLeagueGameResults = routeHandler {
    val leagueId = request.bodyParams["leagueId"] as Int
    val gameDateString = request.bodyParams["gameDate"] as String
    val gameDate = ParseDateTime(gameDateString)
    val json = mapper.writeValueAsString(request.bodyParams["gameResults"])
    val gameResults : List<GameResult> = mapper.readValue(json)

    RecordLeagueGameResults(leagueId, gameDate, gameResults)
    response.send("Recorded game results for League $leagueId", "application/json")
}

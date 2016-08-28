package server

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import contracts.AddPlayerCommand
import helpers.ParseDateTime
import io.netty.handler.codec.http.HttpMethod
import managers.AddLeaguePlayer
import managers.ConvertToGameResults
import contracts.GameResultCommand
import managers.ConvertToLeaguePlayers
import org.wasabi.app.AppServer
import org.wasabi.interceptors.enableCORS
import org.wasabi.protocol.http.CORSEntry
import org.wasabi.protocol.http.StatusCodes
import org.wasabi.routing.routeHandler

/**
 * Created by william on 8/17/16.
 */

val mapper: ObjectMapper = jacksonObjectMapper()
    .configure(SerializationFeature.INDENT_OUTPUT, false)
    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)

fun main(args: Array<String>) {
    StartServer()
}

fun StartServer(): Unit {
    var server = AppServer()

    server.get("/", { response.send("Hello World!") })
    server.post("/games/record", RecordLeagueGameResults)
    server.post("/players/add", AddLeaguePlayers)
    server.post("/ratings/recalculate", RecalculateRatings)
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

val AddLeaguePlayers = routeHandler {
    val leagueId = request.bodyParams["leagueId"] as Int
    val json = mapper.writeValueAsString(request.bodyParams["leaguePlayers"])
    val addPlayerCommands : List<AddPlayerCommand> = mapper.readValue(json)
    val leaguePlayers = ConvertToLeaguePlayers(leagueId, addPlayerCommands)
    val (successful, failed) = managers.AddLeaguePlayers(leagueId, leaguePlayers)
    response.send("Added $successful to League $leagueId. Failed to add $failed", "application/json")
}

val RecordLeagueGameResults = routeHandler {
    val leagueId = request.bodyParams["leagueId"] as Int
    val gameDateString = request.bodyParams["gameDate"] as String
    val gameDate = ParseDateTime(gameDateString)
    val json = mapper.writeValueAsString(request.bodyParams["gameResults"])
    val gameResultCommands : List<GameResultCommand> = mapper.readValue(json)
    val gameResults = ConvertToGameResults(leagueId, gameDate, gameResultCommands)
    managers.RecordLeagueGameResults(gameResults)
    response.send("Recorded game results for League $leagueId", "application/json")
}

val RecalculateRatings = routeHandler {
    val leagueId = request.bodyParams["leagueId"] as Int
    val recalculateDateString = request.bodyParams["recalculateDate"] as String
    val recalculateDate = ParseDateTime(recalculateDateString)
    managers.RecalculateRatings(leagueId, recalculateDate)
}
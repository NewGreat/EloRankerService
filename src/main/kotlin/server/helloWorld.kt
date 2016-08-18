package server

/**
 * Created by william on 6/20/16.
 */

fun getGreeting(): String {
    val words = mutableListOf<String>()
    words.add("Hello,")
    words.add("world!")

    return words.joinToString(separator = " ")
}

fun main(args: Array<String>) {
    val server = EloRankerService()
    server.StartServer()
}

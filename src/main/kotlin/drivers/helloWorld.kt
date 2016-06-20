package drivers

import repositories.MySqlDataRepository
import server.LinFamilyServer

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
//    val d = LinkedInDriver()
//    d.RequestAuthorizationCode()

    val m = MySqlDataRepository()
    m.InsertUser()

    println(getGreeting())
//    val server = LinFamilyServer()
//    server.StartServer()
}

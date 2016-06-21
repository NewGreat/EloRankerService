package server.drivers

import server.repositories.MySqlDataRepository
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
//    m.InsertUser()
    val user = m.GetUser(3)
    println(user)
    val users = m.GetUsers(listOf(1,2,3))
    println(users)


    println(getGreeting())
//    val server = LinFamilyServer()
//    server.StartServer()
}

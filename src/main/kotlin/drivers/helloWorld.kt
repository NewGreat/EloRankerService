package drivers

import com.github.kittinunf.fuel.httpGet

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
    val d = LinkedInDriver()
    
    println(getGreeting())
    val (request, response, result) = "http://httpbin.org/get".httpGet().responseString()
//    println(request)
    println(response)
//    println(result)
}

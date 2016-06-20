package server

import org.wasabi.app.AppServer

/**
 * Created by william on 6/20/16.
 */
class LinFamilyServer {
    fun StartServer(): Unit {
        var server = AppServer()

        server.get("/", { response.send("Hello World!") })

        server.start()
    }
}

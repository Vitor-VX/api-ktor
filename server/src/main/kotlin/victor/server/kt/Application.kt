package victor.server.kt

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import victor.server.kt.routes.configureRouting

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
}
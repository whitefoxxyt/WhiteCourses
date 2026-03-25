package fr.white.appcourse

import fr.white.appcourse.controllers.registerListeRoutes
import fr.white.appcourse.services.ListeService
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module(
    listeService: ListeService = ListeService.fromEnvironment(),
    accessLogger: (String) -> Unit = {}
) {
    configureObservability(accessLogger)

    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        registerListeRoutes(listeService)
    }
}
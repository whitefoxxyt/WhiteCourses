package fr.white.appcourse

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.logging.KtorSimpleLogger
import kotlin.system.measureTimeMillis

private val observabilityLogger = KtorSimpleLogger("fr.white.appcourse.observability")

fun Application.configureObservability(
    accessLogger: (String) -> Unit = { observabilityLogger.info(it) }
) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            observabilityLogger.error(
                "unhandled_exception method=${call.request.httpMethod.value} path=${call.request.path()}",
                cause
            )
            call.respond(HttpStatusCode.InternalServerError, "erreur interne")
        }
    }

    intercept(ApplicationCallPipeline.Monitoring) {
        var failure: Throwable? = null
        val durationMs = measureTimeMillis {
            try {
                proceed()
            } catch (t: Throwable) {
                failure = t
                throw t
            }
        }

        val status = call.response.status()?.value ?: if (failure != null) 500 else 200
        accessLogger(
            "method=${call.request.httpMethod.value} path=${call.request.path()} status=$status durationMs=$durationMs"
        )
    }

    routing {
        get("/health") {
            call.respondText("OK")
        }
    }
}


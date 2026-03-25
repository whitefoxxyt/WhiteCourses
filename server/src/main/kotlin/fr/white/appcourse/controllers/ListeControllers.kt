package fr.white.appcourse.controllers

import fr.white.appcourse.ApiConstants
import fr.white.appcourse.services.ListeQueryResult
import fr.white.appcourse.services.ListeService
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val jsonEncoder = Json { prettyPrint = false }

fun Route.registerListeRoutes(listeService: ListeService) {
    route(ApiConstants.ENDPOINT_LISTES) {
        get("/{listId}") {
            val listId = call.parameters["listId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "listId invalide")

            if (listId <= 0) {
                return@get call.respond(HttpStatusCode.BadRequest, "listId invalide")
            }

            val magasinIdRaw = call.request.queryParameters[ApiConstants.PARAM_MAGASIN_ID]
            val magasinId = when {
                magasinIdRaw.isNullOrBlank() -> null
                else -> magasinIdRaw.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "magasinId invalide")
            }

            if (magasinId != null && magasinId <= 0) {
                return@get call.respond(HttpStatusCode.BadRequest, "magasinId invalide")
            }

            try {
                when (val result = listeService.getListeTriee(listId, magasinId)) {
                    is ListeQueryResult.Found -> {
                        val payload = jsonEncoder.encodeToString(result.items)
                        call.respondText(payload, ContentType.Application.Json, HttpStatusCode.OK)
                    }
                    ListeQueryResult.ListeNotFound -> {
                        call.respond(HttpStatusCode.NotFound, "liste introuvable")
                    }
                }
            } catch (_: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "erreur interne")
            }
        }

        patch("/item/{itemId}/etat") {
            val itemId = call.parameters["itemId"]?.toIntOrNull()
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "itemId invalide")
            val achete = call.request.queryParameters[ApiConstants.PARAM_ACHETE]?.toBooleanStrictOrNull()
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "parametre achete invalide")

            call.respondText(
                "{\"itemId\":$itemId,\"estAchete\":$achete}",
                ContentType.Application.Json,
                HttpStatusCode.OK
            )
        }
    }
}
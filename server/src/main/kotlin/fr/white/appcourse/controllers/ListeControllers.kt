package fr.white.appcourse.controllers

import fr.white.appcourse.ApiConstants
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route

fun Route.registerListeRoutes() {
    route(ApiConstants.ENDPOINT_LISTES) {
        get("/{listId}") {
            val listId = call.parameters["listId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "listId invalide")

            if (listId <= 0) {
                return@get call.respond(HttpStatusCode.BadRequest, "listId invalide")
            }

            // Stub de contrat API: la persistance MySQL arrive dans les US suivantes.
            val payload = """
                [
                  {"id":1,"nom":"Pommes","quantite":4,"categorieNom":"Fruits","PositionEnRayon":1,"estAchete":false},
                  {"id":2,"nom":"Lait","quantite":1,"categorieNom":"Frais","PositionEnRayon":2,"estAchete":false},
                  {"id":3,"nom":"Eau","quantite":6,"categorieNom":"Boissons","PositionEnRayon":3,"estAchete":false}
                ]
            """.trimIndent()

            call.respondText(payload, ContentType.Application.Json)
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
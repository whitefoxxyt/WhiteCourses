package fr.white.appcourse.controllers

import fr.white.appcourse.ApiConstants
import fr.white.appcourse.services.ListeQueryResult
import fr.white.appcourse.services.ListeService
import fr.white.appcourse.services.ToggleAchatResult
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route

private fun String.asJsonString(): String = replace("\\", "\\\\").replace("\"", "\\\"")

private fun buildItemsJson(items: List<fr.white.appcourse.models.ProduitItem>): String {
    return items.joinToString(separator = ",", prefix = "[", postfix = "]") { item ->
        val positionValue = item.PositionEnRayon?.toString() ?: "null"
        "{\"id\":${item.id},\"nom\":\"${item.nom.asJsonString()}\",\"quantite\":${item.quantite},\"categorieNom\":\"${item.categorieNom.asJsonString()}\",\"PositionEnRayon\":$positionValue,\"estAchete\":${item.estAchete}}"
    }
}

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
                        val payload = buildItemsJson(result.items)
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
            if (itemId <= 0) {
                return@patch call.respond(HttpStatusCode.BadRequest, "itemId invalide")
            }
            val achete = call.request.queryParameters[ApiConstants.PARAM_ACHETE]?.toBooleanStrictOrNull()
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "parametre achete invalide")

            try {
                when (val result = listeService.setItemAchete(itemId, achete)) {
                    is ToggleAchatResult.Updated -> {
                        val payload = "{\"itemId\":${result.itemId},\"estAchete\":${result.estAchete}}"
                        call.respondText(payload, ContentType.Application.Json, HttpStatusCode.OK)
                    }
                    ToggleAchatResult.ItemNotFound -> {
                        call.respond(HttpStatusCode.NotFound, "item introuvable")
                    }
                }
            } catch (_: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "erreur interne")
            }
        }
    }
}
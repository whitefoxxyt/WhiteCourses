package fr.white.appcourse.repository

import fr.white.appcourse.models.*
import fr.white.appcourse.network.client
import io.ktor.client.call.*
import io.ktor.client.request.*

class ShoppingRepository(
    private val baseUrl: String = "http://10.0.2.2:8080") {

    /**
     *  Appelle l'API Spring boot pour récupérer la liste triée
     */
    suspend fun getListeTriee(listId: Int, magasinId: Int?): Result<List<ProduitItem>> {
        return runCatching {
            client.get("$baseUrl/api/listes/$listId") {
                parameter("magasinId", magasinId)
            }.body<List<ProduitItem>>()
        }
    }

    /**
     *  Mise à jour du statut d'achat d'un article
     */
    suspend fun toggleAchat(itemId: Int, estAchete: Boolean): Result<Unit> {
        return runCatching {
            client.patch("$baseUrl/api/listes/item/$itemId/etat") {
                parameter("achete", estAchete)
            }
        }
    }

}
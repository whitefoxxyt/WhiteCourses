package fr.white.appcourse.repository

import fr.white.appcourse.ApiConstants
import fr.white.appcourse.models.ProduitItem
import fr.white.appcourse.network.ShoppingApiContract
import fr.white.appcourse.network.client
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch

class ShoppingRepository(
    private val baseUrl: String = ApiConstants.BASE_URL
) {
    /**
     * Appelle l'API Ktor pour récupérer la liste triée.
     */
    suspend fun getListeTriee(listId: Int, magasinId: Int?): Result<List<ProduitItem>> {
        return runCatching {
            client.get("$baseUrl${ShoppingApiContract.listePath(listId)}") {
                parameter(ApiConstants.PARAM_MAGASIN_ID, magasinId)
            }.body<List<ProduitItem>>()
        }
    }

    /**
     * Met à jour le statut d'achat d'un article.
     */
    suspend fun toggleAchat(itemId: Int, estAchete: Boolean): Result<Unit> {
        return runCatching {
            client.patch("$baseUrl${ShoppingApiContract.toggleAchatPath(itemId)}") {
                parameter(ApiConstants.PARAM_ACHETE, estAchete)
            }
        }
    }

}
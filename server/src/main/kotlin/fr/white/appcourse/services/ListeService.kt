package fr.white.appcourse.services

import fr.white.appcourse.toDto
import fr.white.appcourse.models.ProduitItem
import fr.white.appcourse.repositories.DatabaseConfig
import fr.white.appcourse.repositories.JdbcProduitRepository
import fr.white.appcourse.repositories.ProduitRepository
import fr.white.appcourse.repositories.UpdateItemEtatResult

sealed interface ListeQueryResult {
    data class Found(val items: List<ProduitItem>) : ListeQueryResult
    data object ListeNotFound : ListeQueryResult
}

sealed interface ToggleAchatResult {
    data class Updated(val itemId: Int, val estAchete: Boolean) : ToggleAchatResult
    data object ItemNotFound : ToggleAchatResult
}

class ListeService(
    private val produitRepository: ProduitRepository
) {
    fun getListeTriee(listId: Int, magasinId: Int?): ListeQueryResult {
        if (!produitRepository.listeExists(listId)) {
            return ListeQueryResult.ListeNotFound
        }

        val items = produitRepository
            .findProduitsByListe(listId, magasinId)
            .map { it.toDto() }

        return ListeQueryResult.Found(items)
    }

    fun setItemAchete(itemId: Int, estAchete: Boolean): ToggleAchatResult {
        return when (produitRepository.setItemAchete(itemId, estAchete)) {
            UpdateItemEtatResult.Updated -> ToggleAchatResult.Updated(itemId, estAchete)
            UpdateItemEtatResult.NotFound -> ToggleAchatResult.ItemNotFound
        }
    }

    companion object {
        fun fromEnvironment(): ListeService {
            return ListeService(JdbcProduitRepository(DatabaseConfig.fromEnvironment()))
        }
    }
}
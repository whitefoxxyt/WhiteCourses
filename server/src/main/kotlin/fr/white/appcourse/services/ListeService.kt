package fr.white.appcourse.services

import fr.white.appcourse.toDto
import fr.white.appcourse.models.ProduitItem
import fr.white.appcourse.repositories.DatabaseConfig
import fr.white.appcourse.repositories.JdbcProduitRepository
import fr.white.appcourse.repositories.ProduitRepository

sealed interface ListeQueryResult {
    data class Found(val items: List<ProduitItem>) : ListeQueryResult
    data object ListeNotFound : ListeQueryResult
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

    companion object {
        fun fromEnvironment(): ListeService {
            return ListeService(JdbcProduitRepository(DatabaseConfig.fromEnvironment()))
        }
    }
}
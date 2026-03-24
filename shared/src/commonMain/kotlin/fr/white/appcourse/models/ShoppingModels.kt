package fr.white.appcourse.models

import kotlinx.serialization.Serializable

@Serializable
data class ProduitItem(
    val id: Int,
    val nom: String,
    val quantite: Int,
    val categorieNom: String,
    val PositionEnRayon: Int?,
    val estAchete: Boolean = false
)

@Serializable
data class MagazinSimple(
    val id: Int,
    val nom: String,
    val enseigne: String?
)

@Serializable
data class ListeCourses(
    val id: Int,
    val nom: String,
    val items: List<ProduitItem> = emptyList()
)

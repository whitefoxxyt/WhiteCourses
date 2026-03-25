package fr.white.appcourse.models

data class ListeProduit(
    val id: Int,
    val nom: String,
    val quantite: Int,
    val categorieNom: String,
    val estAchete: Boolean
)

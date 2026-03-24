package fr.white.appcourse

import fr.white.appcourse.models.ProduitItem

fun ListesProduits.toDto(position: Int?): ProduitItem {
    return ProduitItem(
        id = this.id,
        nom = this.nom,
        quantite = this.quantite,
        categorieNom = this.categorieNom,
        PositionEnRayon = position,
        estAchete = this.estAchete
    )
}
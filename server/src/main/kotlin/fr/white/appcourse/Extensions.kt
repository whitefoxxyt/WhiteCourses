package fr.white.appcourse

import fr.white.appcourse.models.ListeProduit
import fr.white.appcourse.models.ProduitItem

fun ListeProduit.toDto(): ProduitItem {
    return ProduitItem(
        id = id,
        nom = nom,
        quantite = quantite,
        categorieNom = categorieNom,
        PositionEnRayon = positionEnRayon,
        estAchete = estAchete
    )
}
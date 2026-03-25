package fr.white.appcourse.network

import fr.white.appcourse.ApiConstants

object ShoppingApiContract {
    fun listePath(listId: Int): String = "${ApiConstants.ENDPOINT_LISTES}/$listId"

    fun toggleAchatPath(itemId: Int): String = "${ApiConstants.ENDPOINT_LISTES}/item/$itemId/etat"
}


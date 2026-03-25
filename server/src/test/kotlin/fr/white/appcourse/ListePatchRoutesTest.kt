package fr.white.appcourse

import fr.white.appcourse.models.ListeProduit
import fr.white.appcourse.repositories.ProduitRepository
import fr.white.appcourse.repositories.UpdateItemEtatResult
import fr.white.appcourse.services.ListeService
import io.ktor.client.request.patch
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ListePatchRoutesTest {

    @Test
    fun `patch toggle returns 200 for existing item`() = testApplication {
        application {
            module(ListeService(FakePatchProduitRepository(existingItems = setOf(10))))
        }

        val response = client.patch("/api/listes/item/10/etat?achete=true")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("{\"itemId\":10,\"estAchete\":true}", response.bodyAsText())
    }

    @Test
    fun `patch toggle is idempotent when called twice with same value`() = testApplication {
        application {
            module(ListeService(FakePatchProduitRepository(existingItems = setOf(10))))
        }

        val first = client.patch("/api/listes/item/10/etat?achete=true")
        val second = client.patch("/api/listes/item/10/etat?achete=true")

        assertEquals(HttpStatusCode.OK, first.status)
        assertEquals(HttpStatusCode.OK, second.status)
        assertEquals("{\"itemId\":10,\"estAchete\":true}", second.bodyAsText())
    }

    @Test
    fun `patch toggle returns 404 for missing item`() = testApplication {
        application {
            module(ListeService(FakePatchProduitRepository(existingItems = emptySet())))
        }

        val response = client.patch("/api/listes/item/999/etat?achete=false")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("item introuvable", response.bodyAsText())
    }

    @Test
    fun `patch toggle returns 400 for invalid achete query`() = testApplication {
        application {
            module(ListeService(FakePatchProduitRepository(existingItems = setOf(10))))
        }

        val response = client.patch("/api/listes/item/10/etat?achete=abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `patch toggle returns 400 for invalid itemId`() = testApplication {
        application {
            module(ListeService(FakePatchProduitRepository(existingItems = setOf(10))))
        }

        val response = client.patch("/api/listes/item/0/etat?achete=true")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `patch toggle returns 400 for non numeric itemId`() = testApplication {
        application {
            module(ListeService(FakePatchProduitRepository(existingItems = setOf(10))))
        }

        val response = client.patch("/api/listes/item/abc/etat?achete=true")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `patch toggle returns 400 when achete query is missing`() = testApplication {
        application {
            module(ListeService(FakePatchProduitRepository(existingItems = setOf(10))))
        }

        val response = client.patch("/api/listes/item/10/etat")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `patch toggle returns 500 when service throws`() = testApplication {
        application {
            module(
                ListeService(
                    FakePatchProduitRepository(
                        existingItems = setOf(10),
                        throwOnSet = true
                    )
                )
            )
        }

        val response = client.patch("/api/listes/item/10/etat?achete=true")
        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }
}

private class FakePatchProduitRepository(
    existingItems: Set<Int>,
    private val throwOnSet: Boolean = false
) : ProduitRepository {
    private val existingItems = existingItems.toMutableSet()

    override fun listeExists(listId: Int): Boolean = false

    override fun findProduitsByListe(listId: Int, magasinId: Int?): List<ListeProduit> = emptyList()

    override fun setItemAchete(itemId: Int, estAchete: Boolean): UpdateItemEtatResult {
        if (throwOnSet) error("db offline")
        return if (existingItems.contains(itemId)) {
            UpdateItemEtatResult.Updated
        } else {
            UpdateItemEtatResult.NotFound
        }
    }
}

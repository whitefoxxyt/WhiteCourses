package fr.white.appcourse

import fr.white.appcourse.models.ListeProduit
import fr.white.appcourse.repositories.ProduitRepository
import fr.white.appcourse.repositories.UpdateItemEtatResult
import fr.white.appcourse.services.ListeService
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ListeRoutesTest {

    @Test
    fun `get liste triee returns 200 with sorted payload`() = testApplication {
        val repository = FakeProduitRepository(
            exists = true,
            items = listOf(
                ListeProduit(1, "Pommes", 2, "Fruits", 1, false),
                ListeProduit(2, "Lait", 1, "Frais", 2, true)
            )
        )

        application {
            module(ListeService(repository))
        }

        val response = client.get("/api/listes/1?magasinId=2")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            "[{\"id\":1,\"nom\":\"Pommes\",\"quantite\":2,\"categorieNom\":\"Fruits\",\"PositionEnRayon\":1,\"estAchete\":false},{\"id\":2,\"nom\":\"Lait\",\"quantite\":1,\"categorieNom\":\"Frais\",\"PositionEnRayon\":2,\"estAchete\":true}]",
            response.bodyAsText()
        )
    }

    @Test
    fun `get liste triee returns 400 when magasinId is invalid`() = testApplication {
        application {
            module(ListeService(FakeProduitRepository(exists = true, items = emptyList())))
        }

        val response = client.get("/api/listes/1?magasinId=abc")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `get liste triee returns 404 when list does not exist`() = testApplication {
        application {
            module(ListeService(FakeProduitRepository(exists = false, items = emptyList())))
        }

        val response = client.get("/api/listes/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("liste introuvable", response.bodyAsText())
    }
}

private class FakeProduitRepository(
    private val exists: Boolean,
    private val items: List<ListeProduit>
) : ProduitRepository {
    override fun listeExists(listId: Int): Boolean = exists

    override fun findProduitsByListe(listId: Int, magasinId: Int?): List<ListeProduit> = items

    override fun setItemAchete(itemId: Int, estAchete: Boolean): UpdateItemEtatResult {
        return UpdateItemEtatResult.NotFound
    }
}

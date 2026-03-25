package fr.white.appcourse

import fr.white.appcourse.models.ListeProduit
import fr.white.appcourse.repositories.ProduitRepository
import fr.white.appcourse.repositories.UpdateItemEtatResult
import fr.white.appcourse.services.ListeService
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObservabilityTest {

    @Test
    fun `health endpoint returns OK`() = testApplication {
        application {
            module(
                listeService = ListeService(FakeFailingProduitRepository(throwOnFind = false, throwOnSet = false))
            )
        }

        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OK", response.bodyAsText())
    }

    @Test
    fun `global exception handler returns 500`() = testApplication {
        application {
            module(
                listeService = ListeService(FakeFailingProduitRepository(throwOnFind = true, throwOnSet = false))
            )
        }

        val response = client.get("/api/listes/1?magasinId=1")
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("erreur interne", response.bodyAsText())
    }

    @Test
    fun `access log contains method path status and duration`() = testApplication {
        val logs = mutableListOf<String>()

        application {
            module(
                listeService = ListeService(FakeFailingProduitRepository(throwOnFind = false, throwOnSet = false)),
                accessLogger = { logs += it }
            )
        }

        client.patch("/api/listes/item/10/etat?achete=true")

        val line = logs.lastOrNull() ?: error("no access log captured")
        assertTrue(line.contains("method=PATCH"))
        assertTrue(line.contains("path=/api/listes/item/10/etat"))
        assertTrue(line.contains("status=404"))
        assertTrue(line.contains("durationMs="))
    }
}

private class FakeFailingProduitRepository(
    private val throwOnFind: Boolean,
    private val throwOnSet: Boolean
) : ProduitRepository {
    override fun listeExists(listId: Int): Boolean = true

    override fun findProduitsByListe(listId: Int, magasinId: Int?): List<ListeProduit> {
        if (throwOnFind) error("boom get")
        return emptyList()
    }

    override fun setItemAchete(itemId: Int, estAchete: Boolean): UpdateItemEtatResult {
        if (throwOnSet) error("boom patch")
        return UpdateItemEtatResult.NotFound
    }
}


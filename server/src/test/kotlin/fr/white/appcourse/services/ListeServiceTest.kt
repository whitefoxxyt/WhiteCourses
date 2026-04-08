package fr.white.appcourse.services

import fr.white.appcourse.models.ListeProduit
import fr.white.appcourse.repositories.ProduitRepository
import fr.white.appcourse.repositories.UpdateItemEtatResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListeServiceTest {

    private val mockRepository = mockk<ProduitRepository>()
    private val service = ListeService(mockRepository)

    @Test
    fun `getListeTriee returns Found when liste exists`() {
        val listId = 1
        val magasinId = 1
        val mockData = listOf(
            ListeProduit(1, "Pomme", 2, "Fruits", 1, false),
            ListeProduit(2, "Carotte", 1, "Légumes", 2, false)
        )

        every { mockRepository.listeExists(listId) } returns true
        every { mockRepository.findProduitsByListe(listId, magasinId) } returns mockData

        val result = service.getListeTriee(listId, magasinId)

        assertTrue(result is ListeQueryResult.Found)
        assertEquals(2, (result as ListeQueryResult.Found).items.size)
        verify { mockRepository.listeExists(listId) }
        verify { mockRepository.findProduitsByListe(listId, magasinId) }
    }

    @Test
    fun `getListeTriee returns ListeNotFound when liste does not exist`() {
        val listId = 999

        every { mockRepository.listeExists(listId) } returns false

        val result = service.getListeTriee(listId, null)

        assertTrue(result is ListeQueryResult.ListeNotFound)
        verify { mockRepository.listeExists(listId) }
        verify(exactly = 0) { mockRepository.findProduitsByListe(any(), any()) }
    }

    @Test
    fun `setItemAchete returns Updated when item exists`() {
        val itemId = 1
        val estAchete = true

        every { mockRepository.setItemAchete(itemId, estAchete) } returns UpdateItemEtatResult.Updated

        val result = service.setItemAchete(itemId, estAchete)

        assertTrue(result is ToggleAchatResult.Updated)
        assertEquals(itemId, (result as ToggleAchatResult.Updated).itemId)
        assertEquals(estAchete, result.estAchete)
        verify { mockRepository.setItemAchete(itemId, estAchete) }
    }

    @Test
    fun `setItemAchete returns ItemNotFound when item does not exist`() {
        val itemId = 999
        val estAchete = true

        every { mockRepository.setItemAchete(itemId, estAchete) } returns UpdateItemEtatResult.NotFound

        val result = service.setItemAchete(itemId, estAchete)

        assertTrue(result is ToggleAchatResult.ItemNotFound)
        verify { mockRepository.setItemAchete(itemId, estAchete) }
    }
}

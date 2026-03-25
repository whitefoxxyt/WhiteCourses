package fr.white.appcourse

import fr.white.appcourse.models.ListeProduit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ExtensionsTest {

    @Test
    fun `toDto maps every field with non null position`() {
        val source = ListeProduit(
            id = 42,
            nom = "Yaourt",
            quantite = 3,
            categorieNom = "Frais",
            positionEnRayon = 7,
            estAchete = true
        )

        val dto = source.toDto()

        assertEquals(42, dto.id)
        assertEquals("Yaourt", dto.nom)
        assertEquals(3, dto.quantite)
        assertEquals("Frais", dto.categorieNom)
        assertEquals(7, dto.PositionEnRayon)
        assertEquals(true, dto.estAchete)
    }

    @Test
    fun `toDto keeps null position`() {
        val source = ListeProduit(
            id = 1,
            nom = "Pommes",
            quantite = 2,
            categorieNom = "Fruits",
            positionEnRayon = null,
            estAchete = false
        )

        val dto = source.toDto()

        assertNull(dto.PositionEnRayon)
        assertEquals(false, dto.estAchete)
    }
}


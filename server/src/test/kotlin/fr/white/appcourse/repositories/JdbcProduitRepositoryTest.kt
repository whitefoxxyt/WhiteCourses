package fr.white.appcourse.repositories

import fr.white.appcourse.models.ListeProduit
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JdbcProduitRepositoryTest {

    private lateinit var connection: Connection
    private lateinit var repository: ProduitRepository
    private val testDbConfig = DatabaseConfig(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL",
        user = "sa",
        password = ""
    )

    @Before
    fun setup() {
        connection = DriverManager.getConnection(testDbConfig.url, testDbConfig.user, testDbConfig.password)
        repository = JdbcProduitRepository(testDbConfig)
        
        // Drop tables if they exist (clean slate)
        connection.createStatement().use { stmt ->
            stmt.execute("DROP TABLE IF EXISTS Listes_Produits")
            stmt.execute("DROP TABLE IF EXISTS Configuration_Rayons")
            stmt.execute("DROP TABLE IF EXISTS Listes")
            stmt.execute("DROP TABLE IF EXISTS Produits")
            stmt.execute("DROP TABLE IF EXISTS Magasins")
            stmt.execute("DROP TABLE IF EXISTS Categories")
        }
        
        // Create schema
        connection.createStatement().use { stmt ->
            stmt.execute("""
                CREATE TABLE Categories (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    nom VARCHAR(100) NOT NULL UNIQUE
                )
            """)
            stmt.execute("""
                CREATE TABLE Produits (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    nom VARCHAR(255) NOT NULL,
                    categorie_id INT NOT NULL,
                    CONSTRAINT fk_produit_categorie FOREIGN KEY (categorie_id) REFERENCES Categories(id)
                )
            """)
            stmt.execute("""
                CREATE TABLE Magasins (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    nom VARCHAR(255) NOT NULL,
                    enseigne VARCHAR(100),
                    adresse TEXT
                )
            """)
            stmt.execute("""
                CREATE TABLE Listes (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    nom VARCHAR(255) NOT NULL,
                    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """)
            stmt.execute("""
                CREATE TABLE Configuration_Rayons (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    magasin_id INT NOT NULL,
                    categorie_id INT NOT NULL,
                    position INT NOT NULL,
                    CONSTRAINT fk_cfg_magasin FOREIGN KEY (magasin_id) REFERENCES Magasins(id),
                    CONSTRAINT fk_cfg_categorie FOREIGN KEY (categorie_id) REFERENCES Categories(id),
                    CONSTRAINT uq_cfg_magasin_categorie UNIQUE (magasin_id, categorie_id)
                )
            """)
            stmt.execute("""
                CREATE TABLE Listes_Produits (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    liste_id INT NOT NULL,
                    produit_id INT NOT NULL,
                    quantite INT DEFAULT 1,
                    est_achete BOOLEAN DEFAULT FALSE,
                    CONSTRAINT fk_lp_liste FOREIGN KEY (liste_id) REFERENCES Listes(id),
                    CONSTRAINT fk_lp_produit FOREIGN KEY (produit_id) REFERENCES Produits(id)
                )
            """)
        }
        
        // Insert test data in correct order
        connection.createStatement().use { stmt ->
            stmt.execute("INSERT INTO Categories (id, nom) VALUES (1, 'Fruits')")
            stmt.execute("INSERT INTO Categories (id, nom) VALUES (2, 'Légumes')")
            stmt.execute("INSERT INTO Produits (id, nom, categorie_id) VALUES (1, 'Pomme', 1)")
            stmt.execute("INSERT INTO Produits (id, nom, categorie_id) VALUES (2, 'Carotte', 2)")
            stmt.execute("INSERT INTO Magasins (id, nom) VALUES (1, 'Super U')")
            stmt.execute("INSERT INTO Listes (id, nom) VALUES (1, 'Ma liste de courses')")
            stmt.execute("INSERT INTO Listes_Produits (id, liste_id, produit_id, quantite, est_achete) VALUES (1, 1, 1, 2, false)")
            stmt.execute("INSERT INTO Listes_Produits (id, liste_id, produit_id, quantite, est_achete) VALUES (2, 1, 2, 1, false)")
            stmt.execute("INSERT INTO Configuration_Rayons (magasin_id, categorie_id, position) VALUES (1, 1, 1)")
            stmt.execute("INSERT INTO Configuration_Rayons (magasin_id, categorie_id, position) VALUES (1, 2, 2)")
        }
    }

    @After
    fun teardown() {
        connection.close()
    }

    @Test
    fun `listeExists returns true when liste exists`() {
        assertTrue(repository.listeExists(1))
    }

    @Test
    fun `listeExists returns false when liste does not exist`() {
        assertFalse(repository.listeExists(999))
    }

    @Test
    fun `findProduitsByListe returns products for existing liste`() {
        val result = repository.findProduitsByListe(1, null)
        
        assertEquals(2, result.size)
        // Products are ordered by name when no magasin is specified
        val carotte = result.find { it.nom == "Carotte" }
        val pomme = result.find { it.nom == "Pomme" }
        
        assertNotNull(carotte)
        assertNotNull(pomme)
        
        assertEquals("Carotte", carotte?.nom)
        assertEquals(1, carotte?.quantite)
        assertEquals("Légumes", carotte?.categorieNom)
        assertFalse(carotte?.estAchete ?: true)
        
        assertEquals("Pomme", pomme?.nom)
        assertEquals(2, pomme?.quantite)
        assertEquals("Fruits", pomme?.categorieNom)
        assertFalse(pomme?.estAchete ?: true)
    }

    @Test
    fun `findProduitsByListe orders by magasin position when magasinId provided`() {
        val result = repository.findProduitsByListe(1, 1)
        
        assertEquals(2, result.size)
        assertEquals("Pomme", result[0].nom)
        assertEquals(1, result[0].positionEnRayon)
        assertEquals("Carotte", result[1].nom)
        assertEquals(2, result[1].positionEnRayon)
    }

    @Test
    fun `setItemAchete updates item state`() {
        val result = repository.setItemAchete(1, true)
        
        assertEquals(UpdateItemEtatResult.Updated, result)
        
        val items = repository.findProduitsByListe(1, null)
        val pomme = items.find { it.nom == "Pomme" }
        assertTrue(pomme?.estAchete ?: false)
    }

    @Test
    fun `setItemAchete returns NotFound for non-existing item`() {
        val result = repository.setItemAchete(999, true)
        
        assertEquals(UpdateItemEtatResult.NotFound, result)
    }
}

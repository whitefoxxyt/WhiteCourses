package fr.white.appcourse.repositories

import fr.white.appcourse.models.ListeProduit
import java.sql.Connection
import java.sql.DriverManager

interface ProduitRepository {
    fun listeExists(listId: Int): Boolean
    fun findProduitsByListe(listId: Int, magasinId: Int?): List<ListeProduit>
}

data class DatabaseConfig(
    val url: String,
    val user: String,
    val password: String
) {
    companion object {
        fun fromEnvironment(): DatabaseConfig {
            val host = System.getenv("DB_HOST") ?: "127.0.0.1"
            val port = System.getenv("DB_PORT") ?: "3306"
            val database = System.getenv("DB_NAME") ?: "MaListe"
            val user = System.getenv("DB_USER") ?: "root"
            val password = System.getenv("DB_PASSWORD") ?: ""
            return DatabaseConfig(
                url = "jdbc:mysql://$host:$port/$database?useSSL=false&serverTimezone=UTC",
                user = user,
                password = password
            )
        }
    }
}

class JdbcProduitRepository(
    private val dbConfig: DatabaseConfig
) : ProduitRepository {

    override fun listeExists(listId: Int): Boolean {
        connection().use { conn ->
            conn.prepareStatement("SELECT 1 FROM Listes WHERE id = ? LIMIT 1").use { stmt ->
                stmt.setInt(1, listId)
                stmt.executeQuery().use { rs ->
                    return rs.next()
                }
            }
        }
    }

    override fun findProduitsByListe(listId: Int, magasinId: Int?): List<ListeProduit> {
        val sql = """
            SELECT lp.id, p.nom, lp.quantite, c.nom AS categorie_nom, cr.position, lp.est_achete
            FROM Listes_Produits lp
            INNER JOIN Produits p ON p.id = lp.produit_id
            INNER JOIN Categories c ON c.id = p.categorie_id
            LEFT JOIN Configuration_Rayons cr
                ON cr.categorie_id = p.categorie_id
               AND cr.magasin_id = ?
            WHERE lp.liste_id = ?
            ORDER BY cr.position IS NULL, cr.position, p.nom, lp.id
        """.trimIndent()

        connection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                if (magasinId == null) {
                    stmt.setNull(1, java.sql.Types.INTEGER)
                } else {
                    stmt.setInt(1, magasinId)
                }
                stmt.setInt(2, listId)

                stmt.executeQuery().use { rs ->
                    val items = mutableListOf<ListeProduit>()
                    while (rs.next()) {
                        val position = (rs.getObject("position") as? Number)?.toInt()
                        items += ListeProduit(
                            id = rs.getInt("id"),
                            nom = rs.getString("nom"),
                            quantite = rs.getInt("quantite"),
                            categorieNom = rs.getString("categorie_nom"),
                            positionEnRayon = position,
                            estAchete = rs.getBoolean("est_achete")
                        )
                    }
                    return items
                }
            }
        }
    }

    private fun connection(): Connection {
        return DriverManager.getConnection(dbConfig.url, dbConfig.user, dbConfig.password)
    }
}
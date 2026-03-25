package fr.white.appcourse

const val SERVER_PORT = 8080

object ApiConstants {
    const val BASE_URL = "http://10.0.2.2:$SERVER_PORT"
    const val API_BASE_PATH = "/api"
    const val ENDPOINT_LISTES = "$API_BASE_PATH/listes"
    const val PARAM_MAGASIN_ID = "magasinId"
    const val PARAM_ACHETE = "achete"
}
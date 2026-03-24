package fr.white.appcourse

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
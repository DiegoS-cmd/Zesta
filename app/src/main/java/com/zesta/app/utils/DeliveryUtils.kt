package com.zesta.app.utils

import kotlin.math.*

private const val PREP_MINUTES = 12
private const val SPEED_KMH = 25.0 // velocidad media repartidor en ciudad

fun calcularTiempoEntregaMinutos(
    restaurantLat: Double,
    restaurantLon: Double,
    userLat: Double,
    userLon: Double
): Int {
    val distanciaKm = haversineKm(restaurantLat, restaurantLon, userLat, userLon)
    // Multiplicamos x1.3 para simular que no va en línea recta (calles)
    val distanciaReal = distanciaKm * 1.3
    val minutosViaje = (distanciaReal / SPEED_KMH * 60).toInt()
    return PREP_MINUTES + minutosViaje.coerceAtLeast(3) // mínimo 3 min de viaje
}

fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return r * 2 * asin(sqrt(a))
}

// Geocodificación simple de direcciones de Madrid a coordenadas aproximadas
// Busca números y nombres de calles conocidos para estimar posición
fun geocodificarDireccionMadrid(direccion: String): Pair<Double, Double> {
    val dir = direccion.lowercase()
    return when {
        "gran vía" in dir -> 40.4200 to -3.7056
        "sol" in dir || "puerta del sol" in dir -> 40.4168 to -3.7038
        "retiro" in dir -> 40.4153 to -3.6844
        "salamanca" in dir -> 40.4230 to -3.6820
        "malasaña" in dir || "fuencarral" in dir -> 40.4267 to -3.7027
        "lavapiés" in dir || "lavapies" in dir -> 40.4080 to -3.7032
        "chueca" in dir -> 40.4233 to -3.6973
        "chamberí" in dir || "chamberi" in dir -> 40.4340 to -3.7020
        "atocha" in dir -> 40.4063 to -3.6918
        "vallecas" in dir -> 40.3853 to -3.6560
        "carabanchel" in dir -> 40.3788 to -3.7320
        "hortaleza" in dir -> 40.4750 to -3.6480
        "moncloa" in dir -> 40.4340 to -3.7193
        "arganzuela" in dir -> 40.3953 to -3.7032
        "tetuán" in dir || "tetuan" in dir -> 40.4600 to -3.7012
        "moratalaz" in dir -> 40.4050 to -3.6400
        "latina" in dir -> 40.4063 to -3.7270
        "barajas" in dir -> 40.4730 to -3.5800
        "pozuelo" in dir -> 40.4350 to -3.8140
        "alcobendas" in dir -> 40.5340 to -3.6430
        else -> 40.4168 to -3.7038 // centro de Madrid por defecto
    }
}
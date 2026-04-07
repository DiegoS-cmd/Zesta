package com.zesta.app.data.model

data class User(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val direcciones: List<String> = emptyList(),
    val rol: String = "cliente"
)

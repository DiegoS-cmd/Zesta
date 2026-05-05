package com.zesta.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class User(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val direcciones: List<String> = emptyList(),
    val favoritos: List<Int> = emptyList(),
    val rol: String = "cliente",
    val profilePhotoUrl: String? = null,
    @get:PropertyName("isDisabled")
    @set:PropertyName("isDisabled")
    var isDisabled: Boolean = false,
    val disabledAt: Timestamp? = null
)

package com.zesta.app.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ContactRepository {
    // Accedemos a la colección "solicitudes_empresa" y añadimos un documento nuevo
    private val db = FirebaseFirestore.getInstance()

    // Esta función envía los datos del formulario a Firebase
    // es suspend porque funciona con internet y tarda en ejecutarse
    suspend fun enviarSolicitudEmpresa(
        nombre: String,
        correo: String,
        mensaje: String
    ): Result<Unit> = try {
        db.collection("solicitudes_empresa")
            .add(
                mapOf(
                    "nombre" to nombre,
                    "correo" to correo,
                    "mensaje" to mensaje,
                    "timestamp" to Timestamp.now()
                )
            ).await() // Esperamos a que Firebase confirme que se guardó
        Result.success(Unit) // todo bien
    } catch (e: Exception) {
        Result.failure(e) // error
    }
}
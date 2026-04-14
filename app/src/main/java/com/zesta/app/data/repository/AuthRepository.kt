package com.zesta.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.zesta.app.data.model.User
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun register(
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        direccion: String
    ): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("No se pudo obtener el identificador del usuario"))

            val user = User(
                uid = uid,
                nombre = nombre,
                email = email,
                telefono = telefono,
                direccion = direccion,
                rol = "cliente"
            )
            db.collection("users").document(uid).set(user).await()
            Result.success(user)
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(mapFirebaseRegisterError(e.errorCode)))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo registrar el usuario"))
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No se pudo recuperar la sesión"))

            val snapshot = db.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("No se encontró el perfil del usuario"))

            Result.success(user)
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(mapFirebaseLoginError(e.errorCode)))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo iniciar sesión"))
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("No se pudo obtener el usuario"))

            val docRef = db.collection("users").document(uid)
            val snapshot = docRef.get().await()

            val user = if (!snapshot.exists()) {
                val newUser = User(
                    uid = uid,
                    nombre = authResult.user?.displayName.orEmpty(),
                    email = authResult.user?.email.orEmpty(),
                    rol = "cliente"
                )
                docRef.set(newUser).await()
                newUser
            } else {
                snapshot.toObject(User::class.java)
                    ?: return Result.failure(Exception("Error al cargar perfil"))
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo iniciar sesión con Google"))
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            val snapshot = db.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("No se encontró el perfil del usuario"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo recuperar el usuario actual"))
        }
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun logout() = auth.signOut()



    suspend fun updateProfile(telefono: String, direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesion iniciada"))

            val doc = db.collection("users").document(uid).get().await()
            val listaActual = doc.get("direcciones") as? List<String> ?: emptyList()
            val nuevaLista = if (direccion.isNotBlank() && !listaActual.contains(direccion)) {
                (listaActual + direccion).takeLast(3)
            } else listaActual

            db.collection("users").document(uid)
                .update(mapOf("telefono" to telefono, "direccion" to direccion, "direcciones" to nuevaLista))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudieron guardar los datos"))
        }
    }

    suspend fun clearProfileField(field: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesion iniciada"))

            if (field == "direccion") {
                val doc = db.collection("users").document(uid).get().await()
                val listaActual = doc.get("direcciones") as? List<String> ?: emptyList()
                val direccionActual = doc.getString("direccion") ?: ""
                val nuevaLista = listaActual.filter { it != direccionActual }
                val nuevaActiva = nuevaLista.firstOrNull() ?: ""
                db.collection("users").document(uid)
                    .update(mapOf("direccion" to nuevaActiva, "direcciones" to nuevaLista))
                    .await()
            } else {
                db.collection("users").document(uid).update(field, "").await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo eliminar el dato"))
        }
    }

    suspend fun addDireccion(direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesion iniciada"))

            val doc = db.collection("users").document(uid).get().await()
            val current = doc.get("direcciones") as? List<String> ?: emptyList()

            if (current.size >= 3) return Result.failure(Exception("Maximo 3 direcciones"))

            db.collection("users").document(uid)
                .update(mapOf("direcciones" to current + direccion, "direccion" to direccion))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setDireccionActiva(direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesion iniciada"))

            db.collection("users").document(uid).update("direccion", direccion).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDireccion(direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesion iniciada"))

            val doc = db.collection("users").document(uid).get().await()
            val current = doc.get("direcciones") as? List<String> ?: emptyList()
            val updated = current.filter { it != direccion }
            val currentActiva = doc.getString("direccion") ?: ""
            val nuevaActiva = if (currentActiva == direccion) updated.firstOrNull() ?: "" else currentActiva

            db.collection("users").document(uid)
                .update(mapOf("direcciones" to updated, "direccion" to nuevaActiva))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFavorito(restaurantId: Int): Result<List<Int>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesion iniciada"))

            val doc = db.collection("users").document(uid).get().await()
            val current = (doc.get("favoritos") as? List<Long>)?.map { it.toInt() } ?: emptyList()
            val updated = if (current.contains(restaurantId))
                current.filter { it != restaurantId }
            else
                current + restaurantId

            db.collection("users").document(uid).update("favoritos", updated).await()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo actualizar favoritos"))
        }
    }

    private fun mapFirebaseRegisterError(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "El correo no tiene un formato válido"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Ese correo ya está registrado"
            "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres"
            else -> "No se pudo registrar el usuario"
        }
    }

    private fun mapFirebaseLoginError(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "El correo no tiene un formato válido"
            "ERROR_USER_NOT_FOUND",
            "ERROR_WRONG_PASSWORD",
            "ERROR_INVALID_LOGIN_CREDENTIALS" -> "Credenciales incorrectas"
            else -> "No se pudo iniciar sesión"
        }
    }
}
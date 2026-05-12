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
    private fun usersRef() = db.collection("users")

    suspend fun register(
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        direccion: String
    ): Result<User> {
        return try {
            val uid = auth.createUserWithEmailAndPassword(email, password).await()
                .user?.uid ?: return Result.failure(Exception("No se pudo obtener el uid"))

            val user = User(
                uid = uid,
                nombre = nombre,
                email = email,
                telefono = telefono,
                direccion = direccion,
                rol = "cliente"
            )
            usersRef().document(uid).set(user).await()
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

            val user = usersRef().document(uid).get().await()
                .toObject(User::class.java)
                ?: return Result.failure(Exception("No se encontró el perfil del usuario"))

            if (user.isDisabled) {
                auth.signOut()
                return Result.failure(Exception("Esta cuenta ha sido deshabilitada"))
            }

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
            val firebaseUser = auth.signInWithCredential(credential).await().user
                ?: return Result.failure(Exception("No se pudo obtener el usuario"))

            val docRef = usersRef().document(firebaseUser.uid)
            val snapshot = docRef.get().await()

            val user = if (!snapshot.exists()) {
                // Primera vez con Google: creamos el documento
                val nuevo = User(
                    uid = firebaseUser.uid,
                    nombre = firebaseUser.displayName.orEmpty(),
                    email = firebaseUser.email.orEmpty(),
                    rol = "cliente"
                )
                docRef.set(nuevo).await()
                nuevo
            } else {
                snapshot.toObject(User::class.java)
                    ?: return Result.failure(Exception("Error al cargar perfil"))
            }

            if (user.isDisabled) {
                auth.signOut()
                return Result.failure(Exception("Esta cuenta ha sido deshabilitada"))
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

            val user = usersRef().document(uid).get().await()
                .toObject(User::class.java)
                ?: return Result.failure(Exception("No se encontró el perfil"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo recuperar el usuario"))
        }
    }

    fun isLoggedIn() = auth.currentUser != null

    fun logout() = auth.signOut()

    // Si la dirección es nueva se añade al historial (máximo 3)
    suspend fun updateProfile(telefono: String, direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            val doc = usersRef().document(uid).get().await()
            val dirs = doc.get("direcciones") as? List<String> ?: emptyList()
            val dirsActualizado = if (direccion.isNotBlank() && !dirs.contains(direccion)) {
                (dirs + direccion).takeLast(3)
            } else dirs

            usersRef().document(uid).update(mapOf(
                "telefono" to telefono,
                "direccion" to direccion,
                "direcciones" to dirsActualizado
            )).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudieron guardar los datos"))
        }
    }

    // Al borrar la dirección activa se promueve la primera del historial, o queda vacía
    suspend fun clearProfileField(field: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            if (field == "direccion") {
                val doc = usersRef().document(uid).get().await()
                val dirs = doc.get("direcciones") as? List<String> ?: emptyList()
                val activa = doc.getString("direccion") ?: ""
                val nuevaLista = dirs.filter { it != activa }
                usersRef().document(uid).update(mapOf(
                    "direccion" to (nuevaLista.firstOrNull() ?: ""),
                    "direcciones" to nuevaLista
                )).await()
            } else {
                usersRef().document(uid).update(field, "").await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo eliminar el dato"))
        }
    }
    // Añade una dirección nueva y la pone como activa, máximo 3 en total
    suspend fun addDireccion(direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            val doc = usersRef().document(uid).get().await()
            val dirs = doc.get("direcciones") as? List<String> ?: emptyList()

            if (dirs.size >= 3) return Result.failure(Exception("Máximo 3 direcciones"))

            usersRef().document(uid).update(mapOf(
                "direcciones" to dirs + direccion,
                "direccion" to direccion
            )).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setDireccionActiva(direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))
            usersRef().document(uid).update("direccion", direccion).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Si se elimina la dirección activa se promueve la primera restante
    suspend fun deleteDireccion(direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            val doc = usersRef().document(uid).get().await()
            val dirs = doc.get("direcciones") as? List<String> ?: emptyList()
            val restantes = dirs.filter { it != direccion }
            val activa = doc.getString("direccion") ?: ""

            usersRef().document(uid).update(mapOf(
                "direcciones" to restantes,
                "direccion" to if (activa == direccion) restantes.firstOrNull() ?: "" else activa
            )).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Añade o quita el restaurante de favoritos según si ya estaba o no
    suspend fun toggleFavorito(restaurantId: Int): Result<List<Int>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            val doc = usersRef().document(uid).get().await()
            // Firestore guarda los Int como Long
            val favs = (doc.get("favoritos") as? List<Long>)?.map { it.toInt() } ?: emptyList()
            val updated = if (favs.contains(restaurantId))
                favs.filter { it != restaurantId }
            else
                favs + restaurantId

            usersRef().document(uid).update("favoritos", updated).await()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo actualizar favoritos"))
        }
    }

    suspend fun checkAccountStillActive(): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            // reload() revalida y lanza excepción si la cuenta fue eliminada
            user.reload().await()

            val disabled = usersRef().document(user.uid).get().await()
                .getBoolean("isDisabled") ?: false

            if (disabled) {
                auth.signOut()
                return Result.failure(Exception("Esta cuenta ha sido deshabilitada"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            auth.signOut()
            Result.failure(Exception("Esta cuenta ha sido deshabilitada"))
        }
    }
    // Reactiva una cuenta deshabilitada: hace login, quita el flag y cierra sesión
    suspend fun reactivateAccount(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No se pudo identificar la cuenta"))

            usersRef().document(uid).update(mapOf(
                "isDisabled" to false,
                "disabledAt" to null
            )).await()

            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo reactivar la cuenta. Comprueba tus credenciales."))
        }
    }

    // siempre devuelve éxito
    suspend fun enviarResetEmail(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email.trim()).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("No se encontró ninguna cuenta con ese correo"))
    }

    suspend fun cambiarContrasena(
        email: String,
        currentPassword: String,
        newPassword: String
    ): Result<Unit> = try {
        // Verificamos credenciales haciendo login temporal
        val result = auth.signInWithEmailAndPassword(email, currentPassword).await()
        val user = result.user ?: return Result.failure(Exception("No se pudo autenticar"))
        user.updatePassword(newPassword).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(Exception("Contraseña actual incorrecta"))
    }
    // Traduce los códigos de error de Firebase a mensajes legibles para el usuario
    private fun mapFirebaseRegisterError(errorCode: String) = when (errorCode) {
        "ERROR_INVALID_EMAIL" -> "El correo no tiene un formato válido"
        "ERROR_EMAIL_ALREADY_IN_USE" -> "Ese correo ya está registrado"
        "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres"
        else -> "No se pudo registrar el usuario"
    }

    private fun mapFirebaseLoginError(errorCode: String) = when (errorCode) {
        "ERROR_INVALID_EMAIL" -> "El correo no tiene un formato válido"
        "ERROR_USER_NOT_FOUND",
        "ERROR_WRONG_PASSWORD",
        "ERROR_INVALID_LOGIN_CREDENTIALS" -> "Credenciales incorrectas"
        else -> "No se pudo iniciar sesión"
    }
}
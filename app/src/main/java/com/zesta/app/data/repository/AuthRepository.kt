package com.zesta.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.zesta.app.data.model.User
import kotlinx.coroutines.tasks.await

/**
 * Repositorio de autenticación y gestión de perfil de usuario.
 *
 * Centraliza todas las operaciones contra Firebase Auth y la colección
 * "users" de Firestore: registro, login, perfil, direcciones y favoritos.
 *
 * @param auth Instancia de FirebaseAuth (inyectable para tests).
 * @param db   Instancia de FirebaseFirestore (inyectable para tests).
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Registra un nuevo usuario en Firebase Auth y crea su documento en Firestore.
     * El rol se fija siempre como "cliente" en el momento del registro.
     *
     * @return [User] recién creado, o error si el email ya existe o la contraseña es débil.
     */
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

    /**
     * Inicia sesión con email y contraseña.
     *
     * Tras autenticar con Firebase Auth, comprueba en Firestore si la cuenta
     * está deshabilitada (isDisabled = true). Si lo está, cierra la sesión
     * inmediatamente y devuelve un error para que el usuario no pueda entrar.
     *
     * @return [User] autenticado, o error si las credenciales son incorrectas
     *         o la cuenta está deshabilitada.
     */
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No se pudo recuperar la sesión"))

            val snapshot = db.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("No se encontró el perfil del usuario"))

            android.util.Log.d("ZESTA_DISABLE", "login - isDisabled: ${user.isDisabled}, uid: $uid")

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

    /**
     * Inicia sesión con Google usando el token de ID del cliente.
     *
     * Si el usuario no tiene documento en Firestore (primer login con Google),
     * se crea automáticamente con los datos de su cuenta de Google.
     * Al igual que en el login normal, se bloquea el acceso si la cuenta
     * está deshabilitada.
     *
     * @return [User] autenticado o recién creado, o error si falla el proceso.
     */
    suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("No se pudo obtener el usuario"))

            val docRef = db.collection("users").document(uid)
            val snapshot = docRef.get().await()

            val user = if (!snapshot.exists()) {
                // Primer login con Google: crear documento en Firestore
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

            // Bloquear acceso si la cuenta fue deshabilitada
            if (user.isDisabled) {
                auth.signOut()
                return Result.failure(Exception("Esta cuenta ha sido deshabilitada"))
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo iniciar sesión con Google"))
        }
    }

    /**
     * Obtiene los datos actualizados del usuario autenticado desde Firestore.
     * Se llama cada vez que se necesita refrescar el perfil en memoria.
     *
     * @return [User] actualizado, o error si no hay sesión activa.
     */
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

    /** Devuelve true si Firebase Auth tiene un token de sesión activo. */
    fun isLoggedIn(): Boolean = auth.currentUser != null

    /** Cierra la sesión de Firebase Auth localmente. */
    fun logout() = auth.signOut()

    /**
     * Actualiza el teléfono y la dirección activa del usuario en Firestore.
     * Si la dirección es nueva, la añade al historial (máximo 3).
     */
    suspend fun updateProfile(telefono: String, direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            val doc = db.collection("users").document(uid).get().await()
            val listaActual = doc.get("direcciones") as? List<String> ?: emptyList()
            val nuevaLista = if (direccion.isNotBlank() && !listaActual.contains(direccion)) {
                (listaActual + direccion).takeLast(3)
            } else listaActual

            db.collection("users").document(uid)
                .update(mapOf(
                    "telefono" to telefono,
                    "direccion" to direccion,
                    "direcciones" to nuevaLista
                ))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudieron guardar los datos"))
        }
    }

    /**
     * Borra el valor de un campo concreto del perfil ("telefono" o "direccion").
     * Si se borra la dirección activa, se promueve automáticamente la primera
     * del historial como nueva dirección activa, o se deja vacía si no hay más.
     */
    suspend fun clearProfileField(field: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

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

    /**
     * Añade una nueva dirección al historial del usuario.
     * Máximo 3 direcciones; devuelve error si ya se alcanzó el límite.
     * La nueva dirección pasa a ser la dirección activa.
     */
    suspend fun addDireccion(direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            val doc = db.collection("users").document(uid).get().await()
            val current = doc.get("direcciones") as? List<String> ?: emptyList()

            if (current.size >= 3) return Result.failure(Exception("Máximo 3 direcciones"))

            db.collection("users").document(uid)
                .update(mapOf("direcciones" to current + direccion, "direccion" to direccion))
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cambia la dirección activa del usuario a una de las que ya tiene en su historial.
     */
    suspend fun setDireccionActiva(direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            db.collection("users").document(uid).update("direccion", direccion).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una dirección del historial del usuario.
     * Si era la dirección activa, promueve la primera restante o deja el campo vacío.
     */
    suspend fun deleteDireccion(direccion: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

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

    /**
     * Alterna el estado favorito de un restaurante para el usuario.
     * Si ya estaba en favoritos lo elimina; si no estaba lo añade.
     * Firestore almacena los IDs como Long, por eso se convierten a Int al leer.
     *
     * @return Lista actualizada de IDs favoritos.
     */
    suspend fun toggleFavorito(restaurantId: Int): Result<List<Int>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            val doc = db.collection("users").document(uid).get().await()
            // Firestore serializa los Int como Long, hay que convertirlos al leer
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

    /** Traduce los códigos de error de Firebase Auth al registrarse a mensajes legibles. */
    private fun mapFirebaseRegisterError(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "El correo no tiene un formato válido"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Ese correo ya está registrado"
            "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres"
            else -> "No se pudo registrar el usuario"
        }
    }

    /** Traduce los códigos de error de Firebase Auth al iniciar sesión a mensajes legibles. */
    private fun mapFirebaseLoginError(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "El correo no tiene un formato válido"
            "ERROR_USER_NOT_FOUND",
            "ERROR_WRONG_PASSWORD",
            "ERROR_INVALID_LOGIN_CREDENTIALS" -> "Credenciales incorrectas"
            else -> "No se pudo iniciar sesión"
        }
    }

    /**
     * Fuerza la recarga del token de Firebase Auth y comprueba si la cuenta
     * está deshabilitada en Firestore. Llámalo al volver a primer plano o
     * al arrancar la app con sesión ya iniciada.
     *
     * Usando forceRefresh = true se invalida la caché del token, por lo que
     * Firebase devuelve error inmediatamente si la cuenta fue deshabilitada.
     */
    suspend fun checkAccountStillActive(): Result<Unit> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            // reload() fuerza revalidación del estado de la cuenta contra Firebase Auth
            // Si la cuenta está deshabilitada en Firebase Console lanza excepción aquí
            user.reload().await()

            // Comprobamos también nuestro campo isDisabled en Firestore
            val snapshot = db.collection("users").document(user.uid).get().await()
            val isDisabled = snapshot.getBoolean("isDisabled") ?: false
            if (isDisabled) {
                auth.signOut()
                return Result.failure(Exception("Esta cuenta ha sido deshabilitada"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            auth.signOut()
            Result.failure(Exception("Esta cuenta ha sido deshabilitada"))
        }
    }
    suspend fun reactivateAccount(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No se pudo identificar la cuenta"))

            db.collection("users").document(uid)
                .update(mapOf(
                    "isDisabled" to false,
                    "disabledAt" to null
                ))
                .await()

            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo reactivar la cuenta. Comprueba tus credenciales."))
        }
    }
}

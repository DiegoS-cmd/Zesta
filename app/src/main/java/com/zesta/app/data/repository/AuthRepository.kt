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
            // Primero se crea la cuenta en Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("No se pudo obtener el identificador del usuario"))

            // Después se guarda el perfil del usuario en Firestore
            val user = User(
                uid = uid,
                nombre = nombre,
                email = email,
                telefono = telefono,
                direccion = direccion,
                rol = "cliente"
            )

            db.collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(user)
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(mapFirebaseRegisterError(e.errorCode)))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo registrar el usuario"))
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Se inicia sesión en Firebase Auth
            auth.signInWithEmailAndPassword(email, password).await()

            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No se pudo recuperar la sesión"))

            // Con el uid cargamos el perfil real desde Firestore
            val snapshot = db.collection("users")
                .document(uid)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("No se encontró el perfil del usuario"))

            Result.success(user)
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(mapFirebaseLoginError(e.errorCode)))
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo iniciar sesión"))
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("No hay sesión iniciada"))

            val snapshot = db.collection("users")
                .document(uid)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("No se encontró el perfil del usuario"))

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo recuperar el usuario actual"))
        }
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logout() {
        auth.signOut()
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

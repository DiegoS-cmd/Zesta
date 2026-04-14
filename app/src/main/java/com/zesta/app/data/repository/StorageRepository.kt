package com.zesta.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class StorageRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun uploadProfilePhoto(context: Context, uid: String, uri: Uri): Result<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("No se pudo abrir la imagen"))

            val original = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (original == null) return Result.failure(Exception("No se pudo decodificar la imagen"))

            val scaled = Bitmap.createScaledBitmap(original, 300, 300, true)
            val baos = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)

            db.collection("users").document(uid)
                .update("profilePhotoUrl", base64)
                .await()

            Result.success(base64)
        } catch (e: Exception) {
            Result.failure(Exception("Error: ${e.message}"))
        }
    }

    suspend fun uploadProfilePhotoFromBase64(uid: String, base64: String): Result<String> {
        return try {
            db.collection("users").document(uid)
                .update("profilePhotoUrl", base64)
                .await()
            Result.success(base64)
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo guardar la foto: ${e.message}"))
        }
    }

    suspend fun deleteProfilePhoto(uid: String): Result<Unit> {
        return try {
            db.collection("users").document(uid)
                .update("profilePhotoUrl", null)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }
}

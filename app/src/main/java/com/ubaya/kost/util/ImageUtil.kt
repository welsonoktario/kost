package com.ubaya.kost.util

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64
import android.util.Base64OutputStream
import java.io.ByteArrayOutputStream
import java.io.File

class ImageUtil {
    fun fileToBase64(file: File): String {
        return ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.NO_WRAP).use { base64FilterStream ->
                file.inputStream().use { inputStream ->
                    inputStream.copyTo(base64FilterStream)
                }
            }
            return@use outputStream.toString()
        }
    }

    fun contentUriToBase64(contentResolver: ContentResolver, uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)

        return ByteArrayOutputStream().use { outputStream ->
            Base64OutputStream(outputStream, Base64.NO_WRAP).use { base64FilterStream ->
                inputStream.use { inputStream ->
                    inputStream?.copyTo(base64FilterStream)
                }
            }
            return@use outputStream.toString()
        }
    }
}
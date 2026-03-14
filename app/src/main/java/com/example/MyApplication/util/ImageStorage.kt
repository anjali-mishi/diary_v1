package com.example.myapplication.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorage {

    /**
     * Copies a content:// URI into the app's private photos directory.
     * Returns the absolute path of the copied file, or null on failure.
     * If the URI is already a file path (not content://), returns it unchanged.
     */
    fun copyToInternalStorage(context: Context, uriString: String): String? {
        if (!uriString.startsWith("content://")) return uriString // already a file path

        return try {
            val uri = Uri.parse(uriString)
            val photoDir = File(context.filesDir, "photos").also { it.mkdirs() }
            val destFile = File(photoDir, "${UUID.randomUUID()}.jpg")

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

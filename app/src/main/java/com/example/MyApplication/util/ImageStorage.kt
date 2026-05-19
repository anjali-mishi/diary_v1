package com.example.myapplication.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

private const val TAG = "Diary.ImageStorage"

object ImageStorage {

    /**
     * Copies a content:// URI into the app's private photos directory.
     * Returns the absolute path of the copied file, or null on failure.
     * If the URI is already a file path (not content://), returns it unchanged.
     */
    fun copyToInternalStorage(context: Context, uriString: String): String? {
        if (!uriString.startsWith("content://")) {
            Log.d(TAG, "copyToInternalStorage: already a file path, skipping copy")
            return uriString
        }

        Log.d(TAG, "copyToInternalStorage: copying uri=$uriString")
        return try {
            val uri = Uri.parse(uriString)
            val photoDir = File(context.filesDir, "photos").also { it.mkdirs() }
            val destFile = File(photoDir, "${UUID.randomUUID()}.jpg")

            val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }

            var sampleSize = 1
            val maxEdge = maxOf(opts.outWidth, opts.outHeight)
            while (maxEdge / sampleSize > 2048) sampleSize *= 2

            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val bitmap = context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, decodeOpts)
            }

            if (bitmap != null) {
                FileOutputStream(destFile).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
                }
                bitmap.recycle()
            } else {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destFile).use { output -> input.copyTo(output) }
                }
            }
            Log.i(TAG, "copyToInternalStorage: success → ${destFile.absolutePath}")
            destFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "copyToInternalStorage: failed — ${e.message}", e)
            null
        }
    }
}

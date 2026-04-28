package com.example.myapplication.util

import android.media.MediaMetadataRetriever

/**
 * Returns a formatted duration string ("M:SS") for the audio file at [path],
 * or null if the path is null / the file cannot be read.
 */
fun audioFileDuration(path: String?): String? {
    path ?: return null
    return try {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(path)
        val ms = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toLongOrNull() ?: 0L
        mmr.release()
        val totalSec = (ms / 1000).toInt()
        "%d:%02d".format(totalSec / 60, totalSec % 60)
    } catch (e: Exception) {
        null
    }
}

package com.example.myapplication.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val textContent: String? = null,
    val audioFilePath: String? = null,
    val photoFilePath: String? = null,
    val emotionalTone: String? = null,
    val isHidden: Boolean = false,
    val lastViewedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val waveformData: String? = null,

    // ── v3 columns ────────────────────────────────────────────────────────────
    /** 0.0–1.0 confidence score from the sentiment model; null until analysed. */
    @ColumnInfo(defaultValue = "NULL")
    val emotionIntensity: Float? = null,

    /** Second-highest emotion label from sentiment model. */
    @ColumnInfo(defaultValue = "NULL")
    val secondaryEmotionalTone: String? = null,

    /** True when the user has starred this memory. */
    @ColumnInfo(defaultValue = "0")
    val isBookmarked: Boolean = false,

    /** Epoch-ms when bookmarked; null if never bookmarked. */
    @ColumnInfo(defaultValue = "NULL")
    val bookmarkedAt: Long? = null,

    /** JSON-encoded List<String> of sticker emoji codes; null if none. */
    @ColumnInfo(defaultValue = "NULL")
    val stickers: String? = null,

    /** "MEMORY" (default) or "LETTER". */
    @ColumnInfo(defaultValue = "MEMORY")
    val entryType: String = "MEMORY",

    /** Epoch-ms until which a LETTER is sealed; null for normal memories. */
    @ColumnInfo(defaultValue = "NULL")
    val sealedUntil: Long? = null,

    /** True once a sealed letter has been opened for the first time. */
    @ColumnInfo(defaultValue = "0")
    val isRevealed: Boolean = false
)


package com.example.myapplication.util

import android.util.Log
import com.example.myapplication.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "Diary.SentimentAnalyzer"
private const val MODEL_URL =
    "https://api-inference.huggingface.co/models/j-hartmann/emotion-english-distilroberta-base"

data class SentimentResult(
    val tone: String,
    val intensity: Float,
    val secondaryTone: String?
)

/**
 * Analyses [text] using the HuggingFace Inference API (free tier).
 * Falls back to keyword-based [EmotionDetector] on any failure.
 *
 * Set HUGGINGFACE_API_KEY in local.properties to enable the API path.
 */
suspend fun analyzeSentiment(text: String): SentimentResult = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.HUGGINGFACE_API_KEY
    if (apiKey.isBlank()) {
        Log.w(TAG, "analyzeSentiment: API key not configured — using keyword fallback")
        return@withContext keywordFallback(text)
    }
    try {
        val connection = (URL(MODEL_URL).openConnection() as HttpURLConnection).apply {
            connectTimeout = 8_000
            readTimeout    = 15_000
            requestMethod  = "POST"
            setRequestProperty("Authorization", "Bearer $apiKey")
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
        }

        // Escape only the characters that would break the JSON string literal
        val escaped = text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
        connection.outputStream.use { it.write("""{"inputs":"$escaped"}""".toByteArray()) }

        val code = connection.responseCode
        if (code != HttpURLConnection.HTTP_OK) {
            Log.w(TAG, "analyzeSentiment: HTTP $code — using keyword fallback")
            return@withContext keywordFallback(text)
        }

        // Response shape: [[{"label":"joy","score":0.94}, {"label":"neutral","score":0.02}, ...]]
        val body   = connection.inputStream.bufferedReader().readText()
        val labels = JSONArray(body).getJSONArray(0)   // results for the single input

        val top1 = labels.getJSONObject(0)
        val top2 = if (labels.length() > 1) labels.getJSONObject(1) else null

        val tone      = mapHfLabel(top1.getString("label"))
        val intensity = top1.getDouble("score").toFloat()
        // Only record secondary if it maps to a different app tone
        val secondary = top2
            ?.let { mapHfLabel(it.getString("label")) }
            ?.takeIf { it != tone }

        Log.d(TAG, "analyzeSentiment: tone=$tone intensity=%.2f secondary=$secondary".format(intensity))
        SentimentResult(tone, intensity, secondary)
    } catch (e: Exception) {
        Log.w(TAG, "analyzeSentiment: ${e.javaClass.simpleName} — ${e.message} — using keyword fallback")
        keywordFallback(text)
    }
}

private fun mapHfLabel(label: String): String = when (label.lowercase()) {
    "joy"      -> "HAPPY"
    "sadness"  -> "SAD"
    "fear"     -> "ANXIOUS"
    "anger"    -> "ANXIOUS"
    "surprise" -> "EXCITED"
    "disgust"  -> "SAD"
    else       -> "NEUTRAL"  // "neutral"
}

private fun keywordFallback(text: String): SentimentResult =
    SentimentResult(
        tone          = EmotionDetector.detect(text),
        intensity     = 1.0f,
        secondaryTone = null
    )

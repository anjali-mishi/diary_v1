package com.example.myapplication.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.EmotionAnxious
import com.example.myapplication.ui.theme.EmotionCalm
import com.example.myapplication.ui.theme.EmotionExcited
import com.example.myapplication.ui.theme.EmotionHappy
import com.example.myapplication.ui.theme.EmotionNeutral
import com.example.myapplication.ui.theme.EmotionSad

/**
 * Overlays the paper grain texture image on any composable.
 * Gives memory cards and the capture screen an authentic diary-page feel.
 */
fun Modifier.paperTexture(alpha: Float = 0.35f): Modifier = composed {
    val painter = painterResource(R.drawable.paper_texture)
    drawWithContent {
        drawContent()
        with(painter) {
            draw(size = this@drawWithContent.size, alpha = alpha)
        }
    }
}

/**
 * Single source of truth for emotion → accent colour.
 * Used by MemoryCard, BentoMemoryCard, IndexMemoryRow, and MemoryDetailScreen.
 */
fun emotionColor(tone: String?): Color = when (tone) {
    "HAPPY"   -> EmotionHappy
    "SAD"     -> EmotionSad
    "ANXIOUS" -> EmotionAnxious
    "CALM"    -> EmotionCalm
    "EXCITED" -> EmotionExcited
    else      -> EmotionNeutral
}

internal fun emotionEmoji(tone: String?) = when (tone?.uppercase()) {
    "HAPPY"   -> "😊"
    "SAD"     -> "😢"
    "ANXIOUS" -> "😰"
    "CALM"    -> "😌"
    "EXCITED" -> "🎉"
    else      -> "😐"
}

internal fun emotionLabel(tone: String?) =
    tone?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Neutral"

@Composable
fun Modifier.appleShadow(cornerRadius: Dp = 100.dp): Modifier {
    return this.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.WHITE
                    setShadowLayer(24f, 0f, 6f, android.graphics.Color.argb(18, 0, 0, 0))
                }
            }
            canvas.drawRoundRect(0f, 0f, size.width, size.height,
                cornerRadius.toPx(), cornerRadius.toPx(), paint)
        }
    }
}

fun Modifier.cardShadow(intensity: Float, cornerRadius: Dp = 14.dp): Modifier {
    if (intensity <= 0f) return this
    return this.drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.WHITE
                    setShadowLayer(
                        24f * intensity,
                        0f,
                        6f * intensity,
                        android.graphics.Color.argb((18 * intensity).toInt(), 0, 0, 0)
                    )
                }
            }
            canvas.drawRoundRect(0f, 0f, size.width, size.height,
                cornerRadius.toPx(), cornerRadius.toPx(), paint)
        }
    }
}

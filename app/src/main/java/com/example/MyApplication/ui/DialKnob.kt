package com.example.myapplication.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlinx.coroutines.launch
import kotlin.math.*

// Rotation limits: ±150° in radians
private const val MIN_ROT = -2.618f
private const val MAX_ROT =  2.618f

/**
 * Skeuomorphic radio-dial knob rendered via Compose Canvas.
 *
 * Camera angle: ~15° inclination — side wall dominates, top is a thin ellipse.
 * Aesthetic: matte cream/ivory, dense knurling ridges on the side,
 * radial brushed-metal on the top surface, silver indicator line.
 *
 * @param value       Normalised position 0..1 (0 = full left, 1 = full right)
 * @param onValueChange Called continuously as the user drags
 */
@Composable
fun DialKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    // Animatable so we can apply momentum decay after drag ends
    val rotAnim = remember { Animatable(lerpF(MIN_ROT, MAX_ROT, value.coerceIn(0f, 1f))) }
    val decay   = remember { exponentialDecay<Float>(frictionMultiplier = 3.0f) }

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            val vt = VelocityTracker()
            detectDragGestures(
                onDragStart = {
                    scope.launch { rotAnim.stop() }
                    vt.resetTracking()
                },
                onDrag = { change, drag ->
                    change.consume()
                    vt.addPosition(change.uptimeMillis, change.position)
                    val target = (rotAnim.value + drag.x * 0.010f).coerceIn(MIN_ROT, MAX_ROT)
                    scope.launch { rotAnim.snapTo(target) }
                    onValueChange(normRot(rotAnim.value))
                },
                onDragEnd = {
                    val vx = vt.calculateVelocity().x * 0.010f
                    scope.launch {
                        rotAnim.animateDecay(vx, decay)
                        // Snap if overshot
                        val clamped = rotAnim.value.coerceIn(MIN_ROT, MAX_ROT)
                        if (rotAnim.value != clamped) rotAnim.snapTo(clamped)
                        onValueChange(normRot(rotAnim.value))
                    }
                }
            )
        }
    ) {
        drawDial(rotAnim.value)
    }
}

// ─── Private helpers ──────────────────────────────────────────────────────────

private fun lerpF(a: Float, b: Float, t: Float) = a + (b - a) * t
private fun normRot(rot: Float) = ((rot - MIN_ROT) / (MAX_ROT - MIN_ROT)).coerceIn(0f, 1f)

// ─── Canvas drawing ───────────────────────────────────────────────────────────

private fun DrawScope.drawDial(rotRad: Float) {
    val cx    = size.width / 2f
    val rx    = size.width * 0.44f     // x-radius of cylinder
    val ry    = rx * 0.115f            // y-radius of top ellipse (~15° tilt)
    val bodyH = size.height * 0.84f    // tall side wall
    val topY  = (size.height - bodyH) / 2f
    val botY  = topY + bodyH

    // ── Clip path: stadium silhouette of the cylinder ─────────────────
    // Top border = lower arc of top ellipse; bottom border = upper arc of bottom ellipse
    val bodyPath = Path().apply {
        moveTo(cx - rx, topY)
        arcTo(Rect(cx - rx, topY - ry, cx + rx, topY + ry), 180f,  180f, false)
        lineTo(cx + rx, botY)
        arcTo(Rect(cx - rx, botY - ry, cx + rx, botY + ry),   0f, -180f, false)
        close()
    }

    // ── 1. Bottom cap — dark, mostly occluded ─────────────────────────
    drawOval(
        color = Color(0xFF8C8880.toInt()),
        topLeft = Offset(cx - rx * 1.01f, botY - ry * 1.01f),
        size    = Size(rx * 2.02f, ry * 2.02f)
    )

    // ── 2. Cylinder body — horizontal gradient faking curvature ───────
    val bodyGrad = Brush.horizontalGradient(
        colorStops = arrayOf(
            0.00f to Color(0xFF6E6860.toInt()),   // extreme left edge
            0.06f to Color(0xFFA09890.toInt()),
            0.14f to Color(0xFFCCC6B8.toInt()),
            0.26f to Color(0xFFDED8C8.toInt()),
            0.40f to Color(0xFFEAE4D4.toInt()),
            0.50f to Color(0xFFF0EAD8.toInt()),   // centre: brightest
            0.60f to Color(0xFFEAE4D4.toInt()),
            0.74f to Color(0xFFDAD4C2.toInt()),
            0.86f to Color(0xFFBEB8A8.toInt()),
            0.94f to Color(0xFF9E9890.toInt()),
            1.00f to Color(0xFF6E6860.toInt()),
        ),
        startX = cx - rx,
        endX   = cx + rx
    )
    clipPath(bodyPath) {
        drawRect(brush = bodyGrad, topLeft = Offset(cx - rx, topY - ry), size = Size(rx * 2f, bodyH + ry * 2f))
    }

    // ── 3. Knurling ridges ─────────────────────────────────────────────
    // Each of the 100 ridges sits at a fixed angular position on the cylinder.
    // When the dial rotates, their projected x-positions shift, selling the spin.
    val numKnurls = 100
    clipPath(bodyPath) {
        for (k in 0 until numKnurls) {
            val theta = (k.toFloat() / numKnurls) * 2f * PI.toFloat() + rotRad
            val cosT  = cos(theta)
            if (cosT < 0.04f) continue             // back-face cull

            val xPos = cx + rx * cosT
            if (xPos < cx - rx + 1.5f || xPos > cx + rx - 1.5f) continue

            // Bright crest: cosT ≈ 1 → ridge faces camera, maximum highlight
            val hiliteA = (cosT * 0.62f + 0.10f).coerceIn(0f, 0.74f)
            drawLine(
                color       = Color(1f, 0.99f, 0.95f, hiliteA),
                start       = Offset(xPos, topY + ry * 1.2f),
                end         = Offset(xPos, botY - ry * 0.6f),
                strokeWidth = 0.9f
            )
            // Dark valley immediately right of crest
            val valleyA = (cosT * 0.20f).coerceIn(0f, 0.24f)
            drawLine(
                color       = Color(0f, 0f, 0f, valleyA),
                start       = Offset(xPos + 2.2f, topY + ry * 1.2f),
                end         = Offset(xPos + 2.2f, botY - ry * 0.6f),
                strokeWidth = 1.8f
            )
        }
    }

    // ── 4. Extra edge darkening — sides curve sharply away ────────────
    clipPath(bodyPath) {
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0f, 0f, 0f, 0.40f), Color(0f, 0f, 0f, 0f)),
                startX = cx - rx, endX = cx - rx * 0.78f
            ),
            topLeft = Offset(cx - rx, topY), size = Size(rx * 0.22f, bodyH)
        )
        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0f, 0f, 0f, 0f), Color(0f, 0f, 0f, 0.40f)),
                startX = cx + rx * 0.78f, endX = cx + rx
            ),
            topLeft = Offset(cx + rx * 0.78f, topY), size = Size(rx * 0.22f, bodyH)
        )
    }

    // ── 5. Top cap — barely visible slice, cream with radial brush ─────
    val topBrush = Brush.radialGradient(
        colors = listOf(Color(0xFFF4EEE3.toInt()), Color(0xFFDAD4C8.toInt())),
        center = Offset(cx, topY),
        radius = rx * 1.1f
    )
    drawOval(brush = topBrush, topLeft = Offset(cx - rx, topY - ry), size = Size(rx * 2f, ry * 2f))

    // Radial brushed-metal lines on top surface
    for (i in 0 until 80) {
        val la    = (i.toFloat() / 80f) * 2f * PI.toFloat()
        val alpha = if (i % 5 == 0) 0.12f else 0.055f  // occasional brighter line
        drawLine(
            color       = Color(0.22f, 0.20f, 0.17f, alpha),
            start       = Offset(cx + cos(la) * 5f, topY + sin(la) * ry * 0.2f),
            end         = Offset(cx + cos(la) * rx * 0.95f, topY + sin(la) * ry * 0.95f),
            strokeWidth = 0.55f
        )
    }

    // ── 6. Silver indicator line (radial mark on top surface) ─────────
    val iCos = cos(rotRad)
    val iSin = sin(rotRad)
    // Fades out as it rotates to the back of the dial
    val indAlpha = ((iCos + 0.25f) / 1.25f).coerceIn(0f, 1f)
    if (indAlpha > 0.05f) {
        drawLine(
            color       = Color(0.58f, 0.54f, 0.46f, indAlpha),
            start       = Offset(cx + iCos * 6f,         topY + iSin * ry * 0.22f),
            end         = Offset(cx + iCos * rx * 0.87f, topY + iSin * ry * 0.87f),
            strokeWidth = 2.6f,
            cap         = StrokeCap.Round
        )
    }

    // ── 7. Bevel ring — the sharp rim where top meets side wall ───────
    // Front arc: bright highlight catching the light
    drawArc(
        color        = Color(1f, 1f, 0.97f, 0.70f),
        startAngle   = 185f, sweepAngle = 170f, useCenter = false,
        topLeft      = Offset(cx - rx, topY - ry), size = Size(rx * 2f, ry * 2f),
        style        = Stroke(width = 2.4f, cap = StrokeCap.Round)
    )
    // Back arc: subtle shadow
    drawArc(
        color        = Color(0f, 0f, 0f, 0.16f),
        startAngle   = 5f, sweepAngle = 170f, useCenter = false,
        topLeft      = Offset(cx - rx, topY - ry), size = Size(rx * 2f, ry * 2f),
        style        = Stroke(width = 1.6f)
    )

    // ── 8. Vertical ambient occlusion at very top and bottom of body ──
    clipPath(bodyPath) {
        // Top: subtle dark gradient just below bevel (ambient occlusion)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0f, 0f, 0f, 0.14f), Color(0f, 0f, 0f, 0f)),
                startY = topY, endY = topY + bodyH * 0.08f
            ),
            topLeft = Offset(cx - rx, topY), size = Size(rx * 2f, bodyH * 0.08f)
        )
        // Bottom: deeper shadow (light doesn't reach the underside)
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0f, 0f, 0f, 0f), Color(0f, 0f, 0f, 0.28f)),
                startY = botY - bodyH * 0.12f, endY = botY
            ),
            topLeft = Offset(cx - rx, botY - bodyH * 0.12f), size = Size(rx * 2f, bodyH * 0.12f)
        )
    }
}

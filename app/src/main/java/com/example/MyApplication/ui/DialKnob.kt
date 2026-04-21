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

private const val MIN_ROT = -2.618f   // −150°
private const val MAX_ROT =  2.618f   //  +150°

/**
 * Skeuomorphic radio-dial knob — Compose Canvas, zero dependencies.
 *
 * Design intent:
 *  · Low ~15° camera angle: tall side wall, very thin top ellipse
 *  · Matte cream/ivory finish — no sharp specular highlights
 *  · Dense knurling ridges; shift with rotation to sell the 3-D spin
 *  · No hard base; dial fades at the bottom & sits on a soft cast shadow
 */
@Composable
fun DialKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope    = rememberCoroutineScope()
    val rotAnim  = remember { Animatable(lerpF(MIN_ROT, MAX_ROT, value.coerceIn(0f, 1f))) }
    val decay    = remember { exponentialDecay<Float>(frictionMultiplier = 3.2f) }

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            val vt = VelocityTracker()
            detectDragGestures(
                onDragStart = { scope.launch { rotAnim.stop() }; vt.resetTracking() },
                onDrag = { change, drag ->
                    change.consume()
                    vt.addPosition(change.uptimeMillis, change.position)
                    val t = (rotAnim.value + drag.x * 0.010f).coerceIn(MIN_ROT, MAX_ROT)
                    scope.launch { rotAnim.snapTo(t) }
                    onValueChange(normRot(rotAnim.value))
                },
                onDragEnd = {
                    val vx = vt.calculateVelocity().x * 0.010f
                    scope.launch {
                        rotAnim.animateDecay(vx, decay)
                        val c = rotAnim.value.coerceIn(MIN_ROT, MAX_ROT)
                        if (rotAnim.value != c) rotAnim.snapTo(c)
                        onValueChange(normRot(rotAnim.value))
                    }
                }
            )
        }
    ) { drawDial(rotAnim.value) }
}

private fun lerpF(a: Float, b: Float, t: Float) = a + (b - a) * t
private fun normRot(rot: Float) = ((rot - MIN_ROT) / (MAX_ROT - MIN_ROT)).coerceIn(0f, 1f)

// ─── Rendering ────────────────────────────────────────────────────────────────

private fun DrawScope.drawDial(rotRad: Float) {
    val cx    = size.width / 2f
    val rx    = size.width * 0.44f
    val ry    = rx * 0.115f          // flat top ellipse — ~15° perspective
    val bodyH = size.height * 0.84f
    val topY  = (size.height - bodyH) / 2f
    val botY  = topY + bodyH

    // Stadium clip path (top lower-arc → right side → bottom upper-arc → left side)
    val bodyPath = Path().apply {
        moveTo(cx - rx, topY)
        arcTo(Rect(cx - rx, topY - ry, cx + rx, topY + ry), 180f,  180f, false)
        lineTo(cx + rx, botY)
        arcTo(Rect(cx - rx, botY - ry, cx + rx, botY + ry),   0f, -180f, false)
        close()
    }

    // ── Shadow — soft ellipse beneath the dial, gives "placed on field" look ──
    // Drawn outside clip path so it spreads beyond the dial edges
    drawOval(
        brush   = Brush.radialGradient(
            colors  = listOf(Color(0f, 0f, 0f, 0.28f), Color(0f, 0f, 0f, 0f)),
            center  = Offset(cx, botY + ry * 0.6f),
            radius  = rx * 1.05f
        ),
        topLeft = Offset(cx - rx * 1.05f, botY - ry * 0.8f),
        size    = Size(rx * 2.1f, ry * 4.0f)
    )

    // ── Cylinder body — matte horizontal gradient ──────────────────────
    // Edges are still dark (curvature) but the overall range is more compressed
    // compared to a shiny finish — less contrast = matte look
    val bodyGrad = Brush.horizontalGradient(
        colorStops = arrayOf(
            0.00f to Color(0xFF848078.toInt()),
            0.07f to Color(0xFFAEA89E.toInt()),
            0.16f to Color(0xFFCEC9BC.toInt()),
            0.28f to Color(0xFFDEDACC.toInt()),
            0.42f to Color(0xFFE8E3D5.toInt()),
            0.50f to Color(0xFFECE7D8.toInt()),   // centre — only mildly brighter
            0.58f to Color(0xFFE8E3D5.toInt()),
            0.72f to Color(0xFFDAD5C6.toInt()),
            0.84f to Color(0xFFCAC5B6.toInt()),
            0.93f to Color(0xFFAEA89E.toInt()),
            1.00f to Color(0xFF848078.toInt()),
        ),
        startX = cx - rx, endX = cx + rx
    )
    clipPath(bodyPath) {
        drawRect(brush = bodyGrad, topLeft = Offset(cx - rx, topY - ry), size = Size(rx * 2f, bodyH + ry * 2f))
    }

    // ── Knurling ridges — matte calibration ───────────────────────────
    // Highlights are softer than a polished surface
    val numKnurls = 100
    clipPath(bodyPath) {
        for (k in 0 until numKnurls) {
            val theta = (k.toFloat() / numKnurls) * 2f * PI.toFloat() + rotRad
            val cosT  = cos(theta)
            if (cosT < 0.04f) continue

            val xPos = cx + rx * cosT
            if (xPos < cx - rx + 1.5f || xPos > cx + rx - 1.5f) continue

            // Matte: crest highlight max ~48% vs 74% on polished
            val hiliteA = (cosT * 0.38f + 0.06f).coerceIn(0f, 0.48f)
            drawLine(
                color       = Color(1f, 0.99f, 0.95f, hiliteA),
                start       = Offset(xPos, topY + ry * 1.2f),
                end         = Offset(xPos, botY - ry * 0.6f),
                strokeWidth = 0.9f
            )
            // Valley shadow — also softer
            val valleyA = (cosT * 0.12f).coerceIn(0f, 0.16f)
            drawLine(
                color       = Color(0f, 0f, 0f, valleyA),
                start       = Offset(xPos + 2.2f, topY + ry * 1.2f),
                end         = Offset(xPos + 2.2f, botY - ry * 0.6f),
                strokeWidth = 1.8f
            )
        }
    }

    // ── Edge darkening — sides curve away ─────────────────────────────
    clipPath(bodyPath) {
        drawRect(
            brush   = Brush.horizontalGradient(
                colors  = listOf(Color(0f, 0f, 0f, 0.35f), Color(0f, 0f, 0f, 0f)),
                startX  = cx - rx, endX = cx - rx * 0.80f
            ),
            topLeft = Offset(cx - rx, topY), size = Size(rx * 0.20f, bodyH)
        )
        drawRect(
            brush   = Brush.horizontalGradient(
                colors  = listOf(Color(0f, 0f, 0f, 0f), Color(0f, 0f, 0f, 0.35f)),
                startX  = cx + rx * 0.80f, endX = cx + rx
            ),
            topLeft = Offset(cx + rx * 0.80f, topY), size = Size(rx * 0.20f, bodyH)
        )
    }

    // ── Bottom fade — dial dissolves at base instead of having a hard cap ─
    clipPath(bodyPath) {
        drawRect(
            brush   = Brush.verticalGradient(
                colors  = listOf(Color(0f, 0f, 0f, 0f), Color(0f, 0f, 0f, 0.72f)),
                startY  = botY - bodyH * 0.30f, endY = botY + ry
            ),
            topLeft = Offset(cx - rx, botY - bodyH * 0.30f),
            size    = Size(rx * 2f, bodyH * 0.30f + ry)
        )
    }

    // ── Top cap — narrow cream ellipse, barely visible ─────────────────
    drawOval(
        brush   = Brush.radialGradient(
            colors  = listOf(Color(0xFFF2EDE2.toInt()), Color(0xFFD8D3C7.toInt())),
            center  = Offset(cx, topY),
            radius  = rx * 1.1f
        ),
        topLeft = Offset(cx - rx, topY - ry),
        size    = Size(rx * 2f, ry * 2f)
    )

    // Radial brushed-metal lines on top
    for (i in 0 until 80) {
        val la    = (i.toFloat() / 80f) * 2f * PI.toFloat()
        val alpha = if (i % 5 == 0) 0.11f else 0.048f
        drawLine(
            color       = Color(0.22f, 0.20f, 0.17f, alpha),
            start       = Offset(cx + cos(la) * 5f,          topY + sin(la) * ry * 0.2f),
            end         = Offset(cx + cos(la) * rx * 0.95f,  topY + sin(la) * ry * 0.95f),
            strokeWidth = 0.55f
        )
    }

    // ── Silver indicator line ──────────────────────────────────────────
    val iCos = cos(rotRad); val iSin = sin(rotRad)
    val indA = ((iCos + 0.25f) / 1.25f).coerceIn(0f, 1f)
    if (indA > 0.05f) {
        drawLine(
            color       = Color(0.55f, 0.52f, 0.45f, indA * 0.85f),
            start       = Offset(cx + iCos * 6f,         topY + iSin * ry * 0.22f),
            end         = Offset(cx + iCos * rx * 0.87f, topY + iSin * ry * 0.87f),
            strokeWidth = 2.4f, cap = StrokeCap.Round
        )
    }

    // ── Matte rim — very faint arc at the top edge (no glare) ─────────
    drawArc(
        color        = Color(1f, 1f, 0.97f, 0.22f),   // was 0.70 on polished
        startAngle   = 185f, sweepAngle = 170f, useCenter = false,
        topLeft      = Offset(cx - rx, topY - ry), size = Size(rx * 2f, ry * 2f),
        style        = Stroke(width = 1.6f, cap = StrokeCap.Round)
    )

    // ── Top AO — faint darkening just below the rim ────────────────────
    clipPath(bodyPath) {
        drawRect(
            brush   = Brush.verticalGradient(
                colors  = listOf(Color(0f, 0f, 0f, 0.12f), Color(0f, 0f, 0f, 0f)),
                startY  = topY, endY = topY + bodyH * 0.07f
            ),
            topLeft = Offset(cx - rx, topY), size = Size(rx * 2f, bodyH * 0.07f)
        )
    }
}

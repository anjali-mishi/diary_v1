package com.example.myapplication.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.trocchiFamily
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.math.roundToInt

private const val MIN_ROT = -2.618f
private const val MAX_ROT =  2.618f

@Composable
fun DialKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    snapCount: Int = 0,
    items: List<String> = emptyList(),
    backgroundColor: Color = Color(0xFFF5F2EB.toInt()),
    modifier: Modifier = Modifier,
) {
    val scope        = rememberCoroutineScope()
    val rotAnim      = remember { Animatable(lerpF(MIN_ROT, MAX_ROT, value.coerceIn(0f, 1f))) }
    val decay        = remember { exponentialDecay<Float>(frictionMultiplier = 3.2f) }
    val textMeasurer = rememberTextMeasurer()

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
                        val clamped = rotAnim.value.coerceIn(MIN_ROT, MAX_ROT)
                        if (rotAnim.value != clamped) rotAnim.snapTo(clamped)
                        if (snapCount > 1) {
                            val norm    = normRot(rotAnim.value)
                            val nearest = (norm * (snapCount - 1)).roundToInt()
                                .coerceIn(0, snapCount - 1)
                            val snapRot = lerpF(
                                MIN_ROT, MAX_ROT,
                                nearest.toFloat() / (snapCount - 1)
                            )
                            rotAnim.animateTo(
                                targetValue   = snapRot,
                                animationSpec = spring(dampingRatio = 0.62f, stiffness = 280f)
                            )
                        }
                        onValueChange(normRot(rotAnim.value))
                    }
                }
            )
        }
    ) {
        drawDialScene(
            normalizedValue = normRot(rotAnim.value),
            items           = items,
            textMeasurer    = textMeasurer,
            backgroundColor = backgroundColor,
        )
    }
}

private fun lerpF(a: Float, b: Float, t: Float) = a + (b - a) * t
private fun normRot(rot: Float) = ((rot - MIN_ROT) / (MAX_ROT - MIN_ROT)).coerceIn(0f, 1f)

private fun DrawScope.drawDialScene(
    normalizedValue: Float,
    items: List<String>,
    textMeasurer: TextMeasurer,
    backgroundColor: Color,
) {
    val w = size.width
    val h = size.height

    // ── Palette ───────────────────────────────────────────────────────────
    val neuDark    = Color(0.38f, 0.27f, 0.14f, 1f)   // warm shadow tint
    val surfaceUp  = Color(0xFFFCFAF6.toInt())          // --surface-raised
    val surfaceDn  = Color(0xFFEDE6DA.toInt())          // window top (warm/sunken)
    val rimAccent  = Color(0xFF7A6250.toInt())          // --rim  (focal label)
    val mutedFg    = Color(0xFF9A8A78.toInt())          // --muted-foreground (side labels)

    // ── Outer pill geometry ───────────────────────────────────────────────
    val pillH  = h * 0.76f
    val pillT  = (h - pillH) / 2f
    val pillB  = pillT + pillH
    val pillL  = w * 0.04f
    val pillR  = w - pillL
    val pillW  = pillR - pillL
    val pillCR = pillH / 2f
    val step   = pillH * 0.034f    // shadow step — wider spread for softer blur

    // Drop shadow: dark, bottom offset, 5 layers
    for (i in 5 downTo 1) {
        val off = i * step
        drawRoundRect(
            color        = neuDark.copy(alpha = (0.022f - (5 - i) * 0.0045f).coerceAtLeast(0f)),
            topLeft      = Offset(pillL, pillT + off),
            size         = Size(pillW, pillH),
            cornerRadius = CornerRadius(pillCR)
        )
    }
    // Highlight shadow: white, top offset, 5 layers
    for (i in 5 downTo 1) {
        val off = i * step
        drawRoundRect(
            color        = Color(1f, 1f, 1f, (0.14f - (5 - i) * 0.03125f).coerceAtLeast(0f)),
            topLeft      = Offset(pillL, pillT - off),
            size         = Size(pillW, pillH),
            cornerRadius = CornerRadius(pillCR)
        )
    }
    // Pill fill — white border ring
    drawRoundRect(
        color        = Color.White,
        topLeft      = Offset(pillL, pillT),
        size         = Size(pillW, pillH),
        cornerRadius = CornerRadius(pillCR)
    )

    // ── Barrel geometry ───────────────────────────────────────────────────
    val bInset = 12f * density   // 12 dp — outer white border width
    val bL  = pillL + bInset;  val bT = pillT + bInset
    val bR  = pillR - bInset;  val bB = pillB - bInset
    val bW  = bR - bL;         val bH = bB - bT
    val bCR = (pillCR - bInset).coerceAtLeast(0f)

    val barrelPath = Path().apply {
        addRoundRect(RoundRect(bL, bT, bR, bB, CornerRadius(bCR)))
    }

    // ── Metallic rim ring (rim-metallic, p-[2px] equivalent) ─────────────
    // Drawn as a filled shape slightly larger than the barrel.
    // The barrel clip will overdraw its interior — leaving a 2dp metallic ring visible.
    val rimThick = 2f * density   // 2 dp
    drawRoundRect(
        brush        = Brush.linearGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFFD8CDBC.toInt()),
                0.22f to Color(0xFF705E44.toInt()),
                0.50f to Color(0xFF483A28.toInt()),
                0.78f to Color(0xFF806A4C.toInt()),
                1.00f to Color(0xFFE0D6C6.toInt()),
            ),
            start = Offset(bL - rimThick, bT - rimThick),
            end   = Offset(bR + rimThick, bB + rimThick)
        ),
        topLeft      = Offset(bL - rimThick, bT - rimThick),
        size         = Size(bW + rimThick * 2f, bH + rimThick * 2f),
        cornerRadius = CornerRadius(bCR + rimThick)
    )
    // Drop shadow on rim (shadow-[0_2px_4px_rgba(0,0,0,0.25)])
    drawRoundRect(
        color        = Color(0f, 0f, 0f, 0.10f),
        topLeft      = Offset(bL - rimThick + 1f, bT - rimThick + 3f * density),
        size         = Size(bW + rimThick * 2f - 2f, bH + rimThick * 2f),
        cornerRadius = CornerRadius(bCR + rimThick)
    )

    // ── Everything inside the barrel clip ─────────────────────────────────
    clipPath(barrelPath) {

        // ── Layer 1: Pillow surface (gradient-pillow) ─────────────────────
        drawRect(
            brush   = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.00f to backgroundColor,
                    0.38f to backgroundColor,
                    0.72f to Color(0xFFF0EAD8.toInt()),
                    1.00f to Color(0xFFEDE4D4.toInt()),
                ),
                startY = bT, endY = bB
            ),
            topLeft = Offset(bL, bT), size = Size(bW, bH)
        )
        // Inner barrel top-bright / bottom-dark (shadow-neu-in)
        drawRect(
            brush   = Brush.verticalGradient(
                colors = listOf(Color(1f, 1f, 1f, 0.36f), Color.Transparent),
                startY = bT, endY = bT + bH * 0.20f
            ),
            topLeft = Offset(bL, bT), size = Size(bW, bH * 0.20f)
        )
        drawRect(
            brush   = Brush.verticalGradient(
                colors = listOf(Color.Transparent, neuDark.copy(alpha = 0.19f)),
                startY = bB - bH * 0.24f, endY = bB
            ),
            topLeft = Offset(bL, bB - bH * 0.24f), size = Size(bW, bH * 0.24f)
        )

        // ── Layer 2: Horizontal ribs ──────────────────────────────────────
        // CSS: repeating-linear-gradient(0deg, dark/0.4 0 1px, transparent 1px 10px)
        //      mask-image: radial-gradient(ellipse at center, black 55%, transparent 92%)
        //      opacity: 0.18, mix-blend-multiply
        val ribSpacing = bH * 0.092f
        val nRibs      = (bH / ribSpacing + 2).toInt()
        val ribMaxAlpha = 0.085f
        for (r in 0..nRibs) {
            val y         = bT + r * ribSpacing
            // Vertical component of ellipse mask (Y distance from center, 0–1)
            val distY     = abs(y - (bT + bH / 2f)) / (bH / 2f)
            // CSS ellipse mask: full opacity inside 55% radius, fades to 0 at 92%
            val maskY = when {
                distY < 0.55f -> 1f
                distY > 0.92f -> 0f
                else          -> 1f - (distY - 0.55f) / (0.92f - 0.55f)
            }
            if (maskY <= 0f) continue
            drawLine(
                color       = neuDark.copy(alpha = ribMaxAlpha * maskY),
                start       = Offset(bL + bW * 0.04f, y),
                end         = Offset(bR - bW * 0.04f, y),
                strokeWidth = 1f
            )
        }

        // ── Layer 3: Center selection window ─────────────────────────────
        // CSS: top-3 bottom-3 (3px inset each side), w-[140px], rounded-full
        // → nearly full barrel height, fixed width ≈ barrel-height × 1.1 (landscape pill)
        val wInset = bH * 0.12f           // visible gap between window and metallic rim
        val wH     = bH - wInset * 2f     // almost full barrel height
        val wW     = wH * 2.12f           // wide landscape pill
        val wCX    = (bL + bR) / 2f
        val wCY    = (bT + bB) / 2f
        val wL     = wCX - wW / 2f
        val wTop   = wCY - wH / 2f
        val wBot   = wCY + wH / 2f
        val wCR    = wH / 2f              // full pill rounding on short ends

        val windowPath = Path().apply {
            addRoundRect(RoundRect(wL, wTop, wL + wW, wBot, CornerRadius(wCR)))
        }

        // Window fill — CSS: linear-gradient(180deg, L=0.92 0%, L=0.96 50%, L=0.99 100%)
        // = warm-slightly-dark at top → near-white at bottom
        drawPath(
            path  = windowPath,
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.0f to surfaceDn.copy(alpha = 0.85f),
                    0.5f to backgroundColor,
                    1.0f to surfaceUp.copy(alpha = 0.85f),
                ),
                startY = wTop, endY = wBot
            )
        )
        // Window: inset bottom shadow + bottom highlight
        clipPath(windowPath) {
            drawRect(
                brush   = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, neuDark.copy(alpha = 0.20f)),
                    startY = wBot - wH * 0.32f, endY = wBot
                ),
                topLeft = Offset(wL, wBot - wH * 0.32f), size = Size(wW, wH * 0.32f)
            )
            // Bottom highlight
            drawRect(
                brush   = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(1f, 1f, 1f, 0.65f)),
                    startY = wBot - wH * 0.22f, endY = wBot
                ),
                topLeft = Offset(wL, wBot - wH * 0.22f), size = Size(wW, wH * 0.22f)
            )
        }
        // Window border
        drawRoundRect(
            color        = neuDark.copy(alpha = 0.18f),
            topLeft      = Offset(wL, wTop),
            size         = Size(wW, wH),
            cornerRadius = CornerRadius(wCR),
            style        = Stroke(width = 1f)
        )
        // Window outer bottom highlight — spans only the straight section of the pill
        drawLine(
            color       = Color(1f, 1f, 1f, 0.65f),
            start       = Offset(wL + wCR, wBot + 1.2f),
            end         = Offset(wL + wW - wCR, wBot + 1.2f),
            strokeWidth = 1.2f, cap = StrokeCap.Round
        )

        // ── Layer 4: Item labels — clipped to center pill ────────────────
        clipPath(windowPath) {
            if (items.isNotEmpty()) {
                val fractIdx = normalizedValue * (items.size - 1)
                val spacing  = wW * 0.5f

                for (i in items.indices) {
                    val relPos  = i.toFloat() - fractIdx
                    if (abs(relPos) > 2.8f) continue

                    val xCenter = wCX + relPos * spacing
                    if (xCenter < wL - wW || xCenter > wL + wW * 2f) continue

                    val dist    = abs(relPos)
                    val isFocal = dist < 0.5f
                    val alpha   = (1f - dist * 0.58f).coerceIn(0f, 1f)

                    val result = textMeasurer.measure(
                        text  = items[i],
                        style = TextStyle(
                            fontFamily = trocchiFamily,
                            fontSize   = if (isFocal) 16.sp else 11.sp,
                            fontWeight = if (isFocal) FontWeight.SemiBold else FontWeight.Normal,
                            fontStyle  = if (isFocal) FontStyle.Italic else FontStyle.Normal,
                            color      = (if (isFocal) rimAccent else mutedFg).copy(alpha = alpha),
                        )
                    )
                    drawText(
                        textLayoutResult = result,
                        topLeft = Offset(
                            x = xCenter - result.size.width  / 2f,
                            y = wCY    - result.size.height / 2f,
                        )
                    )
                }
            }
        }

        // ── Layer 5: Side fades — last, covers item edges ─────────────────
        // CSS: bg-gradient-to-r from-[oklch(0.9_0.025_70_/_0.7)]  (left)
        //       bg-gradient-to-l from-[oklch(0.9_0.025_70_/_0.7)]  (right)
        val fadeW = bW * 0.09f
        drawRect(
            brush   = Brush.horizontalGradient(
                colors = listOf(backgroundColor, Color.Transparent),
                startX = bL, endX = bL + fadeW
            ),
            topLeft = Offset(bL, bT), size = Size(fadeW, bH)
        )
        drawRect(
            brush   = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, backgroundColor),
                startX = bR - fadeW, endX = bR
            ),
            topLeft = Offset(bR - fadeW, bT), size = Size(fadeW, bH)
        )
    }

    // No explicit rim stroke — gradient contrast between barrel and outer border suffices.
}

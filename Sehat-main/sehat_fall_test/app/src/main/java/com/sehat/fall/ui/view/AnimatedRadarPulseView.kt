package com.sehat.fall.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-graphics Tactical Radar HUD visualizer showing active Geofence boundary protection.
 */
class AnimatedRadarPulseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class PulseMode {
        SAFE_HOME,       // Calm pulsing Emerald/Cyan (sensors asleep, battery saving)
        OUTSIDE_ACTIVE,  // Vigilant scanning Crimson/Violet (fall sensors active)
        SEARCHING_GPS    // Ambient Amber pulse (acquiring position)
    }

    private var currentMode: PulseMode = PulseMode.SAFE_HOME
    private var pulseFraction: Float = 0.0f
    private var sweepAngle: Float = 0.0f

    private var pulseAnimator: ValueAnimator? = null

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        color = Color.argb(45, 56, 189, 248)
    }
    private val sweepPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3.2f
    }
    private val corePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val satPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#38BDF8")
    }
    private val iconTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = 40f
    }

    init {
        startAnimators()
    }

    fun setMode(mode: PulseMode) {
        if (currentMode != mode) {
            currentMode = mode
            invalidate()
        }
    }

    private fun startAnimators() {
        pulseAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2400L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { va ->
                pulseFraction = va.animatedValue as Float
                sweepAngle = (pulseFraction * 360f) % 360f
                invalidate()
            }
            start()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (pulseAnimator?.isRunning != true) {
            startAnimators()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pulseAnimator?.cancel()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0 || h <= 0) return

        val cx = w / 2f
        val cy = h / 2f
        val maxRadius = (w.coerceAtMost(h) / 2f) - 10f

        val baseColor = when (currentMode) {
            PulseMode.SAFE_HOME -> Color.parseColor("#10B981")
            PulseMode.OUTSIDE_ACTIVE -> Color.parseColor("#EF4444")
            PulseMode.SEARCHING_GPS -> Color.parseColor("#F59E0B")
        }

        // 1. Tactical Dark Radial Backdrop
        bgPaint.shader = RadialGradient(
            cx, cy, maxRadius,
            intArrayOf(Color.argb(120, 17, 29, 58), Color.argb(180, 7, 11, 24)),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, cy, maxRadius, bgPaint)

        // 2. Static Concentric Range Rings & Crosshairs
        for (i in 1..3) {
            canvas.drawCircle(cx, cy, maxRadius * (i / 3f), gridPaint)
        }
        canvas.drawLine(cx - maxRadius, cy, cx + maxRadius, cy, gridPaint)
        canvas.drawLine(cx, cy - maxRadius, cx, cy + maxRadius, gridPaint)

        // 3. Rotating Radar Sweep Arc
        canvas.save()
        canvas.rotate(sweepAngle, cx, cy)
        sweepPaint.shader = SweepGradient(
            cx, cy,
            intArrayOf(Color.TRANSPARENT, Color.argb(95, Color.red(baseColor), Color.green(baseColor), Color.blue(baseColor))),
            floatArrayOf(0.75f, 1f)
        )
        canvas.drawCircle(cx, cy, maxRadius, sweepPaint)
        canvas.restore()

        // 4. Expanding Concentric Pulse Waves
        for (i in 0 until 3) {
            val ringProgress = (pulseFraction + (i * 0.333f)) % 1.0f
            val r = ringProgress * maxRadius
            val alpha = ((1.0f - ringProgress) * 200).toInt().coerceIn(0, 255)

            ringPaint.color = baseColor
            ringPaint.alpha = alpha
            canvas.drawCircle(cx, cy, r, ringPaint)
        }

        // 5. Orbiting GPS Satellite Blip
        val rad = Math.toRadians(sweepAngle.toDouble())
        val satX = cx + (maxRadius * 0.68f * cos(rad)).toFloat()
        val satY = cy + (maxRadius * 0.68f * sin(rad)).toFloat()
        satPaint.alpha = 210
        canvas.drawCircle(satX, satY, 6f, satPaint)

        // 6. Central Glowing Core
        corePaint.color = baseColor
        corePaint.alpha = 65
        canvas.drawCircle(cx, cy, 38f, corePaint)
        corePaint.alpha = 230
        canvas.drawCircle(cx, cy, 26f, corePaint)

        // Center Icon
        val icon = when (currentMode) {
            PulseMode.SAFE_HOME -> "🏠"
            PulseMode.OUTSIDE_ACTIVE -> "🛡️"
            PulseMode.SEARCHING_GPS -> "📍"
        }
        val textY = cy - ((iconTextPaint.descent() + iconTextPaint.ascent()) / 2f)
        canvas.drawText(icon, cx, textY, iconTextPaint)
    }
}

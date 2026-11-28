package com.sehat.fall.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sehat.fall.R

/**
 * Premium animated startup splash screen for TRINETRA.
 * Displays the TRINETRA shield-eye emblem with concentric orbital pulse rings,
 * spring scale-in, and letter-spaced wordmark reveal before entering MainActivity.
 */
class SplashActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = FrameLayout(this).apply {
            background = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    Color.parseColor("#0B1024"),
                    Color.parseColor("#060812"),
                    Color.parseColor("#04060D")
                )
            )
        }

        // Animated concentric orbital rings + radial aura
        val orbitBg = SplashOrbitView(this)
        root.addView(orbitBg, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))

        val density = resources.displayMetrics.density
        val centerCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
        }

        // Logo container with subtle glowing border
        val logoSize = (128 * density).toInt()
        val logoCard = FrameLayout(this).apply {
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 32f * density
                setColor(Color.parseColor("#080B16"))
                setStroke((1.5f * density).toInt(), Color.parseColor("#5538BDF8"))
            }
            background = bg
            elevation = 16f * density
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
        }

        val logoImg = ImageView(this).apply {
            setImageResource(R.drawable.app_logo)
            scaleType = ImageView.ScaleType.CENTER_CROP
            clipToOutline = true
        }
        logoCard.addView(logoImg, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))

        centerCol.addView(logoCard, LinearLayout.LayoutParams(logoSize, logoSize))

        // Brand Wordmark
        val brandTitle = TextView(this).apply {
            text = "TRINETRA"
            textSize = 30f
            letterSpacing = 0.28f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            alpha = 0f
            translationY = 24f * density
            setPadding(0, (24 * density).toInt(), 0, 0)
        }
        centerCol.addView(brandTitle)

        // Tagline
        val tagline = TextView(this).apply {
            text = "AI-ASSISTED INTELLIGENT SAFETY"
            textSize = 11f
            letterSpacing = 0.22f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(Color.parseColor("#38BDF8"))
            gravity = Gravity.CENTER
            alpha = 0f
            translationY = 18f * density
            setPadding(0, (8 * density).toInt(), 0, 0)
        }
        centerCol.addView(tagline)

        // Sleek progress bar track & animated fill
        val trackWidth = (140 * density).toInt()
        val trackHeight = (3 * density).toInt()
        val progressTrack = FrameLayout(this).apply {
            background = GradientDrawable().apply {
                cornerRadius = 99f
                setColor(Color.parseColor("#1E293B"))
            }
            alpha = 0f
        }
        val progressFill = View(this).apply {
            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#38BDF8"),
                    Color.parseColor("#8B5CF6"),
                    Color.parseColor("#FFB340")
                )
            ).apply { cornerRadius = 99f }
        }
        progressTrack.addView(progressFill, FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT))
        centerCol.addView(progressTrack, LinearLayout.LayoutParams(trackWidth, trackHeight).apply {
            topMargin = (28 * density).toInt()
        })

        val centerParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.CENTER
        )
        root.addView(centerCol, centerParams)

        // Footer privacy note
        val footer = TextView(this).apply {
            text = "EDGE-FIRST • ZERO CLOUD TELEMETRY"
            textSize = 10f
            letterSpacing = 0.16f
            setTextColor(Color.parseColor("#475569"))
            gravity = Gravity.CENTER
            alpha = 0f
        }
        root.addView(footer, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        ).apply {
            bottomMargin = (36 * density).toInt()
        })

        setContentView(root)

        // Orchestrate entrance animations
        val logoScaleX = ObjectAnimator.ofFloat(logoCard, View.SCALE_X, 0.5f, 1.0f).apply {
            duration = 850
            interpolator = OvershootInterpolator(1.4f)
        }
        val logoScaleY = ObjectAnimator.ofFloat(logoCard, View.SCALE_Y, 0.5f, 1.0f).apply {
            duration = 850
            interpolator = OvershootInterpolator(1.4f)
        }
        val logoAlpha = ObjectAnimator.ofFloat(logoCard, View.ALPHA, 0f, 1f).apply {
            duration = 600
        }

        val titleAlpha = ObjectAnimator.ofFloat(brandTitle, View.ALPHA, 0f, 1f).apply {
            startDelay = 300
            duration = 600
        }
        val titleTrans = ObjectAnimator.ofFloat(brandTitle, View.TRANSLATION_Y, 24f * density, 0f).apply {
            startDelay = 300
            duration = 650
            interpolator = DecelerateInterpolator()
        }

        val tagAlpha = ObjectAnimator.ofFloat(tagline, View.ALPHA, 0f, 1f).apply {
            startDelay = 480
            duration = 600
        }
        val tagTrans = ObjectAnimator.ofFloat(tagline, View.TRANSLATION_Y, 18f * density, 0f).apply {
            startDelay = 480
            duration = 600
            interpolator = DecelerateInterpolator()
        }

        val trackAlpha = ObjectAnimator.ofFloat(progressTrack, View.ALPHA, 0f, 1f).apply {
            startDelay = 550
            duration = 400
        }
        val footerAlpha = ObjectAnimator.ofFloat(footer, View.ALPHA, 0f, 1f).apply {
            startDelay = 650
            duration = 500
        }

        val barAnim = ValueAnimator.ofInt(0, trackWidth).apply {
            startDelay = 550
            duration = 1450
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                val lp = progressFill.layoutParams
                lp.width = anim.animatedValue as Int
                progressFill.layoutParams = lp
            }
        }

        AnimatorSet().apply {
            playTogether(
                logoScaleX, logoScaleY, logoAlpha,
                titleAlpha, titleTrans,
                tagAlpha, tagTrans,
                trackAlpha, footerAlpha, barAnim
            )
            start()
        }

        // Transition to MainActivity
        handler.postDelayed({
            if (!isFinishing) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                @Suppress("DEPRECATION")
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }, 2200)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    /**
     * Custom View drawing glowing radial aura and rotating concentric orbital rings.
     */
    private class SplashOrbitView(context: Context) : View(context) {
        private var phase = 0f
        private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
        }
        private val auraPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        private val animator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 3600
            repeatCount = ValueAnimator.INFINITE
            interpolator = android.view.animation.LinearInterpolator()
            addUpdateListener {
                phase = it.animatedValue as Float
                invalidate()
            }
        }

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            animator.start()
        }

        override fun onDetachedFromWindow() {
            animator.cancel()
            super.onDetachedFromWindow()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val cx = width / 2f
            val cy = height / 2f - (32f * resources.displayMetrics.density)
            val density = resources.displayMetrics.density

            // Radial aura glow
            val auraR = 160f * density
            auraPaint.shader = RadialGradient(
                cx, cy, auraR,
                intArrayOf(
                    Color.argb(65, 56, 189, 248),
                    Color.argb(40, 139, 92, 246),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )
            canvas.drawCircle(cx, cy, auraR, auraPaint)

            // Inner rotating arc
            val r1 = 96f * density
            ringPaint.color = Color.argb(140, 56, 189, 248)
            canvas.drawArc(cx - r1, cy - r1, cx + r1, cy + r1, phase, 110f, false, ringPaint)
            ringPaint.color = Color.argb(110, 139, 92, 246)
            canvas.drawArc(cx - r1, cy - r1, cx + r1, cy + r1, phase + 180f, 95f, false, ringPaint)

            // Outer counter-rotating arc
            val r2 = 128f * density
            ringPaint.color = Color.argb(80, 139, 92, 246)
            canvas.drawArc(cx - r2, cy - r2, cx + r2, cy + r2, -phase * 0.7f, 130f, false, ringPaint)

            // Expanding pulse ring
            val pulseProgress = (phase % 180f) / 180f
            val r3 = (110f + pulseProgress * 65f) * density
            ringPaint.color = Color.argb(((1f - pulseProgress) * 70).toInt(), 56, 189, 248)
            canvas.drawCircle(cx, cy, r3, ringPaint)
        }
    }
}

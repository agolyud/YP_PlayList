package com.example.yp_playlist.presentation.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.example.yp_playlist.R
import kotlin.math.min



class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var play: Bitmap? = null
    private var pause: Bitmap? = null

    private var imageRect = RectF(0f, 0f, 0f, 0f)
    private var isPlaying = false
    var onTouchListener: (() -> Unit)? = null

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                play = getDrawable(R.styleable.PlaybackButtonView_play)?.toBitmap()
                pause = getDrawable(R.styleable.PlaybackButtonView_pause)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val bitmapToDraw = if (isPlaying) pause else play
        bitmapToDraw?.let {
            canvas.drawBitmap(it, null, imageRect, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    togglePlaybackState()
                    invalidate()
                    return true
                }

                else -> {}
            }
        }
        return super.onTouchEvent(event)
    }

    fun togglePlaybackState() {
        onTouchListener?.invoke()
        isPlaying = !isPlaying
    }
}
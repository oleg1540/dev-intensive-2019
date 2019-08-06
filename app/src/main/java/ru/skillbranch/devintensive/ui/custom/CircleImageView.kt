package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.Dimension
import ru.skillbranch.devintensive.utils.Utils
import android.graphics.*
import android.graphics.RectF
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Shader
import android.graphics.BitmapShader
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import ru.skillbranch.devintensive.R
import android.util.Log
import kotlin.math.min

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2
    }
    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH

    private var mBitmapShader: Shader? = null
    private var mShaderMatrix: Matrix? = null

    private var mBitmapDrawBounds: RectF? = null
    private var mStrokeBounds: RectF? = null

    private var mBitmap: Bitmap? = null

    private var mBitmapPaint: Paint? = null
    private var mStrokePaint: Paint? = null

    private var drawInitials = false

    init {
        borderWidth = Utils.dpToPx(borderWidth)

        if(attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
            borderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, Utils.dpToPx(DEFAULT_BORDER_WIDTH))
            a.recycle()
        }

        initDefault()
        setupBitmap()
        Log.d("M_CircleImageView", "init")
    }

    override fun onDraw(canvas: Canvas?) {
        Log.d("M_CircleImageView", "onDraw")
        if (drawInitials) {
            super.onDraw(canvas)
        } else {
            drawBitmap(canvas)
            drawStroke(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d("M_CircleImageView", "onSizeChanged")

        val halfStrokeWidth = mStrokePaint!!.strokeWidth / 2f
        updateCircleDrawBounds(mBitmapDrawBounds!!)
        mStrokeBounds!!.set(mBitmapDrawBounds)
        mStrokeBounds!!.inset(halfStrokeWidth, halfStrokeWidth)

        updateBitmapSize()
    }

    @Dimension
    fun getBorderWidth(): Int = borderWidth

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = dp
        mStrokePaint!!.strokeWidth = Utils.dpToPx(borderWidth).toFloat()
        invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        mStrokePaint!!.color = borderColor
        invalidate()
    }

    fun setInitialsBackground(bitmap: Bitmap) {
        drawInitials = true
        super.setImageBitmap(bitmap)
    }

    private fun initDefault() {
        mShaderMatrix = Matrix()
        mBitmapPaint = Paint(ANTI_ALIAS_FLAG)
        mStrokePaint = Paint(ANTI_ALIAS_FLAG)
        mStrokeBounds = RectF()
        mBitmapDrawBounds = RectF()
        mStrokePaint!!.color = borderColor
        mStrokePaint!!.style = Paint.Style.STROKE
        mStrokePaint!!.strokeWidth = borderWidth.toFloat()
    }

    private fun drawStroke(canvas: Canvas?) {
        if (mStrokePaint!!.strokeWidth > 0f) {
            canvas!!.drawOval(mStrokeBounds!!, mStrokePaint!!)
        }
    }

    private fun drawBitmap(canvas: Canvas?) {
        canvas!!.drawOval(mBitmapDrawBounds!!, mBitmapPaint!!)
    }

    private fun setupBitmap() {
        mBitmap = getBitmapFromDrawable(drawable)
        if (mBitmap == null) {
            return
        }

        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint!!.shader = mBitmapShader

        updateBitmapSize()
    }

    private fun updateCircleDrawBounds(bounds: RectF) {
        val contentWidth = (width - paddingLeft - paddingRight).toFloat()
        val contentHeight = (height - paddingTop - paddingBottom).toFloat()

        var left = paddingLeft.toFloat()
        var top = paddingTop.toFloat()
        if (contentWidth > contentHeight) {
            left += (contentWidth - contentHeight) / 2f
        } else {
            top += (contentHeight - contentWidth) / 2f
        }

        val diameter = min(contentWidth, contentHeight)
        bounds.set(left, top, left + diameter, top + diameter)
    }

    private fun updateBitmapSize() {
        if (mBitmap == null) return

        val dx: Float
        val dy: Float
        val scale: Float

        // scale up/down with respect to this view size and maintain aspect ratio
        // translate bitmap position with dx/dy to the center of the image
        if (mBitmap!!.width < mBitmap!!.height) {
            scale = mBitmapDrawBounds!!.width() / mBitmap!!.width
            dx = mBitmapDrawBounds!!.left
            dy = mBitmapDrawBounds!!.top - mBitmap!!.height * scale / 2f + mBitmapDrawBounds!!.width() / 2f
        } else {
            scale = mBitmapDrawBounds!!.height() / mBitmap!!.height
            dx = mBitmapDrawBounds!!.left - mBitmap!!.width * scale / 2f + mBitmapDrawBounds!!.width() / 2f
            dy = mBitmapDrawBounds!!.top
        }
        mShaderMatrix!!.setScale(scale, scale)
        mShaderMatrix!!.postTranslate(dx, dy)
        mBitmapShader!!.setLocalMatrix(mShaderMatrix)
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}
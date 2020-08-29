package com.vishaldairy.otpview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.MovementMethod
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import kotlin.math.abs
import kotlin.math.roundToInt

class OtpView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.otpViewStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {
    private val viewType: Int
    private var otpViewItemCount: Int
    private var otpViewItemWidth: Int
    private var otpViewItemHeight: Int
    private var otpViewItemRadius: Int
    private var otpViewItemSpacing: Int
    private val paint: Paint
    private val animatorTextPaint: TextPaint? = TextPaint()

    private var lineColors: ColorStateList?

    @get:ColorInt
    var currentLineColor = Color.BLACK
        private set
    private var lineWidth: Int
    private val textRect = Rect()
    private val itemBorderRect = RectF()
    private val itemLineRect = RectF()
    private val path = Path()
    private val itemCenterPoint = PointF()
    private lateinit var defaultAddAnimator: ValueAnimator
    private var isAnimationEnable = false
    private var blink: Blink? = null
    private var isCursorVisible: Boolean
    private var drawCursor = false
    private var cursorHeight = 0f
    private var cursorWidth: Int
    private var cursorColor: Int
    private var itemBackgroundResource = 0
    private var itemBackground: Drawable?
    private var hideLineWhenFilled: Boolean
    private val rtlTextDirection: Boolean
    private var maskingChar: String?
    private var onOtpCompletionListener: OnOtpCompletionListener? = null

    override fun setTypeface(tf: Typeface?) {
        super.setTypeface(tf)
        animatorTextPaint?.set(getPaint())
    }

    private fun setMaxLength(maxLength: Int) {
        filters = if (maxLength >= 0) arrayOf<InputFilter>(LengthFilter(maxLength)) else NO_FILTERS
    }

    private fun setupAnimator() {
        defaultAddAnimator = ValueAnimator.ofFloat(0.5f, 1f)
        defaultAddAnimator.duration = 150
        defaultAddAnimator.interpolator = DecelerateInterpolator()
        defaultAddAnimator.addUpdateListener { animation ->
            val scale = animation.animatedValue as Float
            val alpha = (255 * scale).toInt()
            animatorTextPaint!!.textSize = textSize * scale
            animatorTextPaint.alpha = alpha
            postInvalidate()
        }
    }

    private fun checkItemRadius() {
        if (viewType == VIEW_TYPE_LINE) {
            val halfOfLineWidth = lineWidth.toFloat() / 2
            require(otpViewItemRadius <= halfOfLineWidth) { "The itemRadius can not be greater than lineWidth when viewType is line" }
        } else if (viewType == VIEW_TYPE_RECTANGLE) {
            val halfOfItemWidth = otpViewItemWidth.toFloat() / 2
            require(otpViewItemRadius <= halfOfItemWidth) { "The itemRadius can not be greater than itemWidth" }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var width: Int
        val height: Int
        val boxHeight = otpViewItemHeight
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize
        } else {
            val boxesWidth =
                (otpViewItemCount - 1) * otpViewItemSpacing + otpViewItemCount * otpViewItemWidth
            width = boxesWidth + ViewCompat.getPaddingEnd(this) + ViewCompat.getPaddingStart(this)
            if (otpViewItemSpacing == 0) {
                width -= (otpViewItemCount - 1) * lineWidth
            }
        }
        height =
            if (heightMode == MeasureSpec.EXACTLY) heightSize else boxHeight + paddingTop + paddingBottom
        setMeasuredDimension(width, height)
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (start != text.length) {
            moveSelectionToEnd()
        }
        if (text.length == otpViewItemCount && onOtpCompletionListener != null) {
            onOtpCompletionListener!!.onOtpCompleted(text.toString())
        }
        makeBlink()
        if (isAnimationEnable) {
            val isAdd = lengthAfter - lengthBefore > 0
            if (isAdd) {
                defaultAddAnimator.end()
                defaultAddAnimator.start()
            }
        }
    }

    override fun onFocusChanged(
        focused: Boolean,
        direction: Int,
        previouslyFocusedRect: Rect?
    ) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            moveSelectionToEnd()
            makeBlink()
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (text != null && selEnd != text!!.length) {
            moveSelectionToEnd()
        }
    }

    private fun moveSelectionToEnd() {
        if (text != null) {
            setSelection(text!!.length)
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (lineColors == null || lineColors!!.isStateful) {
            updateColors()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        updatePaints()
        drawOtpView(canvas)
        canvas.restore()
    }

    private fun updatePaints() {
        paint.color = currentLineColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = lineWidth.toFloat()
        getPaint().color = currentTextColor
    }

    private fun drawOtpView(canvas: Canvas) {
        val nextItemToFill: Int = if (rtlTextDirection) {
            otpViewItemCount - 1
        } else {
            if (text != null) {
                text!!.length
            } else {
                0
            }
        }
        for (i in 0 until otpViewItemCount) {
            val itemSelected = isFocused && nextItemToFill == i
            val itemFilled = i < nextItemToFill
            var itemState: IntArray? = null
            if (itemFilled) {
                itemState = FILLED_STATE
            } else if (itemSelected) {
                itemState = SELECTED_STATE
            }
            paint.color = (if(itemState!=null) getLineColorForState(itemState)
                    else currentLineColor)
            updateItemRectF(i)
            updateCenterPoint()
            canvas.save()
            if (viewType == VIEW_TYPE_RECTANGLE) {
                updateOtpViewBoxPath(i)
                canvas.clipPath(path)
            }
            drawItemBackground(canvas, itemState)
            canvas.restore()
            if (itemSelected) {
                drawCursor(canvas)
            }
            if (viewType == VIEW_TYPE_RECTANGLE) {
                drawOtpBox(canvas, i)
            } else if (viewType == VIEW_TYPE_LINE) {
                drawOtpLine(canvas, i)
            }
            if (rtlTextDirection) {
                val reversedPosition = otpViewItemCount - i
                if (text!!.length >= reversedPosition) {
                    drawInput(canvas, i)
                } else if (!TextUtils.isEmpty(hint) && hint.length == otpViewItemCount) {
                    drawHint(canvas, i)
                }
            } else {
                if (text!!.length > i) {
                    drawInput(canvas, i)
                } else if (!TextUtils.isEmpty(hint) && hint.length == otpViewItemCount) {
                    drawHint(canvas, i)
                }
            }
        }
        if (isFocused
            && text != null && text!!.length != otpViewItemCount && viewType == VIEW_TYPE_RECTANGLE
        ) {
            val index = text!!.length
            updateItemRectF(index)
            updateCenterPoint()
            updateOtpViewBoxPath(index)
            paint.color = getLineColorForState(SELECTED_STATE)
            drawOtpBox(canvas, index)
        }
    }

    private fun drawInput(canvas: Canvas, i: Int) {
        if (maskingChar != null &&
            (isNumberInputType(inputType) ||
                    isPasswordInputType(inputType))
        ) {
            drawMaskingText(canvas, i, maskingChar!![0].toString())
        } else if (isPasswordInputType(inputType)) {
            drawCircle(canvas, i)
        } else {
            drawText(canvas, i)
        }
    }

    private fun getLineColorForState(states: IntArray): Int {
        return if (lineColors != null) lineColors!!.getColorForState(
            states,
            currentLineColor
        ) else currentLineColor
    }

    private fun drawItemBackground(
        canvas: Canvas,
        backgroundState: IntArray?
    ) {
        if (itemBackground == null) {
            return
        }
        val delta = lineWidth.toFloat() / 2
        val left = (itemBorderRect.left - delta).roundToInt()
        val top = (itemBorderRect.top - delta).roundToInt()
        val right = (itemBorderRect.right + delta).roundToInt()
        val bottom = (itemBorderRect.bottom + delta).roundToInt()
        itemBackground!!.setBounds(left, top, right, bottom)
        if (viewType != VIEW_TYPE_NONE) {
            itemBackground!!.state = backgroundState ?: drawableState
        }
        itemBackground!!.draw(canvas)
    }

    private fun updateOtpViewBoxPath(i: Int) {
        var drawRightCorner = false
        var drawLeftCorner = false
        if (otpViewItemSpacing != 0) {
            drawRightCorner = true
            drawLeftCorner = drawRightCorner
        } else {
            if (i == 0 && i != otpViewItemCount - 1) {
                drawLeftCorner = true
            }
            if (i == otpViewItemCount - 1 && i != 0) {
                drawRightCorner = true
            }
        }
        updateRoundRectPath(
            itemBorderRect,
            otpViewItemRadius.toFloat(),
            otpViewItemRadius.toFloat(),
            drawLeftCorner,
            drawRightCorner
        )
    }

    private fun drawOtpBox(canvas: Canvas, i: Int) {
        if (text != null && hideLineWhenFilled && i < text!!.length) {
            return
        }
        canvas.drawPath(path, paint)
    }

    private fun drawOtpLine(canvas: Canvas, i: Int) {
        if (text != null && hideLineWhenFilled && i < text!!.length) {
            return
        }
        var drawLeft: Boolean
        var drawRight: Boolean
        drawRight = true
        drawLeft = drawRight
        if (otpViewItemSpacing == 0 && otpViewItemCount > 1) {
            when (i) {
                0 -> {
                    drawRight = false
                }
                otpViewItemCount - 1 -> {
                    drawLeft = false
                }
                else -> {
                    drawRight = false
                    drawLeft = drawRight
                }
            }
        }
        paint.style = Paint.Style.FILL
        paint.strokeWidth = lineWidth.toFloat() / 10
        val halfLineWidth = lineWidth.toFloat() / 2
        itemLineRect[itemBorderRect.left - halfLineWidth, itemBorderRect.bottom - halfLineWidth, itemBorderRect.right + halfLineWidth] =
            itemBorderRect.bottom + halfLineWidth
        updateRoundRectPath(
            itemLineRect,
            otpViewItemRadius.toFloat(),
            otpViewItemRadius.toFloat(),
            drawLeft,
            drawRight
        )
        canvas.drawPath(path, paint)
    }

    private fun drawCursor(canvas: Canvas) {
        if (drawCursor) {
            val cx = itemCenterPoint.x
            val cy = itemCenterPoint.y
            val y = cy - cursorHeight / 2
            val color = paint.color
            val width = paint.strokeWidth
            paint.color = cursorColor
            paint.strokeWidth = cursorWidth.toFloat()
            canvas.drawLine(cx, y, cx, y + cursorHeight, paint)
            paint.color = color
            paint.strokeWidth = width
        }
    }

    private fun updateRoundRectPath(
        rectF: RectF,
        rx: Float,
        ry: Float,
        l: Boolean,
        r: Boolean
    ) {
        updateRoundRectPath(rectF, rx, ry, l, r, r, l)
    }

    private fun updateRoundRectPath(
        rectF: RectF, rx: Float, ry: Float,
        tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean
    ) {
        path.reset()
        val l = rectF.left
        val t = rectF.top
        val r = rectF.right
        val b = rectF.bottom
        val w = r - l
        val h = b - t
        val lw = w - 2 * rx
        val lh = h - 2 * ry
        path.moveTo(l, t + ry)
        if (tl) {
            path.rQuadTo(0f, -ry, rx, -ry)
        } else {
            path.rLineTo(0f, -ry)
            path.rLineTo(rx, 0f)
        }
        path.rLineTo(lw, 0f)
        if (tr) {
            path.rQuadTo(rx, 0f, rx, ry)
        } else {
            path.rLineTo(rx, 0f)
            path.rLineTo(0f, ry)
        }
        path.rLineTo(0f, lh)
        if (br) {
            path.rQuadTo(0f, ry, -rx, ry)
        } else {
            path.rLineTo(0f, ry)
            path.rLineTo(-rx, 0f)
        }
        path.rLineTo(-lw, 0f)
        if (bl) {
            path.rQuadTo(-rx, 0f, -rx, -ry)
        } else {
            path.rLineTo(-rx, 0f)
            path.rLineTo(0f, -ry)
        }
        path.rLineTo(0f, -lh)
        path.close()
    }

    private fun updateItemRectF(i: Int) {
        val halfLineWidth = lineWidth.toFloat() / 2
        var left = (scrollX
                + ViewCompat.getPaddingStart(this)
                + i * (otpViewItemSpacing + otpViewItemWidth) + halfLineWidth)
        if (otpViewItemSpacing == 0 && i > 0) {
            left -= lineWidth * i
        }
        val right = left + otpViewItemWidth - lineWidth
        val top = scrollY + paddingTop + halfLineWidth
        val bottom = top + otpViewItemHeight - lineWidth
        itemBorderRect[left, top, right] = bottom
    }

    private fun drawText(canvas: Canvas, i: Int) {
        val paint = getPaintByIndex(i)
        paint!!.color = currentTextColor
        if (rtlTextDirection) {
            val reversedPosition = otpViewItemCount - i
            val reversedCharPosition: Int
            reversedCharPosition = if (text == null) {
                reversedPosition
            } else {
                reversedPosition - text!!.length
            }
            if (reversedCharPosition <= 0 && text != null) {
                drawTextAtBox(canvas, paint, text, abs(reversedCharPosition))
            }
        } else if (text != null) {
            drawTextAtBox(canvas, paint, text, i)
        }
    }

    private fun drawMaskingText(
        canvas: Canvas,
        i: Int,
        maskingChar: String
    ) {
        val paint = getPaintByIndex(i)
        paint!!.color = currentTextColor
        if (rtlTextDirection) {
            val reversedPosition = otpViewItemCount - i
            val reversedCharPosition: Int
            reversedCharPosition = if (text == null) {
                reversedPosition
            } else {
                reversedPosition - text!!.length
            }
            if (reversedCharPosition <= 0 && text != null) {
                drawTextAtBox(
                    canvas, paint, text.toString().replace(".".toRegex(), maskingChar),
                    abs(reversedCharPosition)
                )
            }
        } else if (text != null) {
            drawTextAtBox(
                canvas,
                paint,
                text.toString().replace(".".toRegex(), maskingChar),
                i
            )
        }
    }

    private fun drawHint(canvas: Canvas, i: Int) {
        val paint = getPaintByIndex(i)
        paint!!.color = currentHintTextColor
        if (rtlTextDirection) {
            val reversedPosition = otpViewItemCount - i
            val reversedCharPosition = reversedPosition - hint.length
            if (reversedCharPosition <= 0) {
                drawTextAtBox(canvas, paint, hint, abs(reversedCharPosition))
            }
        } else {
            drawTextAtBox(canvas, paint, hint, i)
        }
    }

    private fun drawTextAtBox(
        canvas: Canvas,
        paint: Paint?,
        text: CharSequence?,
        charAt: Int
    ) {
        paint!!.getTextBounds(text.toString(), charAt, charAt + 1, textRect)
        val cx = itemCenterPoint.x
        val cy = itemCenterPoint.y
        val x =
            cx - abs(textRect.width().toFloat()) / 2 - textRect.left
        val y =
            cy + abs(textRect.height().toFloat()) / 2 - textRect.bottom
        if (text != null) {
            canvas.drawText(text, charAt, charAt + 1, x, y, paint)
        }
    }

    private fun drawCircle(canvas: Canvas, i: Int) {
        val paint = getPaintByIndex(i)
        val cx = itemCenterPoint.x
        val cy = itemCenterPoint.y
        if (rtlTextDirection) {
            val reversedItemPosition = otpViewItemCount - i
            val reversedCharPosition = reversedItemPosition - hint.length
            if (reversedCharPosition <= 0) {
                canvas.drawCircle(cx, cy, paint!!.textSize / 2, paint)
            }
        } else {
            canvas.drawCircle(cx, cy, paint!!.textSize / 2, paint)
        }
    }

    private fun getPaintByIndex(i: Int): Paint? {
        return if (text != null && isAnimationEnable && i == text!!.length - 1) {
            animatorTextPaint!!.color = getPaint().color
            animatorTextPaint
        } else {
            getPaint()
        }
    }

    private fun drawAnchorLine(canvas: Canvas) {
        var cx = itemCenterPoint.x
        var cy = itemCenterPoint.y
        paint.strokeWidth = 1f
        cx -= paint.strokeWidth / 2
        cy -= paint.strokeWidth / 2
        path.reset()
        path.moveTo(cx, itemBorderRect.top)
        path.lineTo(cx, itemBorderRect.top + abs(itemBorderRect.height()))
        canvas.drawPath(path, paint)
        path.reset()
        path.moveTo(itemBorderRect.left, cy)
        path.lineTo(itemBorderRect.left + abs(itemBorderRect.width()), cy)
        canvas.drawPath(path, paint)
        path.reset()
        paint.strokeWidth = lineWidth.toFloat()
    }

    private fun updateColors() {
        var shouldInvalidate = false
        val color = if (lineColors != null) lineColors!!.getColorForState(
            drawableState,
            0
        ) else currentTextColor
        if (color != currentLineColor) {
            currentLineColor = color
            shouldInvalidate = true
        }
        if (shouldInvalidate) {
            invalidate()
        }
    }

    private fun updateCenterPoint() {
        val cx = itemBorderRect.left + abs(itemBorderRect.width()) / 2
        val cy = itemBorderRect.top + abs(itemBorderRect.height()) / 2
        itemCenterPoint[cx] = cy
    }

    override fun getDefaultMovementMethod(): MovementMethod? {
        return DefaultMovementMethod.instance
    }

    fun setLineColor(@ColorInt color: Int) {
        lineColors = ColorStateList.valueOf(color)
        updateColors()
    }

    fun setLineColor(colors: ColorStateList?) {
        requireNotNull(colors) { "Color cannot be null" }
        lineColors = colors
        updateColors()
    }

    fun setLineWidth(@Px borderWidth: Int) {
        lineWidth = borderWidth
        checkItemRadius()
        requestLayout()
    }

    fun getLineWidth(): Int {
        return lineWidth
    }

    var itemCount: Int
        get() = otpViewItemCount
        set(count) {
            otpViewItemCount = count
            setMaxLength(count)
            requestLayout()
        }

    var itemRadius: Int
        get() = otpViewItemRadius
        set(itemRadius) {
            otpViewItemRadius = itemRadius
            checkItemRadius()
            requestLayout()
        }

    @get:Px
    var itemSpacing: Int
        get() = otpViewItemSpacing
        set(itemSpacing) {
            otpViewItemSpacing = itemSpacing
            requestLayout()
        }

    var itemHeight: Int
        get() = otpViewItemHeight
        set(itemHeight) {
            otpViewItemHeight = itemHeight
            updateCursorHeight()
            requestLayout()
        }

    var itemWidth: Int
        get() = otpViewItemWidth
        set(itemWidth) {
            otpViewItemWidth = itemWidth
            checkItemRadius()
            requestLayout()
        }

    fun setAnimationEnable(enable: Boolean) {
        isAnimationEnable = enable
    }

    fun setHideLineWhenFilled(hideLineWhenFilled: Boolean) {
        this.hideLineWhenFilled = hideLineWhenFilled
    }

    override fun setTextSize(size: Float) {
        super.setTextSize(size)
        updateCursorHeight()
    }

    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        updateCursorHeight()
    }

    fun setOtpCompletionListener(otpCompletionListener: OnOtpCompletionListener?) {
        onOtpCompletionListener = otpCompletionListener
    }
    fun setItemBackgroundResources(@DrawableRes resId: Int) {
        if (resId != 0 && itemBackgroundResource != resId) {
            return
        }
        itemBackground = ResourcesCompat.getDrawable(resources, resId, context.theme)
        setItemBackground(itemBackground)
        itemBackgroundResource = resId
    }

    fun setItemBackgroundColor(@ColorInt color: Int) {
        if (itemBackground is ColorDrawable) {
            ((itemBackground as ColorDrawable).mutate() as ColorDrawable).color = color
            itemBackgroundResource = 0
        } else {
            setItemBackground(ColorDrawable(color))
        }
    }

    private fun setItemBackground(background: Drawable?) {
        itemBackgroundResource = 0
        itemBackground = background
        invalidate()
    }
    fun setCursorWidth(@Px width: Int) {
        cursorWidth = width
        if (isCursorVisible()) {
            invalidateCursor(true)
        }
    }

    fun getCursorWidth(): Int {
        return cursorWidth
    }

    fun setCursorColor(@ColorInt color: Int) {
        cursorColor = color
        if (isCursorVisible()) {
            invalidateCursor(true)
        }
    }

    fun getCursorColor(): Int {
        return cursorColor
    }

    fun setMaskingChar(maskingChar: String?) {
        this.maskingChar = maskingChar
        requestLayout()
    }

    fun getMaskingChar(): String? {
        return maskingChar
    }

    override fun setCursorVisible(visible: Boolean) {
        if (isCursorVisible != visible) {
            isCursorVisible = visible
            invalidateCursor(isCursorVisible)
            makeBlink()
        }
    }

    override fun isCursorVisible(): Boolean {
        return isCursorVisible
    }

    override fun onScreenStateChanged(screenState: Int) {
        super.onScreenStateChanged(screenState)
        if (screenState == View.SCREEN_STATE_ON) {
            resumeBlink()
        } else if (screenState == View.SCREEN_STATE_OFF) {
            suspendBlink()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        resumeBlink()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        suspendBlink()
    }

    private fun shouldBlink(): Boolean {
        return isCursorVisible() && isFocused
    }

    private fun makeBlink() {
        if (shouldBlink()) {
            removeCallbacks(blink)
            drawCursor = false
            postDelayed(blink, BLINK.toLong())
        } else {
            removeCallbacks(blink)
        }
    }

    private fun suspendBlink() {
        blink?.cancel()
        invalidateCursor(false)
    }

    private fun resumeBlink() {
        blink?.unCancel()
        makeBlink()
    }

    private fun invalidateCursor(showCursor: Boolean) {
        if (drawCursor != showCursor) {
            drawCursor = showCursor
            invalidate()
        }
    }

    private fun updateCursorHeight() {
        val delta = 2 * dpToPx()
        cursorHeight =
            if (otpViewItemHeight - textSize > delta) textSize + delta else textSize
    }

    private inner class Blink : Runnable {
        private var cancelled = false
        override fun run() {
            if (cancelled) {
                return
            }
            removeCallbacks(this)
            if (shouldBlink()) {
                invalidateCursor(!drawCursor)
                postDelayed(this, BLINK.toLong())
            }
        }

        fun cancel() {
            if (!cancelled) {
                removeCallbacks(this)
                cancelled = true
            }
        }

        fun unCancel() {
            cancelled = false
        }
    }

    //endregion
    private fun dpToPx(): Int {
        return (2.toFloat() * resources.displayMetrics.density + 0.5f).toInt()
    }

    companion object {
        private const val DBG = false
        private const val BLINK = 500
        private const val DEFAULT_COUNT = 4
        private val NO_FILTERS = arrayOfNulls<InputFilter>(0)
        private val SELECTED_STATE = intArrayOf(
            android.R.attr.state_selected
        )
        private val FILLED_STATE = intArrayOf(
            R.attr.state_filled
        )
        private const val VIEW_TYPE_RECTANGLE = 0
        private const val VIEW_TYPE_LINE = 1
        private const val VIEW_TYPE_NONE = 2
        private fun isPasswordInputType(inputType: Int): Boolean {
            val variation =
                inputType and (EditorInfo.TYPE_MASK_CLASS or EditorInfo.TYPE_MASK_VARIATION)
            return (variation
                    == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD) || (variation
                    == EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD) || (variation
                    == EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
        }

        private fun isNumberInputType(inputType: Int): Boolean {
            return inputType == EditorInfo.TYPE_CLASS_NUMBER
        }
    }

    init {
        val res = resources
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        animatorTextPaint!!.set(getPaint())
        val theme = context.theme
        val typedArray =
            theme.obtainStyledAttributes(attrs, R.styleable.OtpView, defStyleAttr, 0)
        viewType = typedArray.getInt(R.styleable.OtpView_viewType, VIEW_TYPE_NONE)
        otpViewItemCount =
            typedArray.getInt(R.styleable.OtpView_itemCount, DEFAULT_COUNT)
        otpViewItemHeight = typedArray.getDimension(
            R.styleable.OtpView_itemHeight,
            res.getDimensionPixelSize(R.dimen.otp_view_item_size).toFloat()
        ).toInt()
        otpViewItemWidth = typedArray.getDimension(
            R.styleable.OtpView_itemWidth,
            res.getDimensionPixelSize(R.dimen.otp_view_item_size).toFloat()
        ).toInt()
        otpViewItemSpacing = typedArray.getDimensionPixelSize(
            R.styleable.OtpView_itemSpacing,
            res.getDimensionPixelSize(R.dimen.otp_view_item_spacing)
        )
        otpViewItemRadius = typedArray.getDimension(R.styleable.OtpView_itemRadius, 0f).toInt()
        lineWidth = typedArray.getDimension(
            R.styleable.OtpView_lineWidth,
            res.getDimensionPixelSize(R.dimen.otp_view_item_line_width).toFloat()
        ).toInt()
        lineColors = typedArray.getColorStateList(R.styleable.OtpView_lineColor)
        isCursorVisible = typedArray.getBoolean(R.styleable.OtpView_android_cursorVisible, true)
        cursorColor = typedArray.getColor(R.styleable.OtpView_cursorColor, currentTextColor)
        cursorWidth = typedArray.getDimensionPixelSize(
            R.styleable.OtpView_cursorWidth,
            res.getDimensionPixelSize(R.dimen.otp_view_cursor_width)
        )
        itemBackground = typedArray.getDrawable(R.styleable.OtpView_android_itemBackground)
        hideLineWhenFilled = typedArray.getBoolean(R.styleable.OtpView_hideLineWhenFilled, false)
        rtlTextDirection = typedArray.getBoolean(R.styleable.OtpView_rtlTextDirection, false)
        maskingChar = typedArray.getString(R.styleable.OtpView_maskingChar)
        typedArray.recycle()
        if (lineColors != null) {
            currentLineColor = lineColors!!.defaultColor
        }
        updateCursorHeight()
        checkItemRadius()
        setMaxLength(otpViewItemCount)
        paint.strokeWidth = lineWidth.toFloat()
        setupAnimator()
        super.setCursorVisible(false)
        setTextIsSelectable(false)
    }
}
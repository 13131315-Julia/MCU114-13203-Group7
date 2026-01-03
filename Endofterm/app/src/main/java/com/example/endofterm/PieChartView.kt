package com.example.endofterm

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class PieDataItem(
        val label: String,
        val value: Float,
        val color: Int
    )

    private val defaultColors = listOf(
        Color.parseColor("#FF6B6B"), // 紅色 - 餐飲
        Color.parseColor("#4ECDC4"), // 青色 - 交通
        Color.parseColor("#FFD166"), // 黃色 - 娛樂
        Color.parseColor("#06D6A0"), // 綠色 - 購物
        Color.parseColor("#118AB2"), // 藍色 - 房租
        Color.parseColor("#073B4C"), // 深藍 - 水電費
        Color.parseColor("#7209B7"), // 紫色 - 電話費
        Color.parseColor("#F72585"), // 粉色 - 醫療
        Color.parseColor("#3A86FF"), // 亮藍 - 教育
        Color.parseColor("#6A994E")  // 墨綠 - 其他
    )

    private var pieData: List<PieDataItem> = emptyList()
    private var totalValue: Float = 0f
    private var selectedSlice: Int = -1

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val centerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var radius: Float = 0f
    private var centerX: Float = 0f
    private var centerY: Float = 0f

    var onSliceClickListener: ((Int, PieDataItem) -> Unit)? = null

    init {
        setupPaints()
    }

    private fun setupPaints() {
        // 圓餅圖填充
        paint.style = Paint.Style.FILL

        // 邊框
        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = Color.WHITE
        strokePaint.strokeWidth = 3f

        // 中心文字
        centerTextPaint.color = Color.DKGRAY
        centerTextPaint.textSize = 32f
        centerTextPaint.textAlign = Paint.Align.CENTER
        centerTextPaint.typeface = Typeface.DEFAULT_BOLD

        // 扇形文字
        textPaint.color = Color.WHITE
        textPaint.textSize = 24f
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.DEFAULT_BOLD
    }

    fun setData(data: List<Pair<String, Float>>) {
        pieData = data.mapIndexed { index, (label, value) ->
            val color = if (index < defaultColors.size) defaultColors[index]
            else defaultColors[index % defaultColors.size]
            PieDataItem(label, value, color)
        }
        totalValue = pieData.sumOf { it.value.toDouble() }.toFloat()
        invalidate()
    }

    fun clearData() {
        pieData = emptyList()
        totalValue = 0f
        selectedSlice = -1
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = 40f
        centerX = w / 2f
        centerY = h / 2f
        radius = min(centerX, centerY) - padding
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (pieData.isEmpty() || totalValue == 0f) {
            drawEmptyState(canvas)
            return
        }

        drawPieChart(canvas)
        drawCenterText(canvas)
    }

    private fun drawEmptyState(canvas: Canvas) {
        paint.color = Color.LTGRAY
        canvas.drawCircle(centerX, centerY, radius, paint)

        strokePaint.color = Color.GRAY
        canvas.drawCircle(centerX, centerY, radius, strokePaint)

        centerTextPaint.color = Color.GRAY
        canvas.drawText("無數據", centerX, centerY, centerTextPaint)
    }

    private fun drawPieChart(canvas: Canvas) {
        var startAngle = -90f

        pieData.forEachIndexed { index, item ->
            val sweepAngle = (item.value / totalValue) * 360f

            // 設置顏色
            paint.color = if (index == selectedSlice) darkenColor(item.color) else item.color

            // 繪製扇形
            val rect = RectF(
                centerX - radius,
                centerY - radius,
                centerX + radius,
                centerY + radius
            )
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint)

            // 繪製邊框
            canvas.drawArc(rect, startAngle, sweepAngle, true, strokePaint)

            // 繪製百分比標籤（較大的扇形才顯示）
            if (sweepAngle > 20f) {
                drawSliceLabel(canvas, startAngle, sweepAngle, item)
            }

            startAngle += sweepAngle
        }
    }

    private fun drawSliceLabel(canvas: Canvas, startAngle: Float, sweepAngle: Float, item: PieDataItem) {
        val midAngle = startAngle + sweepAngle / 2
        val midAngleRad = Math.toRadians(midAngle.toDouble())

        val labelRadius = radius * 0.7f
        val x = centerX + (labelRadius * cos(midAngleRad).toFloat())
        val y = centerY + (labelRadius * sin(midAngleRad).toFloat())

        val percentage = (item.value / totalValue * 100).toInt()
        canvas.drawText("$percentage%", x, y, textPaint)
    }

    private fun drawCenterText(canvas: Canvas) {
        // 繪製中間圓孔
        paint.color = Color.WHITE
        canvas.drawCircle(centerX, centerY, radius * 0.5f, paint)

        // 繪製總金額
        centerTextPaint.color = Color.DKGRAY
        val totalText = "總計\n$${String.format("%.2f", totalValue)}"

        val textBounds = Rect()
        centerTextPaint.getTextBounds(totalText, 0, totalText.indexOf("\n"), textBounds)
        canvas.drawText(totalText, centerX, centerY - textBounds.height() / 2, centerTextPaint)
    }

    private fun darkenColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= 0.7f
        return Color.HSVToColor(hsv)
    }

    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        when (event.action) {
            android.view.MotionEvent.ACTION_DOWN -> {
                handleTouch(event.x, event.y)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleTouch(x: Float, y: Float) {
        val dx = x - centerX
        val dy = y - centerY
        val distance = kotlin.math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

        if (distance <= radius) {
            var angle = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
            if (angle < 0) angle += 360f
            angle = (angle + 90f) % 360f

            var startAngle = -90f
            var foundIndex = -1

            pieData.forEachIndexed { index, item ->
                val sweepAngle = (item.value / totalValue) * 360f
                if (angle in startAngle..(startAngle + sweepAngle)) {
                    foundIndex = index
                    return@forEachIndexed
                }
                startAngle += sweepAngle
            }

            if (foundIndex != -1) {
                selectedSlice = if (selectedSlice == foundIndex) -1 else foundIndex
                invalidate()

                onSliceClickListener?.invoke(foundIndex, pieData[foundIndex])
            }
        }
    }
}
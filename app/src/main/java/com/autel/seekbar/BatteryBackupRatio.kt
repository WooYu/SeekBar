package com.autel.seekbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import kotlin.math.abs

class BatteryBackupRatio : AppCompatSeekBar {
    private val logTag = "tag_hems_widget_BatteryBackupRatio"

    /**
     * 刻度描述数据
     */
    var tickMarkTitles = listOf<TickMarkBean>()

    /**
     * 初始进度值
     */
    private var initialProgressValue = 0

    /**
     * 滑块所处位置
     */
    private var positionOfSlider: Int = initialProgressValue


    /**
     * 最小进度值
     */
    private var minProgressValue = 0

    /**
     * 最大进度值
     */
    private var maxProgressValue = 100

    /**
     * 步长
     */
    private var stepSize = 1

    /**
     * x轴文案字体大小
     */
    private val textSizeOfXAxisDesc = dp2px(13.0f)

    /**
     * 滑块文案字体大小
     */
    private val textSizeOfSliderDesc = dp2px(17.0f)

    /**
     * x轴文案颜色
     */
    private val textColorOfXAxisDesc = Color.parseColor("#993C3C43")

    /**
     * 滑块文案颜色
     */
    private val textColorOfSliderDesc = Color.parseColor("#2DBD51")


    /**
     * 第一进度条颜色
     */
    private val firstProgressColor = Color.parseColor("#E4E4E6")

    /**
     * 第二进度跳颜色
     */
    private val secondProgressColor = Color.parseColor("#34C759")

    /**
     * 进度描述和滑块之间的间距
     */
    private var spaceOfProgressDescAndSlider = dp2px(2.5f)

    /**
     * 进度条和x轴文案左右偏移
     */
    private var sizeOfHorizontalIndent = 0f

    /**
     * 刻度线间距
     */
    private var spaceOfTickMark = 0f

    /**
     * 刻度线宽度
     */
    private var widthOfTick = 0f

    /**
     * 进度条高度
     */
    private var heightOfProgressBar: Float = dp2px(4f) * 1.0f

    /**
     * 圆角比例
     */
    private var filletRatio: Float = dp2px(2f) * 1.0f

    /**
     * 滑块尺寸
     */
    private var sizeOfSlider = dp2px(32f)

    /**
     * 第二进度条宽度
     */
    private var widthOfSecondProcess = 0f

    /**
     * 进度条的画笔
     */
    private val mPaintOfProgressBar: Paint = Paint()

    /**
     * 画文本的画笔
     */
    private val mPaintOfText: Paint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 进度条的圆角矩形
     */
    private val mRectFOfProgressBar: RectF = RectF()

    /**
     * 刻度线的圆角矩形
     */
    private val mRectFOfTickMark: RectF = RectF()

    /**
     * 路径
     */
    private val mPath = Path()

    /**
     * 刻度的数量
     */
    private var totalNumberOfTicks = 0


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val wm = MeasureSpec.getMode(widthMeasureSpec)
        val hm = MeasureSpec.getMode(heightMeasureSpec)
        val w = measuredWidth
        var h = measuredHeight

        calculateHorizontalSpace()

        //滑块文案高度+滑块和文案间距+滑块高度+刻度文案高度+上下内边距
        val maxProgressHeight = sizeOfSlider.coerceAtLeast((heightOfProgressBar * 2.5f).toInt())
        h =
            paddingTop + paddingBottom + textSizeOfSliderDesc + spaceOfProgressDescAndSlider + maxProgressHeight + textSizeOfXAxisDesc

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(w, wm), MeasureSpec.makeMeasureSpec(h, hm)
        )
    }

    override fun onDraw(canvas: Canvas?) {

        //画进度条背景
        onDrawProgress(canvas)

        //画刻度线和文案
        onDrawTickMarks(canvas)
        super.onDraw(canvas)

    }

    /**
     * 初始化
     */
    private fun init() {
        mPaintOfProgressBar.color = firstProgressColor
        mPaintOfProgressBar.style = Paint.Style.FILL
        mPaintOfProgressBar.isAntiAlias = true

        mPaintOfText.textAlign = Paint.Align.CENTER

        onChangeMaxProgressValue(max + 1)
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 绘制进度条
     */
    private fun onDrawProgress(canvas: Canvas?) {
        //绘制第一进度条
        mRectFOfProgressBar.left = (paddingLeft + sizeOfHorizontalIndent).toFloat()
        mRectFOfProgressBar.right =
            (measuredWidth - paddingRight - sizeOfHorizontalIndent).toFloat()

        var topOfProgressBar: Float =
            (paddingTop + spaceOfProgressDescAndSlider + textSizeOfSliderDesc).toFloat()
        if (sizeOfSlider > heightOfProgressBar) {
            topOfProgressBar += abs(sizeOfSlider - heightOfProgressBar) / 2
        }

        mRectFOfProgressBar.top = (measuredHeight - heightOfProgressBar) / 2 - paddingTop
        mRectFOfProgressBar.bottom = mRectFOfProgressBar.top + heightOfProgressBar
        mPaintOfProgressBar.color = firstProgressColor
        mPath.reset()
        mPath.addRoundRect(mRectFOfProgressBar, filletRatio, filletRatio, Path.Direction.CW)
        canvas?.drawPath(mPath, mPaintOfProgressBar)

        //绘制第二进度条
        widthOfSecondProcess = spaceOfTickMark * positionOfSlider

        mRectFOfProgressBar.right = mRectFOfProgressBar.left + widthOfSecondProcess
        mPaintOfProgressBar.color = secondProgressColor
        mPath.reset()
        mPath.addRoundRect(mRectFOfProgressBar, filletRatio, filletRatio, Path.Direction.CW)
        canvas?.drawPath(mPath, mPaintOfProgressBar)
    }


    /**
     * 绘制刻度线和文案
     */
    private fun onDrawTickMarks(canvas: Canvas?) {
        if (tickMarkTitles.isEmpty()) {
            return
        }

        for (i in tickMarkTitles.indices) {
            mRectFOfTickMark.left = mRectFOfProgressBar.left - widthOfTick / 2 + i * spaceOfTickMark
            mRectFOfTickMark.right = mRectFOfTickMark.left + widthOfTick

            //偶数
            val isEven = i % 2 == 0
            //长刻度高度：10/4 短刻度高度：8/2
            val heightOfTick: Float = if (isEven) {
                //长刻度
                2.5f * heightOfProgressBar
            } else {
                2f * heightOfProgressBar
            }
            mRectFOfTickMark.top =
                mRectFOfProgressBar.top - (heightOfTick - heightOfProgressBar) / 2
            mRectFOfTickMark.bottom = mRectFOfTickMark.top + heightOfTick
            //刻度颜色
            mPaintOfProgressBar.color = if (i > positionOfSlider) {
                firstProgressColor
            } else {
                secondProgressColor
            }
            //刻度圆角:首尾刻度圆角小些 1.5 2
            val scaleFillet = if (i == 0 || i == (totalNumberOfTicks - 1)) {
                0.75f * filletRatio
            } else {
                filletRatio
            }
            mPath.reset()
            mPath.addRoundRect(mRectFOfTickMark, scaleFillet, scaleFillet, Path.Direction.CW)
            canvas?.drawPath(mPath, mPaintOfProgressBar)

            //绘制刻度底部文字
            val tickMarkInfo = tickMarkTitles[i]
            onDrawTickMarkDesc(canvas, tickMarkInfo, mRectFOfTickMark.left)

            //绘制当前进度
            if (i == positionOfSlider) {
                onDrawCurProgressDesc(canvas, tickMarkInfo.desc, mRectFOfTickMark.left)
            }
        }

    }

    /**
     * 绘制刻度说明
     */
    private fun onDrawTickMarkDesc(
        canvas: Canvas?, tickMarkInfo: TickMarkBean, leftOfTickMark: Float
    ) {
        if (!tickMarkInfo.display) {
            return
        }
        val desc = tickMarkInfo.desc
        mPaintOfText.textSize = textSizeOfXAxisDesc.toFloat()
        mPaintOfText.color = textColorOfXAxisDesc
        mPaintOfText.isFakeBoldText = false
        val boundsOfTickMarkDesc = Rect()
        mPaintOfText.getTextBounds(desc, 0, desc.length, boundsOfTickMarkDesc)

        val startX = leftOfTickMark + widthOfTick / 2
        val baseLineY =
            measuredHeight - paddingBottom - textSizeOfXAxisDesc + boundsOfTickMarkDesc.height()
        canvas?.drawText(desc, startX, baseLineY.toFloat(), mPaintOfText)
    }


    /**
     * 绘制当前进度值
     */
    private fun onDrawCurProgressDesc(canvas: Canvas?, desc: String, leftOfTickMark: Float) {
        mPaintOfText.textSize = textSizeOfSliderDesc.toFloat()
        mPaintOfText.color = textColorOfSliderDesc
        mPaintOfText.isFakeBoldText = true
        val boundsOfTickMarkDesc = Rect()
        mPaintOfText.getTextBounds(desc, 0, desc.length, boundsOfTickMarkDesc)

        var startX = leftOfTickMark + widthOfTick / 2
        val baseLineY = paddingTop + boundsOfTickMarkDesc.height()

        //判断是否超过边界
        val diff = leftOfTickMark + boundsOfTickMarkDesc.width() / 2 - measuredWidth + paddingEnd
        if (diff > 0) {
            startX -= diff / 2
        }

        canvas?.drawText(desc, startX, baseLineY.toFloat(), mPaintOfText)
    }

    /**
     * 改变最大值
     */
    private fun onChangeMaxProgressValue(value: Int) {
        maxProgressValue = value
        totalNumberOfTicks = maxProgressValue
        stepSize = (maxProgressValue - minProgressValue) / totalNumberOfTicks
    }

    /**
     * 计算横向间距
     */
    private fun calculateHorizontalSpace() {
        if (totalNumberOfTicks <= 1 || 0 == measuredWidth) {
            return
        }
        val widthOfProgressWidth =
            measuredWidth - paddingLeft - paddingRight - sizeOfHorizontalIndent * 2
        spaceOfTickMark = widthOfProgressWidth * 1.0f / (totalNumberOfTicks - 1)
        //刻度线间距和刻度宽度比例 14/4= 3.5
        widthOfTick = spaceOfTickMark / 3.5f
        filletRatio = widthOfTick / 2

    }

    /**
     * 初始化刻度描述值
     */
    fun initTickMarkData(list: List<TickMarkBean>?) {
        if (null == list) {
            return
        }
        tickMarkTitles = list

        max = tickMarkTitles.size - 1
        onChangeMaxProgressValue(tickMarkTitles.size)
        calculateHorizontalSpace()
    }

    /**
     * 设置初始进度
     */
    fun setInitialProgressValue(initDesc: String?) {
        if (TextUtils.isEmpty(initDesc)) {
            return
        }

        tickMarkTitles.forEach { element ->
            if (TextUtils.equals(element.desc, initDesc)) {
                positionOfSlider = element.position
                progress = positionOfSlider
                return
            }
        }
    }

    /**
     * 更新当前进度
     */
    fun updateProgressValue(position: Int) {
        positionOfSlider = position
        progress = positionOfSlider
    }

}
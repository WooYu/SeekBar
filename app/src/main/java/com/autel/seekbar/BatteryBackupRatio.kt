package com.autel.seekbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatSeekBar

class BatteryBackupRatio : AppCompatSeekBar {
    private val logTag = "tag_hems_widget_BatteryBackupRatio"

    /**
     * 最小值
     */
    private val minValue = 0

    /**
     * 最大值
     */
    private val maxValue = 100

    /**
     * 步长
     */
    private val stepSize = 5

    /**
     * 默认值
     */
    private var initialValue = 10

    /**
     * 间隔多少步长展示x轴文案
     */
    private val xAxisDescIntervalStep = 4

    /**
     * x轴文案字体大小
     */
    private val textSizeOfXAxisDesc = dp2px(13f)

    /**
     * 滑块文案字体大小
     */
    private val textSizeOfSliderDesc = dp2px(17f)

    /**
     * x轴文案颜色
     */
    private val textColorOfXAxisDesc = Color.parseColor("#993C3C43")

    /**
     * 滑块文案颜色
     */
    private val textColorOfSliderDesc = Color.parseColor("#FF2DBD51")

    /**
     * 刻度颜色
     */
    private val textColorOfScale = Color.parseColor("#FF34C759")

    /**
     * 第一进度条颜色
     */
    private val firstProgressColor = Color.parseColor("#FF34C759")

    /**
     * 第二进度跳颜色
     */
    private val secondProgressColor = Color.parseColor("#FFE4E4E6")

    /**
     * 进度描述和滑块之间的间距
     */
    private var spaceOfProgressDescAndSlider = dp2px(2.5f)

    /**
     * 进度条和x轴文案左右偏移
     */
    private var sizeOfHorizontalIndent = dp2px(11f)

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
    private var heightOfProgressBar = 0f

    /**
     * 圆角比例
     */
    private var filletRatio = 0f

    /**
     * 滑块尺寸
     */
    private var sizeOfSlider = 0f

    /**
     * 画笔
     */
    private val mPaint: Paint = Paint()

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
     * 刻度总数
     */
    private var totalNumberOfTicks = 0;


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
        Log.d(logTag, "onMeasure() start w = $w , h = $h")

        //计算步长间隔距离
        totalNumberOfTicks = (maxValue - minValue) / stepSize
        spaceOfTickMark =
            (w - paddingLeft - paddingRight - sizeOfHorizontalIndent * 2) * 1.0f / totalNumberOfTicks
        //刻度线间距和刻度宽度比例 14/4= 3.5
        widthOfTick = spaceOfTickMark / 3.5f
        heightOfProgressBar = widthOfTick
        //滑块尺寸和刻度间距的比例 32/14 = 2.29
        sizeOfSlider = spaceOfTickMark * 2.29f
        filletRatio = widthOfTick / 2

        h =
            (textSizeOfSliderDesc + spaceOfProgressDescAndSlider + textSizeOfXAxisDesc + sizeOfSlider + paddingTop + paddingBottom).toInt()
        Log.d(
            logTag,
            "onMeasure() end : h = $h , totalNumberOfTicks = $totalNumberOfTicks , spaceOfTickMark = $spaceOfTickMark"
        )
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(w, wm), MeasureSpec.makeMeasureSpec(h, hm))
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //画背景
        onDrawFirstProgress(canvas)

        //画刻度线和文案
        onDrawTickMarks(canvas)
        //绘制已过进度、滑块位置和进度文案
    }

    /**
     * 初始化
     */
    private fun init() {
        mPaint.color = firstProgressColor
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 绘制进度条
     */
    private fun onDrawFirstProgress(canvas: Canvas?) {
        //绘制第一进度条
        mRectFOfProgressBar.left = (paddingLeft + sizeOfHorizontalIndent).toFloat()
        mRectFOfProgressBar.right = (width - paddingRight - sizeOfHorizontalIndent).toFloat()
        mRectFOfProgressBar.top =
            (height - paddingTop - textSizeOfSliderDesc - spaceOfProgressDescAndSlider).toFloat()
        mRectFOfProgressBar.bottom = mRectFOfProgressBar.top + sizeOfSlider
        mPaint.color = firstProgressColor
        mPaint.strokeWidth = heightOfProgressBar
        mPath.reset()
        mPath.addRoundRect(mRectFOfProgressBar, filletRatio, filletRatio, Path.Direction.CW)
        canvas?.drawPath(mPath, mPaint)

        //绘制第二进度条
        val widthOfSecondProgressBar =
            (mRectFOfProgressBar.right - mRectFOfProgressBar.left) * initialValue / (maxValue - minValue)
        mRectFOfProgressBar.right = widthOfSecondProgressBar
        mPaint.color = secondProgressColor
        mPath.reset()
        mPath.addRoundRect(mRectFOfProgressBar, filletRatio, filletRatio, Path.Direction.CW)
        canvas?.drawPath(mPath, mPaint)
    }


    /**
     * 绘制刻度线和文案
     */
    private fun onDrawTickMarks(canvas: Canvas?) {
        if (totalNumberOfTicks <= 0) {
            return
        }
        for (i in 1..totalNumberOfTicks) {
            //奇数位
//            val isOdd = (i % 2 == 1)
//            mRectF.left = (paddingLeft + sizeOfHorizontalIndent).toFloat()
//            mRectF.right = (width - paddingRight - sizeOfHorizontalIndent).toFloat()
//            mRectF.top =
//                (height - paddingTop - textSizeOfSliderDesc - spaceOfProgressDescAndSlider).toFloat()
//            mRectF.bottom = mRectF.top + sizeOfSlider
//            mPaint.color = firstProgressColor
//            mPaint.strokeWidth = heightOfProgressBar
//            mPath.reset()
//            mPath.addRoundRect(mRectF, filletRatio, filletRatio, Path.Direction.CW)
//            canvas?.drawPath(mPath, mPaint)
        }

    }

}
package wang.xuxiao.demoforclock.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

/**
 * @time 20180921
 * @author xuxiao.wang
 * */
class ClockView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG) // 创建一个抗锯齿的画笔

    private var canvas_w = 10
    private var canvas_h = 10

    private var radius = 0f         // 圆半径
    private var circle_x = 0f       // 圆心横坐标
    private var circle_y = 0f       // 圆心纵坐标
    private var rem = 0             // 尺寸常量，根据画板大小计算

    init {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMeasure = measureSize(10, widthMeasureSpec)
        val heightMeasure = measureSize(10, heightMeasureSpec)

        // 保证view为能显示所有内容的最小正方形
        val measureSize = if (widthMeasure > heightMeasure) heightMeasure else widthMeasure

        setMeasuredDimension(measureSize, measureSize)

        // 确定圆盘半径
        radius = (if (width > height) height else width) / 2f - 1.5f

        //确定canvas的大小，可根据此值确定圆心坐标
        canvas_w = width
        canvas_h = height

        circle_x = canvas_w / 2f
        circle_y = canvas_h / 2f

        rem = measureSize / 200
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // 绘图
        // 画表盘
        paint.color = Color.WHITE // 表盘颜色
        paint.style = Paint.Style.FILL // 表盘填充
        canvas.drawCircle(circle_x, circle_y, radius, paint)

        // 画表盘圆环
        paint.color = Color.BLACK // 表盘颜色
        paint.style = Paint.Style.STROKE // 表盘不填充
        paint.strokeWidth = 1.5f // 表盘宽度
        canvas.drawCircle(circle_x, circle_y, radius, paint)

        // 画六十个点
        paint.style = Paint.Style.FILL // 刻度填充
        for (i in 0 until 60) {
            var rad = 2 * Math.PI / 60f * i
            var x: Float = (Math.cos(rad) * (radius - 12f * rem)).toFloat() + circle_x
            var y: Float = (Math.sin(rad) * (radius - 12f * rem)).toFloat() + circle_y
            if (i % 5 === 0) {
                paint.color = Color.BLACK
            } else {
                paint.color = Color.parseColor("#cccccc")
            }
            canvas.drawCircle(x, y, 2f * rem, paint)
        }

        // 画文字
        var numbers = arrayOf("3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "1", "2")
        paint.color = Color.BLACK
        paint.textSize = 13f * rem
        paint.textAlign = Paint.Align.CENTER
        for (i in numbers.indices) {
            var rad = 2 * Math.PI / 12 * i//弧度f
            var x: Float = (Math.cos(rad) * (radius - 26 * rem)).toFloat() + circle_x//x坐标
            var y: Float = (Math.sin(rad) * (radius - 26 * rem)).toFloat() + circle_y//y坐标
            canvas.drawText(numbers[i], x, y + 4f * rem, paint) // 文本基线啊，真不想弄
        }

        // 得到当前时间时分秒
        var hour = time()[0]
        var minute = time()[1]
        var second = time()[2]

        // 画时针
        paint.color = Color.BLACK
        drawHour(canvas, hour, minute)

        // 画分针
        drawMinute(canvas, minute, second)

        // 画秒针
        paint.color = Color.RED
        drawSecond(canvas, second)

        // 画中心圆点
        paint.color = Color.WHITE
        canvas.drawCircle(circle_x, circle_y, 3f * rem, paint)

        postInvalidateDelayed(1000) // 每秒刷新一次
    }

    private fun measureSize(size: Int, measureSpec: Int): Int {
        var result = size
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        when (specMode) {
            View.MeasureSpec.UNSPECIFIED -> result = size
            View.MeasureSpec.AT_MOST -> result = if (resources.displayMetrics.widthPixels > resources.displayMetrics.heightPixels) resources.displayMetrics.heightPixels / 2 else resources.displayMetrics.widthPixels / 2 // 设为 wrap_content 的大小
            View.MeasureSpec.EXACTLY -> result = specSize
        }
        return result
    }

    /**
     * 画时针
     * */
    private fun drawHour(canvas: Canvas, hour: Int, minute: Int) {

        var hrad = 360f / 12 * hour//计算角度
        var mrad = 360f / 12 / 60 * minute

        paint.strokeWidth = 6f * rem // 定义时钟线宽度
        paint.strokeCap = Paint.Cap.ROUND // 线条末端圆形

        canvas.save()
        canvas.rotate(hrad + mrad, circle_x, circle_y)
        canvas.drawLine(circle_x, 45f * rem, circle_y, height / 2f + 8 * rem, paint)
        canvas.restore()
    }

    /**
     * 画分针
     * */
    private fun drawMinute(canvas: Canvas, minute: Int, second: Int) {
        var mrad = 360f / 60 * minute // 分钟角度
//        var srad = 360f/ 60 / 60 * second // 秒针角度

        paint.strokeWidth = 3.5f * rem // 定义时钟线宽度
        paint.strokeCap = Paint.Cap.ROUND // 线条末端圆形

        canvas.save()
        canvas.rotate(mrad, circle_x, circle_y)
        canvas.drawLine(circle_x, 20f * rem, circle_y, height / 2f + 8 * rem, paint)
        canvas.restore()
    }

    private fun drawSecond(canvas: Canvas, second: Int) {
        var srad = 360f / 60 * second // 秒针角度

        paint.strokeWidth = 2f * rem // 定义时钟线宽度
        paint.strokeCap = Paint.Cap.ROUND // 线条末端圆形
        paint.color = Color.RED

        canvas.save()
        canvas.rotate(srad, circle_x, circle_y)
        canvas.drawLine(circle_x, 8f * rem, circle_y, height / 2f + 14 * rem, paint)
        canvas.restore()
    }


    /**
     * 获取当前时分秒
     * */
    private fun time(): Array<Int> {
        var calendar: Calendar = Calendar.getInstance()
        return arrayOf(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND))
    }

}
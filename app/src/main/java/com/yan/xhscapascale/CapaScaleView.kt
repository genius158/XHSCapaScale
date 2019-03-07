package com.yan.xhscapascale

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class CapaScaleView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    var onViewRemoved: (() -> Unit)? = null
    var onTouchEnd: (() -> Unit)? = null
    var onTouchStart: (() -> Unit)? = null

    var eventListener: EventListener? = null
    /**
     * 0.不处理事件
     * 1.放大位移模式
     * 2.位移模式
     */
    private var eventModel = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            return true
        }

        // 两个手指触摸 才做处理事件
        if (eventModel == 0 && event.pointerCount == 2) {
            eventModel = 1
            parent?.requestDisallowInterceptTouchEvent(true)
            onTouchStart?.invoke()
        }

        if (eventModel == 0) {
            return super.onTouchEvent(event)
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_UP -> {
                eventModel = 2
                point1 = null
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (eventModel == 2) {
                    point1 = null
                }
            }
            MotionEvent.ACTION_MOVE -> moveDell(event)
        }

        if (event.actionMasked == MotionEvent.ACTION_CANCEL
            || event.actionMasked == MotionEvent.ACTION_UP
        ) {
            eventModel = 0
            point1 = null
            point2 = null
            onTouchEnd?.invoke()
        }

        return super.onTouchEvent(event)
    }

    private var point1: PointF? = null
    private var point2: PointF? = null

    private fun moveDell(event: MotionEvent) {
        if (eventModel == 1) {
            if (event.pointerCount < 2) {
                return
            }
            if (point1 == null) {
                point1 = PointF(event.getX(0), event.getY(0))
            }
            if (point2 == null) {
                point2 = PointF(event.getX(1), event.getY(1))
            }
            val curX1 = event.getX(0)
            val curY1 = event.getY(0)

            val curX2 = event.getX(1)
            val curY2 = event.getY(1)

            val originalDistance = Math.sqrt(
                ((point2!!.x - point1!!.x) * (point2!!.x - point1!!.x)).toDouble()
                        + ((point2!!.y - point1!!.y) * (point2!!.y - point1!!.y)).toDouble()
            )
            val curDistance = Math.sqrt(
                ((curX2 - curX1) * (curX2 - curX1)).toDouble()
                        + ((curY2 - curY1) * (curY2 - curY1)).toDouble()
            )
            val scale = curDistance / originalDistance

            val dx = curX1 - point1!!.x
            val dy = curY1 - point1!!.y

            Log.e(javaClass.name, "dx: $dx    dy: $dy   scale: $scale")

            eventListener?.onEvent(dx, dy, scale)

            point1!!.set(event.getX(0), event.getY(0))
            point2!!.set(event.getX(1), event.getY(1))
        } else if (eventModel == 2) {
            if (point1 == null) {
                point1 = PointF(event.getX(0), event.getY(0))
            }

            val dx = event.getX(0) - point1!!.x
            val dy = event.getY(0) - point1!!.y

            Log.e(javaClass.name, "dx: $dx    dy: $dy   scale: 1")

            eventListener?.onEvent(dx, dy, 1.0)
            point1!!.set(event.getX(0), event.getY(0))
        }

    }


    interface EventListener {
        fun onEvent(dx: Float, dy: Float, scale: Double)
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onViewRemoved?.invoke()
    }
}

package com.yan.xhscapascale

import android.animation.Animator
import android.animation.ValueAnimator
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator

class MainActivity : AppCompatActivity() {

    /**
     * 最终的缩放大小
     */
    private var finalScale = 1F

    /**
     * touch 结束的 恢复动画
     */
    private var animator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv: RecyclerView = findViewById(R.id.rv)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder =
                object : RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.item_main, p0, false)) {}

            override fun getItemCount(): Int = 10

            override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
                val csv = p0.itemView.findViewById<CapaScaleView>(R.id.csv)
                val tv = p0.itemView.findViewById<View>(R.id.tv)
                tv.post {
                    onCapaSet(csv, tv)
                }
            }
        }
    }

    private fun onCapaSet(csv: CapaScaleView, view: View) {
        // cover view 存贮,方便删除添加操作
        val cover = ArrayList<View>(1)

        csv.eventListener = { dx: Float, dy: Float, scale: Double ->
            cover[0].x = cover[0].x + dx
            cover[0].y = cover[0].y + dy

            finalScale *= scale.toFloat()

            cover[0].scaleX = finalScale
            cover[0].scaleY = cover[0].scaleX
        }

        csv.onTouchStart = {
            animator?.end()
            cover.add(View(this))
            cover[0].setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            val llp = ViewGroup.MarginLayoutParams(view.layoutParams)
            cover[0].layoutParams = llp
            val xy = IntArray(2)
            view.getLocationInWindow(xy)
            cover[0].x = xy[0].toFloat()
            cover[0].y = xy[1].toFloat()

            (window.decorView as ViewGroup).addView(cover[0])
            view.visibility = View.GONE
        }

        csv.onTouchEnd = {
            val c = (if (cover.size > 0) cover[0] else null)
            if (c != null) {
                if (animator == null) {
                    animator = ValueAnimator.ofFloat(1F, 0F)
                }
                val tempAM = animator!!
                tempAM.duration = 250

                val xy = IntArray(2)
                view.getLocationInWindow(xy)

                val viewX = c.x
                val viewY = c.y
                tempAM.interpolator = OvershootInterpolator(0.5F)
                tempAM.addUpdateListener { animation ->
                    val value: Float = animation.animatedValue as Float
                    val tempOffsetScale = (finalScale - 1) * value + 1
                    c.scaleX = tempOffsetScale
                    c.scaleY = c.scaleX

                    c.x = (viewX - xy[0]) * value + xy[0]
                    c.y = (viewY - xy[1]) * value + xy[1]
                }

                tempAM.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        view.visibility = View.VISIBLE
                        finalScale = 1F
                        (window.decorView as ViewGroup).removeView(if (cover.size > 0) cover[0] else null)
                        cover.clear()

                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })
                tempAM.start()
            }
        }

        csv.onViewRemoved = {
            animator?.end()
        }

    }
}

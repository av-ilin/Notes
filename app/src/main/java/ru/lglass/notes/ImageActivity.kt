package ru.lglass.notes

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GestureDetectorCompat
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ImageActivity : AppCompatActivity() {
    private val images = mutableListOf<Bitmap>()
    private var position = 0
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var swipeGestureDetector: GestureDetectorCompat

    private class ScaleListener(private val ivImage: ImageView) : SimpleOnScaleGestureListener() {
        public var mScaleFactor = 1.0f

        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = max(1f, min(mScaleFactor, 10.0f))
            ivImage.scaleX = mScaleFactor
            ivImage.scaleY = mScaleFactor
            return true
        }
        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            if (mScaleFactor == 1f){
                ivImage.pivotX = detector!!.focusX - ivImage.left
                ivImage.pivotY = detector.focusY - ivImage.top
            }
            return true
        }
    }

    private class SwipeListener(private val ivImage: ImageView,
                                private val images: MutableList<Bitmap>,
                                private var position: Int,
                                private val scaleListener: ScaleListener)
        : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?) = true
        override fun onFling(e1: MotionEvent?,e2: MotionEvent?,
                             velocityX: Float, velocityY: Float): Boolean {
            if (scaleListener.mScaleFactor == 1f && abs(e1!!.y - e2!!.y) < 200){
                if (e1.x - e2.x > 100){
                    if (position + 1 < images.size)
                        position += 1
                    else
                        position = 0
                    ivImage.setImageBitmap(images[position])
                }
                if (e1.x - e2.x < -100){
                    if (position - 1 > -1)
                        position -= 1
                    else
                        position = images.size - 1
                    ivImage.setImageBitmap(images[position])
                }
                ivImage.pivotX = ivImage.width/2f
                ivImage.pivotY = ivImage.height/2f
            }
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            if (scaleListener.mScaleFactor != 1f){
                scaleListener.mScaleFactor = 1f
                ivImage.scaleX = 1f
                ivImage.scaleY = 1f
                ivImage.pivotY = 0f
                ivImage.pivotX = 0f
            }
            else{
                scaleListener.mScaleFactor = 2f
                ivImage.scaleX = scaleListener.mScaleFactor
                ivImage.scaleY = scaleListener.mScaleFactor
                ivImage.pivotX = e!!.x- ivImage.left
                ivImage.pivotY = e.y - ivImage.top
            }
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?,
                              distanceX: Float, distanceY: Float): Boolean {
            val speed = 1.5f
            val pts = FloatArray(2) { 0f }
            ivImage.imageMatrix.mapPoints(pts)

            if (scaleListener.mScaleFactor != 1f){
                if (ivImage.pivotX + distanceX > pts[0]
                    && ivImage.pivotX + distanceX <= ivImage.width - pts[0])
                    ivImage.pivotX += distanceX / scaleListener.mScaleFactor * speed

                if (ivImage.pivotY + distanceY > pts[1]
                    && ivImage.pivotY + distanceY <= ivImage.height - pts[1])
                    ivImage.pivotY += distanceY/scaleListener.mScaleFactor * speed
            }
            return true
        }
    }

    private fun fillImages(){
        val img = intent.getStringExtra("Images")!!.split("|")
        for (pathImg in img){
            val bMap = if (File(pathImg).exists())
                BitmapFactory.decodeFile(pathImg)
            else
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_not_interested_24)!!.toBitmap()
            images.add(bMap)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        swipeGestureDetector.onTouchEvent(event!!)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        position = intent.getIntExtra("Current",0)
        fillImages()
        ivImage.setImageBitmap(images[position])

        val scaleListener = ScaleListener(ivImage)
        scaleGestureDetector = ScaleGestureDetector(this, scaleListener)
        swipeGestureDetector = GestureDetectorCompat(this, SwipeListener(ivImage, images, position, scaleListener))

        btCancelImage.setOnClickListener { finish() }
    }
}
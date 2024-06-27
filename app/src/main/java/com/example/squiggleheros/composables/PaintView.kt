package com.example.squiggleheros.composables

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

class PaintView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    var brushColor: Int = Color.BLACK,
    var brushSize: Float = 8f,
    var eraserSize: Float = 8f
) : View(context, attrs, defStyleAttr) {

    private val pathList = ArrayList<Path>()
    private val colorList = ArrayList<Int>()
    private val sizeList = ArrayList<Float>()

    // Set the background color (white in this case)
    private var backgroundColor = Color.WHITE
    var isEraserActive = false
    var onDrawingChange: (() -> Unit)? = null
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val newPath = Path()
                newPath.moveTo(x, y)
                pathList.add(newPath)
                onDrawingChange?.invoke()
                // Set the color explicitly based on the eraser state
                val currentColor = if (isEraserActive) backgroundColor else brushColor
                colorList.add(currentColor)
                sizeList.add(if (isEraserActive) eraserSize else brushSize)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val currentPath = pathList.lastOrNull()
                currentPath?.lineTo(x, y)
                onDrawingChange?.invoke()
            }
            else -> return false
        }
        postInvalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        // Fill the entire canvas with the background color
        canvas.drawColor(backgroundColor)

        for (i in pathList.indices) {
            val currentPath = pathList[i]
            val currentColor = colorList[i]
            val currentSize = sizeList[i]

            val paint = Paint().apply {
                isAntiAlias = true
                color = currentColor
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeWidth = currentSize
            }

            canvas.drawPath(currentPath, paint)
        }
    }

    // Method to change the background color
    override fun setBackgroundColor(newColor: Int) {
        backgroundColor = newColor
        invalidate()
    }
    fun getBitmap(): Bitmap {
        // Create a bitmap with the same dimensions as the view
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        // Create a canvas to draw on the bitmap
        val canvas = Canvas(bitmap)
        // Draw the view's background
        canvas.drawColor(backgroundColor)
        // Draw the paths
        for (i in pathList.indices) {
            val currentPath = pathList[i]
            val currentColor = colorList[i]
            val currentSize = sizeList[i]

            val paint = Paint().apply {
                isAntiAlias = true
                color = currentColor
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeWidth = currentSize
            }

            canvas.drawPath(currentPath, paint)
        }
        return bitmap
    }
}
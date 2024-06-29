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

    private val pathList = ArrayList<Pair<Path, Boolean>>() // Pair of Path and isEraser
    private val colorList = ArrayList<Int>()
    private val sizeList = ArrayList<Float>()

    // Stack to keep track of undone paths for possible redo functionality
    private val undonePathList = ArrayList<Pair<Path, Boolean>>()
    private val undoneColorList = ArrayList<Int>()
    private val undoneSizeList = ArrayList<Float>()

    private var backgroundBitmap: Bitmap? = null

    private var backgroundColor = Color.WHITE
    var isEraserActive = false
    var onDrawingChange: (() -> Unit)? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val newPath = Path().apply {
                    moveTo(x, y)
                }
                pathList.add(Pair(newPath, isEraserActive))

                // Clear the undone path stack when a new path is added
                undonePathList.clear()
                undoneColorList.clear()
                undoneSizeList.clear()

                colorList.add(if (isEraserActive) backgroundColor else brushColor)
                sizeList.add(if (isEraserActive) eraserSize else brushSize)
                onDrawingChange?.invoke()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                pathList.lastOrNull()?.first?.lineTo(x, y)
                onDrawingChange?.invoke()
            }
            else -> return false
        }
        postInvalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        //canvas.drawColor(backgroundColor)

        // Draw the background bitmap if available, otherwise fill the entire canvas with the background color
        backgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        } ?: run {
            canvas.drawColor(backgroundColor)
        }

        for (i in pathList.indices) {
            val (currentPath, isEraser) = pathList[i]
            val currentColor = colorList[i]
            val currentSize = sizeList[i]

            val paint = Paint().apply {
                isAntiAlias = true
                color = currentColor
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeWidth = currentSize
            }

            if (isEraser) {
                paint.color = backgroundColor
            }

            canvas.drawPath(currentPath, paint)
        }
    }

    override fun setBackgroundColor(newColor: Int) {
        backgroundColor = newColor
        backgroundBitmap = null
        invalidate()
    }


    fun loadBitmap(bitmap: Bitmap) {
        backgroundBitmap = bitmap
        invalidate()
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        //canvas.drawColor(backgroundColor)
        backgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        } ?: run {
            canvas.drawColor(backgroundColor)
        }

        for (i in pathList.indices) {
            val (currentPath, isEraser) = pathList[i]
            val currentColor = colorList[i]
            val currentSize = sizeList[i]

            val paint = Paint().apply {
                isAntiAlias = true
                color = currentColor
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeWidth = currentSize
            }

            if (isEraser) {
                paint.color = backgroundColor
            }

            canvas.drawPath(currentPath, paint)
        }

        return bitmap
    }


    // Method to undo the last path
    fun undo() {
        if (pathList.isNotEmpty()) {
            undonePathList.add(pathList.removeAt(pathList.size - 1))
            undoneColorList.add(colorList.removeAt(colorList.size - 1))
            undoneSizeList.add(sizeList.removeAt(sizeList.size - 1))
            onDrawingChange?.invoke()
            invalidate()
        }
    }

    // Optional: Method to redo the last undone path
    fun redo() {
        if (undonePathList.isNotEmpty()) {
            pathList.add(undonePathList.removeAt(undonePathList.size - 1))
            colorList.add(undoneColorList.removeAt(undoneColorList.size - 1))
            sizeList.add(undoneSizeList.removeAt(undoneSizeList.size - 1))
            onDrawingChange?.invoke()
            invalidate()
        }
    }
}

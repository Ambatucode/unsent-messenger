package com.unsent.messenger.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object MediaStorageHelper {

    private const val TAG = "MediaStorageHelper"

    fun saveBitmap(context: Context, bitmap: Bitmap): String? {
        return try {
            val mediaDir = File(context.filesDir, "saved_media")
            if (!mediaDir.exists()) {
                mediaDir.mkdirs()
            }

            val fileName = "img_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            val file = File(mediaDir, fileName)

            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
            }

            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save bitmap to file", e)
            null
        }
    }

    fun iconToBitmap(context: Context, icon: Icon?): Bitmap? {
        if (icon == null) return null
        return try {
            val drawable = icon.loadDrawable(context) ?: return null
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth.coerceAtLeast(1),
                drawable.intrinsicHeight.coerceAtLeast(1),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Failed to convert Icon to Bitmap", e)
            null
        }
    }

    fun createSampleTestBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.parseColor("#0084FF")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, 400f, 300f, paint)

        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("📸 Sample Unsent Photo", 200f, 150f, textPaint)
        return bitmap
    }
}

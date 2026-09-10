package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.webkit.WebView
import java.io.InputStream

object ImageUtils {

    fun uriToBitmap(context: Context, uri: Uri, maxDimension: Int = 1024): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val original = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (original != null) {
                scaleDown(original, maxDimension)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun captureWebView(webView: WebView, maxDimension: Int = 1024): Bitmap? {
        return try {
            val width = webView.width
            val height = webView.height
            if (width <= 0 || height <= 0) return null

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            webView.draw(canvas)
            scaleDown(bitmap, maxDimension)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun scaleDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (ratio > 1) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    fun createSampleBitmap(type: String): Bitmap {
        val bmp = Bitmap.createBitmap(600, 450, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        val paint = Paint().apply { isAntiAlias = true }

        when (type) {
            "sample_landmark" -> {
                // Sky & Eiffel Tower illustration
                paint.color = Color.parseColor("#4A90E2")
                canvas.drawRect(0f, 0f, 600f, 450f, paint)

                paint.color = Color.parseColor("#7ED321")
                canvas.drawRect(0f, 350f, 600f, 450f, paint)

                paint.color = Color.parseColor("#505050")
                paint.strokeWidth = 14f
                canvas.drawLine(300f, 80f, 220f, 350f, paint)
                canvas.drawLine(300f, 80f, 380f, 350f, paint)
                canvas.drawLine(240f, 250f, 360f, 250f, paint)
                canvas.drawLine(260f, 170f, 340f, 170f, paint)

                paint.color = Color.WHITE
                paint.textSize = 28f
                canvas.drawText("Paris Landmark • Eiffel Tower", 120f, 50f, paint)
            }
            "sample_device" -> {
                // Smartphone product
                paint.color = Color.parseColor("#1E293B")
                canvas.drawRect(0f, 0f, 600f, 450f, paint)

                paint.color = Color.parseColor("#334155")
                canvas.drawRoundRect(190f, 60f, 410f, 390f, 36f, 36f, paint)

                paint.color = Color.parseColor("#0F172A")
                canvas.drawRoundRect(202f, 72f, 398f, 378f, 28f, 28f, paint)

                // Camera bar
                paint.color = Color.parseColor("#475569")
                canvas.drawRoundRect(210f, 90f, 390f, 140f, 20f, 20f, paint)

                paint.color = Color.WHITE
                paint.textSize = 26f
                canvas.drawText("Google Pixel 9 Pro • Smartphone", 110f, 425f, paint)
            }
            "sample_nature" -> {
                // Monstera Plant
                paint.color = Color.parseColor("#ECFDF5")
                canvas.drawRect(0f, 0f, 600f, 450f, paint)

                paint.color = Color.parseColor("#059669")
                canvas.drawCircle(300f, 200f, 110f, paint)

                paint.color = Color.parseColor("#10B981")
                canvas.drawCircle(220f, 230f, 80f, paint)
                canvas.drawCircle(380f, 230f, 80f, paint)

                paint.color = Color.parseColor("#B45309")
                canvas.drawRect(240f, 300f, 360f, 400f, paint)

                paint.color = Color.parseColor("#065F46")
                paint.textSize = 26f
                canvas.drawText("Monstera Deliciosa • Swiss Cheese Plant", 60f, 50f, paint)
            }
            "sample_text" -> {
                // Document / Receipt
                paint.color = Color.parseColor("#F8FAFC")
                canvas.drawRect(0f, 0f, 600f, 450f, paint)

                paint.color = Color.parseColor("#0F172A")
                paint.textSize = 22f
                canvas.drawText("COFFEE & BAKERY RECEIPT", 150f, 80f, paint)

                paint.textSize = 18f
                paint.color = Color.parseColor("#334155")
                canvas.drawText("1x Oat Milk Flat White         $4.80", 110f, 140f, paint)
                canvas.drawText("1x Almond Croissant           $4.50", 110f, 180f, paint)
                canvas.drawText("Subtotal                      $9.30", 110f, 240f, paint)
                canvas.drawText("Tax                           $0.70", 110f, 270f, paint)

                paint.textSize = 20f
                paint.color = Color.parseColor("#0F172A")
                canvas.drawText("TOTAL AMOUNT                 $10.00", 110f, 320f, paint)
                canvas.drawText("WiFi Password: CoffeeGlobe2026", 110f, 380f, paint)
            }
            else -> {
                paint.color = Color.parseColor("#2563EB")
                canvas.drawRect(0f, 0f, 600f, 450f, paint)
                paint.color = Color.WHITE
                paint.textSize = 32f
                canvas.drawText("Globe Visual Search", 160f, 230f, paint)
            }
        }

        return bmp
    }
}

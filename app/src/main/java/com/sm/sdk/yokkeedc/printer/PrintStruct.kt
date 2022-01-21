package com.sm.sdk.yokkeedc.printer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import com.sm.sdk.yokkeedc.MtiApplication
import java.io.File
import java.io.FileOutputStream

class PrintStruct {
//    fun view2Bitmap(view: ViewGroup, fileName: String): Bitmap {
//        val displayMetrics = MtiApplication.context.resources.displayMetrics
//        val whiteColor = Color.parseColor("#FFFFFF")
//        view.setBackgroundColor(whiteColor)
//        view.measuredView(displayMetrics.widthPixels, displayMetrics.heightPixels)
//        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)
//        val canvas = Canvas(bitmap)
//        view.draw(canvas)
//        try {
//            val file = File(Environment.getExternalStorageDirectory(), "$fileName.png")
//            val exists = file.exists()
//            if ( ! exists ) {
//                file.createNewFile()
//            }
//            val fileOutputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
//            fileOutputStream.flush()
//            fileOutputStream.close()
//        } catch (e: Throwable) {
//            e.printStackTrace()
//        }
//        return bitmap
//    }

    fun View.measuredView(width: Int, height: Int) {
        layout(0, 0, width, height)
        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        measure(measuredWidth, measuredHeight)
        layout(0, 0, this.measuredWidth, this.measuredHeight)
    }

}
package com.example.runningapp.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.ByteArrayOutputStream

@TypeConverters
class Converters {

    @TypeConverter
    fun bitmapToByteArray(btm: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        btm.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun byteArrayToBitmap(byteArr: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size)
    }

}
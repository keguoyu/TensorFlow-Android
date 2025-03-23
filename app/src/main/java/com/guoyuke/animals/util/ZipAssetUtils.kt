package com.guoyuke.animals.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ZipAssetUtils {
    fun loadImagesFromZip(context: Context, zipPath: String): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()
        val options = BitmapFactory.Options().apply { inSampleSize = 2 }
        try {
            val inputStream = context.assets.open(zipPath)
            val zipInputStream = ZipInputStream(inputStream)
            var entry: ZipEntry? = zipInputStream.nextEntry

            while (entry != null) {
                if (!entry.isDirectory && isImageFile(entry.name)) {
                    BitmapFactory.decodeStream(zipInputStream, null, options)?.let {
                        bitmaps.add(it)
                    }
                }
                entry = zipInputStream.nextEntry
            }
            zipInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmaps
    }

    private fun isImageFile(name: String): Boolean {
        return name.endsWith(".jpg", ignoreCase = true) || 
               name.endsWith(".png", ignoreCase = true) ||
                name.endsWith(".jpeg", ignoreCase = true)
    }
}
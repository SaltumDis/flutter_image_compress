package com.example.flutterimagecompress.handle.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ThumbnailUtils
import com.example.flutterimagecompress.exif.ExifKeeper
import com.example.flutterimagecompress.ext.calcScale
import com.example.flutterimagecompress.ext.compress
import com.example.flutterimagecompress.ext.rotate
import com.example.flutterimagecompress.handle.FormatHandler
import com.example.flutterimagecompress.logger.log
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class CommonHandler(override val type: Int) : FormatHandler {

  override val typeName: String
    get() {
      return when (type) {
        1 -> "png"
        3 -> "webp"
        else -> "jpeg"
      }
    }

  private val bitmapFormat: Bitmap.CompressFormat
    get() {
      return when (type) {
        1 -> Bitmap.CompressFormat.PNG
        3 -> Bitmap.CompressFormat.WEBP
        else -> Bitmap.CompressFormat.JPEG
      }
    }

  override fun handleByteArray(context: Context, byteArray: ByteArray, outputStream: OutputStream, width: Int, height: Int, quality: Int, rotate: Int, keepExif: Boolean, inSampleSize: Int) {
    val result = compress(byteArray, width, height, quality, rotate, inSampleSize)

    if (keepExif && bitmapFormat == Bitmap.CompressFormat.JPEG) {
      val byteArrayOutputStream = ByteArrayOutputStream()
      byteArrayOutputStream.write(result)
      val resultStream = ExifKeeper(byteArray).writeToOutputStream(
              context,
              byteArrayOutputStream
      )
      outputStream.write(resultStream.toByteArray())
    } else {
      outputStream.write(result)
    }

  }

  private fun compress(arr: ByteArray, width: Int, height: Int, quality: Int, rotate: Int = 0, inSampleSize: Int): ByteArray {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = false
    options.inPreferredConfig = Bitmap.Config.RGB_565
    options.inSampleSize = inSampleSize
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
      @Suppress("DEPRECATION")
      options.inDither = true
    }

    val bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.count(), options)
    val outputStream = ByteArrayOutputStream()

    val w = bitmap.width.toFloat()
    val h = bitmap.height.toFloat()

    if(width < w &&  height < h){
      ThumbnailUtils.extractThumbnail(bitmap, width, height)
              .rotate(rotate)
              .compress(bitmapFormat, quality, outputStream)
    }
    else {
      bitmap.rotate(rotate)
              .compress(bitmapFormat, quality, outputStream)
    }


    return outputStream.toByteArray()
  }


  override fun handleFile(context: Context, path: String, outputStream: OutputStream, width: Int, height: Int, quality: Int, rotate: Int, keepExif: Boolean, inSampleSize: Int) {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = false
    options.inPreferredConfig = Bitmap.Config.RGB_565
    options.inSampleSize = inSampleSize
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
      @Suppress("DEPRECATION")
      options.inDither = true
    }
    val bitmap = BitmapFactory.decodeFile(path, options)

    val array = bitmap.compress(width, height, quality, rotate, type)

    if (keepExif && bitmapFormat == Bitmap.CompressFormat.JPEG) {
      val byteArrayOutputStream = ByteArrayOutputStream()
      byteArrayOutputStream.write(array)
      val tmpOutputStream = ExifKeeper(path).writeToOutputStream(
              context,
              byteArrayOutputStream
      )
      outputStream.write(tmpOutputStream.toByteArray())
    } else {
      outputStream.write(array)
    }
  }
}
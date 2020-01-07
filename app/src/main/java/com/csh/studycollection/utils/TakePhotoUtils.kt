package com.oceanus.cameralibrary.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File


/**
 * created by shenghuiche on 2019/2/21
 */
object TakePhotoUtils {

    fun openSystCamera(context: Activity, filePath: String?, resultCode: Int) {
        if (Build.VERSION.SDK_INT >= 24) {
            val takeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoUri = get24MediaFileUri(context, filePath)
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            context.startActivityForResult(takeIntent, resultCode)
        } else {
            val takeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoUri = getMediaFileUri(filePath)
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            context.startActivityForResult(takeIntent, resultCode)
        }
    }

    private fun getMediaFileUri(filePath: String?): Uri? {
        val mediaFile = File(filePath)
        if (!mediaFile.exists()) {
            mediaFile.createNewFile()
        }
        return Uri.fromFile(mediaFile)
    }

    /**
     * 版本24以上
     */
    private fun get24MediaFileUri(context: Context, filePath: String?): Uri? {
        val mediaFile = File(filePath)
        if (!mediaFile.exists()) {
            mediaFile.createNewFile()
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileProvider", mediaFile)
    }

    fun getPhotoUri(context: Context, filePath: String?): Uri? {
        if (Build.VERSION.SDK_INT >= 24) {
            return get24MediaFileUri(context, filePath)
        } else {
            return getMediaFileUri(filePath)
        }
    }

    fun getOrientation(filepath: String): Int {
        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(filepath)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        var degree = 0
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                degree = 90
            }
            ExifInterface.ORIENTATION_ROTATE_180 -> {
                degree = 180
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> {
                degree = 270
            }
        }
        return degree
    }

}
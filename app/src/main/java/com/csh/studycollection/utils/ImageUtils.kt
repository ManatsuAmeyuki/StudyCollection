package com.oceanus.cameralibrary.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.view.View
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream


/**
 * created by shenghuiche on 2018/12/29
 */
object ImageUtils {

    /**
     * 获取图片大小
     *
     * @param pathName
     * @param pathName
     * @return image size
     */
    fun getImageSizeBound(pathName: String): IntArray {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = 1
        BitmapFactory.decodeFile(pathName, options)
        val wh = IntArray(2)
        wh[0] = options.outWidth
        wh[1] = options.outHeight
        return wh
    }

    fun getBitmapSize(data: ByteArray): IntArray {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(data, 0, data.size, options)
        val wh = IntArray(2)
        wh[0] = options.outWidth
        wh[1] = options.outHeight
        return wh
    }

    fun scalBmpByView(showView: View, wh: IntArray): Int {
        val bitmapWidth = wh[0]
        val bitmapHeight = wh[1]

        if (bitmapWidth > 0 && bitmapHeight > 0) {
            var viewWidth = showView.getWidth() / 2
            var viewHeight = showView.getHeight() / 2

            if (viewHeight == 0 || viewWidth == 0) {
                viewWidth = 1080
                viewHeight = 1920
            }
            if (viewWidth > 0 && viewHeight > 0) {
                val scaleX = 1.0f * viewWidth / bitmapWidth
                val scaleY = 1.0f * viewHeight / bitmapHeight
                val scale = if (scaleX > scaleY) scaleY else scaleX
                var inSampleSize = (1 / scale).toInt()
                if (inSampleSize == 0) {
                    inSampleSize = 1
                }
                return inSampleSize
            }
            return 1;
        }
        return 1;
    }

    fun rotateYUV420Degree90(data: ByteArray, imageWidth: Int, imageHeight: Int): ByteArray {
        val yuv = ByteArray(imageWidth * imageHeight * 3 / 2)
        // Rotate the Y luma
        var i = 0
        for (x in 0 until imageWidth) {
            for (y in imageHeight - 1 downTo 0) {
                yuv[i] = data[y * imageWidth + x]
                i++
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1
        var x = imageWidth - 1
        while (x > 0) {
            for (y in 0 until imageHeight / 2) {
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + x]
                i--
                yuv[i] = data[imageWidth * imageHeight + y * imageWidth + (x - 1)]
                i--
            }
            x = x - 2
        }
        return yuv
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return
     */
    fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap {
        // 旋转图片 动作
        val matrix = Matrix()
        matrix.reset()
        matrix.postRotate(angle.toFloat())
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * 保存图片
     *
     * @param src
     */
    fun saveBitmap2File(src: Bitmap?, outPutFile: File?) {
        if (outPutFile == null) {
            return
        }
        var outPutStream: FileOutputStream? = null
        try {
            outPutStream = FileOutputStream(outPutFile)
            src!!.compress(Bitmap.CompressFormat.JPEG, 100, outPutStream)
            outPutStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (outPutStream != null) {
                try {
                    outPutStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }


    /**
     * 根据byte数组生成文件
     *
     * @param bytes
     * 生成文件用到的byte数组
     */
    fun createFileWithByte(bytes: ByteArray, outPutFile: File) {
        var outputStream: FileOutputStream? = null
        var bufferedOutputStream: BufferedOutputStream? = null
        try {
            // 获取FileOutputStream对象
            outputStream = FileOutputStream(outPutFile)
            // 获取BufferedOutputStream对象
            bufferedOutputStream = BufferedOutputStream(outputStream)
            // 往文件所在的缓冲输出流中写byte数据
            bufferedOutputStream.write(bytes)
            // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
            bufferedOutputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close()
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }
            }
        }
    }

    fun sampleSizeCompress(srcFilePath: String): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(srcFilePath, options)
        val bitmapWidth = options.outWidth
        val bitmapHeight = options.outHeight
        if (bitmapWidth > 0 && bitmapHeight > 0) {
//            var viewWidth = cIv.width / 2
//            var viewHeight = cIv.height / 2
//            if (viewHeight == 0 || viewWidth == 0) {
            val viewWidth = 1080
            val viewHeight = 1920
//            }
//            if (viewWidth > 0 && viewHeight > 0) {
            val scaleX = 1.0f * viewWidth / bitmapWidth
            val scaleY = 1.0f * viewHeight / bitmapHeight
            val scale = if (scaleX > scaleY) scaleY else scaleX
            var inSampleSize = (1 / scale).toInt()
            if (inSampleSize == 0) {
                inSampleSize = 1
            }
            if (inSampleSize >= 2) {
                inSampleSize = 2
            }
            options.inSampleSize = inSampleSize
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            options.inMutable = true;
            return BitmapFactory.decodeFile(srcFilePath, options)
//            }
        }
        return null
    }

}
package com.example.camerasimpleapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.media.Image
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import java.nio.ByteBuffer


class YUVtoRGB {
    fun translateYUV(image: Image, context: Context?): Bitmap {
        val yuvBytes: ByteBuffer = imageToByteBuffer(image)

        // Convert YUV to RGB
        val rs = RenderScript.create(context)
        val bitmap =
            Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888)
        val allocationRgb = Allocation.createFromBitmap(rs, bitmap)
        val allocationYuv = Allocation.createSized(rs, Element.U8(rs), yuvBytes.array().size)
        allocationYuv.copyFrom(yuvBytes.array())
        val scriptYuvToRgb = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))
        scriptYuvToRgb.setInput(allocationYuv)
        scriptYuvToRgb.forEach(allocationRgb)
        allocationRgb.copyTo(bitmap)

        // Release
        allocationYuv.destroy()
        allocationRgb.destroy()
        rs.destroy()
        return bitmap
    }

    private fun imageToByteBuffer(image: Image): ByteBuffer {
        val crop: Rect = image.getCropRect()
        val width: Int = crop.width()
        val height: Int = crop.height()
        val planes: Array<Image.Plane> = image.getPlanes()
        val rowData = ByteArray(planes[0].getRowStride())
        val bufferSize = width * height * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8
        val output: ByteBuffer = ByteBuffer.allocateDirect(bufferSize)
        var channelOffset = 0
        var outputStride = 0
        for (planeIndex in 0..2) {
            if (planeIndex == 0) {
                channelOffset = 0
                outputStride = 1
            } else if (planeIndex == 1) {
                channelOffset = width * height + 1
                outputStride = 2
            } else if (planeIndex == 2) {
                channelOffset = width * height
                outputStride = 2
            }
            val buffer: ByteBuffer = planes[planeIndex].getBuffer()
            val rowStride: Int = planes[planeIndex].getRowStride()
            val pixelStride: Int = planes[planeIndex].getPixelStride()
            val shift = if (planeIndex == 0) 0 else 1
            val widthShifted = width shr shift
            val heightShifted = height shr shift
            buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
            for (row in 0 until heightShifted) {
                val length: Int
                if (pixelStride == 1 && outputStride == 1) {
                    length = widthShifted
                    buffer.get(output.array(), channelOffset, length)
                    channelOffset += length
                } else {
                    length = (widthShifted - 1) * pixelStride + 1
                    buffer.get(rowData, 0, length)
                    for (col in 0 until widthShifted) {
                        output.array()[channelOffset] = rowData[col * pixelStride]
                        channelOffset += outputStride
                    }
                }
                if (row < heightShifted - 1) {
                    buffer.position(buffer.position() + rowStride - length)
                }
            }
        }
        return output
    }
}
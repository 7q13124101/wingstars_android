package com.wingstars.member.view

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class CircleWithBorderTransformation (
    private val borderWidth: Float, // 边框宽度（像素）
    private val borderColor: Int    // 边框颜色（ARGB格式）
) : BitmapTransformation() {


//    // 2. 边框颜色（示例：红色，ARGB格式）
//    val borderColor = Color.parseColor("#FF0000") // 或使用 ContextCompat.getColor(context, R.color.red)
//
//    // 3. 加载图片并应用变换
//    Glide.with(this)
//    .load(imageUrl)
//    .transform(CircleWithBorderTransformation(borderWidthPx, borderColor))
//    .into(imageView)
    companion object {
        private const val VERSION = 1
        private const val ID = "com.example.CircleWithBorderTransformation_$VERSION"
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        // 计算最终图片尺寸（原图尺寸 + 2倍边框宽度，确保边框不被裁剪）
        val finalWidth = outWidth + (2 * borderWidth).toInt()
        val finalHeight = outHeight + (2 * borderWidth).toInt()

        // 获取或创建一个带透明通道的 Bitmap 作为结果
        val result = pool.get(finalWidth, finalHeight, Bitmap.Config.ARGB_8888)
            ?: Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888)

        // 创建画布和画笔
        val canvas = Canvas(result)
        val paint = Paint().apply {
            isAntiAlias = true // 抗锯齿
            color = borderColor // 边框颜色
        }

        // 1. 绘制圆形边框（半径 = 原图半径 + 边框宽度）
        val borderRadius = (finalWidth.coerceAtMost(finalHeight) / 2).toFloat()
        canvas.drawCircle(
            finalWidth / 2f,    // 圆心x
            finalHeight / 2f,   // 圆心y
            borderRadius,       // 半径（含边框）
            paint
        )

        // 2. 绘制圆形图片（半径 = 原图半径，叠在边框内部）
        val bitmapPaint = Paint().apply {
            isAntiAlias = true
            // 设置图片作为画笔纹理
            shader  = BitmapShader(
                toTransform,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
            )
        }
        val bitmapRadius = borderRadius - borderWidth
        canvas.drawCircle(
            finalWidth / 2f,
            finalHeight / 2f,
            bitmapRadius,
            bitmapPaint
        )

        return result
    }


    override fun updateDiskCacheKey(digest: MessageDigest) {
        digest.update((ID + borderWidth + borderColor).toByteArray(CHARSET))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as CircleWithBorderTransformation
        return borderWidth == other.borderWidth && borderColor == other.borderColor
    }

    override fun hashCode(): Int {
        return ID.hashCode() + borderWidth.toInt() * 31 + borderColor
    }
}
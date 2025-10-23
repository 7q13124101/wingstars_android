package com.wingstars.member.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class TopRoundedCornersTransformation (
    private val radius: Float // 圆角半径（像素单位）
) : BitmapTransformation() {

    companion object {
        private const val VERSION = 1
        private const val ID = "com.example.TopRoundedCornersTransformation_$VERSION"
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        // 获取或创建一个与目标尺寸一致的 Bitmap
        val result = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888)
            ?: Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888)

        // 创建画布和路径，用于裁剪
        val canvas = Canvas(result)
        val path = Path()
        val rectF = RectF(0f, 0f, outWidth.toFloat(), outHeight.toFloat())

        // 设置四个角的圆角半径：左上、右上、右下、左下（后两个设为0，即底部直角）
        val radii = floatArrayOf(
            radius, radius,   // 左上
            radius, radius,   // 右上
            0f, 0f,           // 右下
            0f, 0f            // 左下
        )
        path.addRoundRect(rectF, radii, Path.Direction.CW)
        canvas.clipPath(path) // 按路径裁剪画布

        // 将原图绘制到裁剪后的画布上
        canvas.drawBitmap(toTransform, 0f, 0f, null)
        return result
    }

    // 以下方法用于缓存标识，必须实现
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + radius).toByteArray(CHARSET))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as TopRoundedCornersTransformation
        return radius == that.radius
    }

    override fun hashCode(): Int {
        return ID.hashCode() + radius.toInt()
    }
}
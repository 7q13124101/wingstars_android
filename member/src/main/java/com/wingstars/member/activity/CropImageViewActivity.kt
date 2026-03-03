package com.wingstars.member.activity


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lihang.BuildConfig
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ActivityCropImageViewBinding
import com.wingstars.member.utils.DateUtils


import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class CropImageViewActivity : BaseActivity(), View.OnClickListener {
    //private var image_background = 0
    private var photoUrl: Bitmap?=null
    private lateinit var binding: ActivityCropImageViewBinding
    private var photoBitmap: Bitmap?=null
    private var file: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropImageViewBinding.inflate(layoutInflater)
        setTitleFoot(binding.root,navigationBarColor=R.color.color_F8EBF1)
        initView()
    }

    override fun initView() {
        binding.imgBack.setOnClickListener(this)
        binding.share.setOnClickListener(this)
        binding.downLoad.setOnClickListener(this)
       var width = ScreenUtils.getWidth(this@CropImageViewActivity)
        setImage(width, width,binding.frame)
        setImage(width,width,binding.radio)
        setImage(width,width,binding.cropView)
        // 加载示例图片
  /*    var bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.home_15);
        binding.cropView.setImageBitmap(bitmap)
        binding.cropView.setAspectRatio(1f,1f)*/
        val parcelableExtra = intent.getParcelableExtra<Uri>("image")
        val photoUrl = intent.getStringExtra("photoUrl")
        if (photoUrl!=null){
            Glide.with(this).asBitmap()
                .load("$photoUrl").into(object : CustomTarget<Bitmap>() {
                    // 成功获取 Bitmap 回调
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        // resource 即为目标 Bitmap，可直接使用
                        // 例如：设置到 ImageView / 保存到本地 / 处理图片等
                        // imageView.setImageBitmap(resource)
                        photoBitmap = resource
                        binding.image.setImageBitmap(resource)
                        //Toast.makeText(this@FanInteractionActivity, "Bitmap 加载成功，尺寸：${resource.width}x${resource.height}", Toast.LENGTH_SHORT).show()
                    }

                    // 加载失败/取消回调
                    override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                        // 可选：清理资源（如 Bitmap 回收）
                    }

                    // 可选：加载失败回调
                    override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Toast.makeText(
                            this@CropImageViewActivity,
                            getString(R.string.image_loading_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
        if (parcelableExtra!=null){
            Thread({
                var bitmap = getCorrectBitmap(this@CropImageViewActivity,parcelableExtra!!)
                runOnUiThread {    binding.cropView.setImageBitmap(bitmap)
                    binding.cropView.setAspectRatio(1f,1f) }

            }).start()

        }


        binding.cropButton.setOnClickListener { //val croppedBitmap: Bitmap? = binding.cropView.getCroppedBitmap()
          //  if (croppedBitmap!=null){
              /*  val width = croppedBitmap.width
                val height = croppedBitmap.height
                Log.e("cropButton","width=$width height=$height")
                binding.resultImageView.setImageBitmap(croppedBitmap)*/


            }
          // }



    }

    private fun saveImageToGallery(bitmap: Bitmap,imageFileName: String) {
        // 设置图片的相关信息
        val values = ContentValues()
        // val imageFileName = "IMG_" + System.currentTimeMillis() + ".jpg"
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)

        // 将图片插入到相册
        val imageUri =
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        try {
            val outputStream: OutputStream? = getContentResolver().openOutputStream(imageUri!!)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
            outputStream?.flush()
            outputStream?.close()
            //Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
            //Log.e("saveImageToGallery","保存成功")

        } catch (e: IOException) {
            //Log.e("saveImageToGallery","保存失败e="+e.message)
            e.printStackTrace()
            //Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
        runOnUiThread {
               closeLoadingDialog()
            showTip(getString(R.string.save_successfully))
        }
    }

    private fun downLoads() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageToGallery(
                BitmapFactory.decodeFile(file?.absolutePath),
                file!!.name
            )
        }else{
            saveImage(file?.name,BitmapFactory.decodeFile(file?.absolutePath))
        }
    }

    fun saveImage(name: String?, bitmap: Bitmap) {
        // 获取Pictures文件夹路径
        val pathFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + File.separator
        )
        // 如果文件夹不存在则创建
        if (!pathFile.exists()) {
            pathFile.mkdir()
        }
        // 创建要保存的文件
        val file = File(pathFile, name + ".jpg")
        try {
            // 将Bitmap写入文件
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            // 通知图库更新
            val localUri = Uri.fromFile(file)
            val localIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri)
            sendBroadcast(localIntent)
            //Log.e("saveImage", "saveFile 保存成功" )
        } catch (e: IOException) {
            //Log.e("saveImage", "saveFile 保存失败 e=" + e.message)
            e.printStackTrace()
        }
        runOnUiThread {
            closeLoadingDialog()
            showTip(getString(R.string.save_successfully))
        }
    }

    private fun saveFile1(originalBitmap: Bitmap, frameBitmap: Bitmap,types: Int) {

        //Log.e("saveFile", "saveFile 开始保存")
        try {
           // val frameBitmap = BitmapFactory.decodeResource(resources, int)  //R.drawable.fans1

            //Log.e("originalBitmap","frameBitmap.width = ${frameBitmap.width},frameBitmap.height=${frameBitmap.height}")
// 2. 创建与边框图片同大小的Bitmap
            val result =
                Bitmap.createBitmap(frameBitmap.width, frameBitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)


// 3. 计算边框内边距（假设边框内边距为50像素）
            val framePadding = 0
            val dstRect = Rect(
                50,
                framePadding,
                frameBitmap.width - 50,
                frameBitmap.height - framePadding
            )


// 4. 绘制原始图片（缩放至边框内部区域）
            canvas.drawBitmap(originalBitmap, null, dstRect, null)


// 5. 绘制边框图片覆盖在上层
            canvas.drawBitmap(frameBitmap, 0f, 0f, null)

            val fusionFile = File(
                getExternalFilesDir(null),
                "${DateUtils.getTimeStr(System.currentTimeMillis())}.jpg"
            )
            var out = FileOutputStream(fusionFile);
            // 压缩Bitmap到PNG格式并写入文件
            result.compress(Bitmap.CompressFormat.PNG, 100, out)
            // 关闭输出流
            out.flush();
            out.close();
            //Log.e("saveFile", "saveFile 保存完成 fusionFile=" + fusionFile.path)
            file = fusionFile
            downLoads()

        } catch (e: Exception) {
            //Log.e("saveFile", "saveFile 保存失败 e=" + e.message)
        }

    }

    fun captureView(v: View): Bitmap {
        //Log.e("captureView","开始-----------v.getHeight()")
        val bitmap = Bitmap.createBitmap(
            v.getWidth(), v.getHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        //Log.e("captureView","结束-----------")
        return bitmap
    }

    fun getCorrectBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val input = context.contentResolver.openInputStream(uri)
            val degrees = getBitmapRotation(context, uri)
            val bitmap = BitmapFactory.decodeStream(input)
            input?.close()
            bitmap?.let { rotateBitmap(it, degrees) }
        } catch (e: Exception) {
            null
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees != 0) {
            val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
            return Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.width, bitmap.height,
                matrix, true
            )
        }
        return bitmap
    }

    private fun getBitmapRotation(context: Context, uri: Uri): Int {
        return try {
            val input = context.contentResolver.openInputStream(uri)
            val exif = ExifInterface(input!!)
            when (exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }.also { input.close() }
        } catch (e: Exception) {
            0
        }
    }
    private fun setImage(width: Int, height: Int,view: View) {

        //Log.e("width", "width=" + width)
        val params = view.layoutParams
        params.width = width
        params.height = height
        view.layoutParams = params
    }

    override fun onClick(v: View?) {
        val id = v?.id
        when(id){
            binding.share.id->{
                shareImage()
            }
            binding.downLoad.id->{
                downLoad();
            }
            binding.imgBack.id-> finish()
        }
    }

    private fun downLoad() {
        if (photoBitmap==null){
            Toast.makeText(
                this@CropImageViewActivity,
                getString(R.string.image_loading_failed),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        runOnUiThread { showLoadingUI(true, this@CropImageViewActivity) }
        Thread({
            val captureView = captureView(binding.radio)
                saveFile1(captureView,photoBitmap!!,1)

        }).start()

    }

    private fun showTip(tip: String){
        Toast.makeText(this,tip, Toast.LENGTH_LONG).show()
    }

    private fun shareImage(state: Int=1) {
        if (file==null){
            showTip(getString(R.string.ple_download_picture))
            return
        }

// 创建分享意图
        val shareIntent = Intent(Intent.ACTION_SEND)
        var stateStr = if (state==1) "image/*" else "video/*"
// 设置分享内容的类型为图片
        shareIntent.setType(stateStr)
// 添加要分享的图片 Uri
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            file!!
        ))

// 添加可选的文本描述
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_picture))

// 授予内容 Uri 的临时访问权限（重要！）
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

// 启动分享意图
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_picture)))
    }

}
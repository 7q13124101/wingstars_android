package com.wingstars.member.activity

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.lihang.BuildConfig
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ActivityTakeMomentsDisplayBinding
import com.wingstars.member.utils.DateUtils
import com.wingstars.member.utils.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class TakeMomentsDisplayActivity : BaseActivity(),OnClickListener {
    private lateinit var binding: ActivityTakeMomentsDisplayBinding
    private var file: File?=null
    private var state = 1 // 1 图片 2 视频
    private var hide = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTakeMomentsDisplayBinding.inflate(layoutInflater)
        setTitleFoot(binding.root,navigationBarColor= R.color.color_F8EBF1)
        /*immersionBar {
            statusBarColor(R.color.color_004738)
            navigationBarColor(R.color.color_F5F5F5)
            fitsSystemWindows(true)
        }*/
        initView()
    }

    override fun initView() {
        val files = intent.getStringExtra("file")
        state = intent.getIntExtra("state", 0)
        hide = intent.getBooleanExtra("hide", false)
        if (hide){
            binding.upLoad.visibility = View.GONE
        }

        setImage(binding.frame)
        setImage(binding.videoView)
        setImage(binding.image)
        if (files!=null){
            file = File(files)
            if (state==1){
                binding.image.visibility = View.VISIBLE
                Glide.with(this).load(file).into(binding.image)
            }else{
                binding.videoView.visibility = View.VISIBLE
                val uriForFile = FileProvider.getUriForFile(
                    this,
                    "${this@TakeMomentsDisplayActivity.packageName}.provider",
                    file!!
                )
                binding.videoView.setVideoURI(uriForFile)

                // 设置循环播放监听器
                binding.videoView.setOnCompletionListener(object:OnCompletionListener{
                    override fun onCompletion(mp: MediaPlayer?) {
                        binding.videoView.start();
                    }
                })



                // 开始播放
                binding.videoView.start()
            }
        }
        binding.imgBack.setOnClickListener(this)
        binding.share.setOnClickListener(this)
        binding.downLoad.setOnClickListener(this)
    }
    private fun setImage(view: View) {
        var width = ScreenUtils.getWidth(this@TakeMomentsDisplayActivity)
        val params = view.layoutParams
        params.width = width
        params.height = width
        view.layoutParams = params
    }

    override fun onPause() {
        super.onPause()
        if (state==2){
            binding.videoView.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (state==2){
            binding.videoView.start()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.share.id->{
                    shareImage(state)
                }
            binding.downLoad.id->{
                Thread({
                    if (state==1){
                        downLoad()
                    }else{
                        downVideo()
                    }

                }).start()
            }
            binding.imgBack.id->{
                finish()
            }
        }
    }

    private fun downVideo(){
        runOnUiThread { showLoadingUI(true, this@TakeMomentsDisplayActivity) }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val uriForFile = FileProvider.getUriForFile(
                this,
                "${this@TakeMomentsDisplayActivity.packageName}.provider",
                file!!
            )
            copyVideoToPicturesDirectory(uriForFile)
        }else{
            copyVideoToPictures()
        }


    }


    private fun copyVideoToPictures() {
        // 源视频文件（替换为实际路径）
        val sourceFile = file
        // 创建目标目录（Pictures/MyVideos/）
        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val targetDir = File(picturesDir, "MyVideos")
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            Toast.makeText(this, "无法创建目标目录", Toast.LENGTH_SHORT).show()
            return
        }


        // 创建目标文件
        val fileName = sourceFile?.name
        val targetFile = File(targetDir, fileName)

        try {
            FileInputStream(sourceFile).use { fis ->
                FileOutputStream(targetFile).use { fos ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int

                    while ((fis.read(buffer).also { bytesRead = it }) != -1) {
                        fos.write(buffer, 0, bytesRead)
                    }

                    fos.flush()



                    // 通知媒体库更新
                    scanFile(targetFile)

                }
            }
        } catch (e: IOException) {
            e.printStackTrace()

        }
        runOnUiThread {
            closeLoadingDialog()
            showTip(getString(R.string.save_successfully))
        }
    }

    private fun scanFile(file: File) {
        MediaScannerConnection.scanFile(
            this,
            arrayOf<String>(file.getAbsolutePath()),
            arrayOf<String>("video/mp4"),
            object : MediaScannerConnection.OnScanCompletedListener {
                override fun onScanCompleted(path: String?, uri: Uri?) {

                }
            }
        )
    }
    fun copyVideoToPicturesDirectory(sourceVideoUri: Uri?) {
        val contentResolver = getContentResolver()
        val contentValues = ContentValues()


        // 设置视频元数据
        contentValues.put(
            MediaStore.Video.Media.DISPLAY_NAME,
            "CopiedVideo_" + System.currentTimeMillis() + ".mp4"
        )
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        contentValues.put(
            MediaStore.Video.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + "/MyVideos/"
        )


        // 插入新的媒体条目
        var targetUri: Uri? = null
        try {
            targetUri =
                contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (targetUri != null) {
                // 复制视频内容
                try {
                    contentResolver.openInputStream(sourceVideoUri!!).use { inputStream ->
                        contentResolver.openOutputStream(targetUri).use { outputStream ->
                            if (inputStream != null && outputStream != null) {
                                val buffer = ByteArray(8192)
                                var bytesRead: Int

                                while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                                    outputStream.write(buffer, 0, bytesRead)
                                }

                                outputStream.flush()
                                //Log.d(TAG, "视频已成功复制到 Pictures 目录")
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                   // Log.e(TAG, "复制视频内容失败: " + e.message)



                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
           // Log.e(TAG, "创建媒体条目失败: " + e.message)
        }
        runOnUiThread {  closeLoadingDialog()
            showTip(getString(R.string.save_successfully))}
    }

    private fun downLoad() {
        runOnUiThread {  showLoadingUI(true, this@TakeMomentsDisplayActivity) }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (hide){
                saveImageToGallery(
                    BitmapFactory.decodeFile(file?.absolutePath),
                    file!!.name
                )
            }else{
                saveImageDownload(file!!.name,BitmapFactory.decodeFile(file?.absolutePath))
            }

        }else{
            if (hide){
                saveImage(file?.name,BitmapFactory.decodeFile(file?.absolutePath))
            }else{
                saveImageDownload(file!!.name,BitmapFactory.decodeFile(file?.absolutePath))
            }

        }
    }

    fun saveImageDownload(name: String?, bitmap: Bitmap) {
        // 获取Pictures文件夹路径
  /*      val pathFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + File.separator
        )*/
        val download =
            FileUtils.createFolderInExternalStorage(this@TakeMomentsDisplayActivity, "download",getExternalFilesDir(null)!!)
        if (download==null){
            return;
        }
        val newFile =
            FileUtils.createFolderInExternalStorage(this@TakeMomentsDisplayActivity, "${DateUtils.getTimeYMD(System.currentTimeMillis())}",download)

        //var pathFile = File(getExternalFilesDir(null),"/download/${DateUtils.getTimeYMD(System.currentTimeMillis())}")
        // 如果文件夹不存在则创建
        if (newFile==null){
            return;
        }
        // 创建要保存的文件
        val file = File(newFile, name)
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
           // Log.e("saveImage", "saveFile 保存成功" )
            //runOnUiThread { showTip("saveImage保存成功 pathFile=${pathFile.path}") }
        } catch (e: IOException) {
         //   Log.e("saveImage", "saveFile 保存失败 e=" + e.message)
           // runOnUiThread { showTip("saveImage保存失败e=${e.message}") }
            //e.printStackTrace()
        }
        runOnUiThread {
            closeLoadingDialog()
            showTip(getString(R.string.save_successfully))
        }
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
          //  Log.e("saveImageToGallery","保存成功")
           /* runOnUiThread { showTip("saveImageToGallery保存成功")
                BaseApplication.shared()!!.closeLoadingDialog()}*/

        } catch (e: IOException) {
            //Log.e("saveImageToGallery","保存失败e="+e.message)
           /* runOnUiThread { showTip("saveImageToGallery保存失败e=${e.message}")
                BaseApplication.shared()!!.closeLoadingDialog()}*/
            e.printStackTrace()
            //Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
        runOnUiThread { closeLoadingDialog()
                showTip(getString(R.string.save_successfully))
      }
    }

    private fun showTip(tip: String){
        Toast.makeText(this,tip, Toast.LENGTH_LONG).show()
    }

    private fun shareImage(state: Int=1) {

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
package com.wingstars.member.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.AutoFocusCallback
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.utils.ScreenUtils
import com.wingstars.member.R
import com.wingstars.member.databinding.ActivityFanInteractionBinding
import com.wingstars.member.utils.DateUtils
import com.wingstars.member.view.TakePhotosMemberPopupView
import com.wingstars.base.view.UpLoadingDialog
import com.wingstars.member.bean.TakePhotosMembersListBean
import com.wingstars.member.viewmodel.FanInteractionViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class FanInteractionActivity : BaseActivity(), View.OnClickListener,
    TextureView.SurfaceTextureListener, TakePhotosMemberPopupView.OnSelectImageUrl {
    private var mBackCameraInfo: Camera.CameraInfo? = null
    private val viewModel: FanInteractionViewModel by viewModels()
    private var surface: SurfaceTexture? = null
    private var open = false
    private var type = 1
    private var REQUEST_SELECT_MEDIA = 30
    private var mCamera: Camera? = null
    private var width: Int = 0
    private var uploadDialog: UpLoadingDialog? = null
    private var takePhotosMembersList = mutableListOf<TakePhotosMembersListBean>()

    private var image_background = R.mipmap.fans1
    private var pos = 0
    private lateinit var binding: ActivityFanInteractionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFanInteractionBinding.inflate(layoutInflater)

        setTitleFoot(binding.root, navigationBarColor = R.color.color_F8EBF1)
        initView()
    }


    override fun initView() {
        viewModel.takePhotosMembersList.observe(this) {
            Log.e("takePhotosMembersList", "${Gson().toJson(it)}")
            takePhotosMembersList.addAll(it)
            if (it.size > 0) {
                onSelectImageUrl(0)
            }
        }
        viewModel.loading.observe(this) {
            showLoadingUI(it, this)
        }
        viewModel.wsPhotoFrames()
        binding.title.setBackClickListener { finish() }
        var width = ScreenUtils.getWidth(this@FanInteractionActivity)
        setImage(width, width)
        binding.surfaceView.surfaceTextureListener = this
        binding.takePicture.setOnClickListener {
            mCamera?.takePicture(null, null, object : Camera.PictureCallback {
                override fun onPictureTaken(data: ByteArray?, camera: Camera?) {
                    Thread(object : Runnable {
                        @SuppressLint("ResourceType")
                        override fun run() {
                            runOnUiThread {
                                showLoadingUI(true, this@FanInteractionActivity)
                            }
                            val bitmap = getBitmap(data!!)
                            var rotates: Bitmap? = null
                            if (type == 0) {
                                rotates = rotate(bitmap!!, 90f)
                            } else {
                                var rotate = rotate(bitmap!!, -90f)
                                rotates = flipBitmapHorizontally(rotate)
                            }

                            if (type == 0) {
                                saveFile1(rotates, image_background, 1)
                            } else {
                                // addFrameAndSave(this@FanInteractionActivity,rotates,image_background)
                                saveFile1(rotates, image_background, 1)
                            }
                            //  saveFile2(rotates)

                        }
                    }).start()

                }

            })
        }
        binding.switchCamera.setOnClickListener {
            type = if (type == 0) {
                1
            } else {
                0
            }
            stopCamera()
            startCamera(surface!!, type)
        }
        binding.title.setRightTextClickListener {
            //startActivity(Intent(this@FanInteractionActivity, RelativeActivity::class.java))
            selectImage()
        }
        binding.selectMember.setOnClickListener(this)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*") // 限制选择类型为图片
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false) // 允许选择多个文件
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png")) // 限制图片类型
        // 限制最多选择3个文件
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_MEDIA)
    }

    private fun saveFile1(originalBitmap: Bitmap, int: Int, types: Int) {

        Log.e("saveFile", "saveFile 开始保存")
        try {
            val frameBitmap = BitmapFactory.decodeResource(resources, int)  //R.drawable.fans1

            Log.e(
                "originalBitmap",
                "frameBitmap.width = ${frameBitmap.width},frameBitmap.height=${frameBitmap.height}"
            )
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

            val tempDir = File(getExternalFilesDir(null), "temporary")
            if (!tempDir.exists()) {
                if (!tempDir.mkdirs()) {
                    Log.e("originalBitmap", "创建临时文件夹失败")
                    return
                }
            }
            /*val fusionFile = File(
                getExternalFilesDir(null),
                "/temporary/${DateUtils.getTimeStr(System.currentTimeMillis())}.jpg"
            )*/
            val fusionFile = File(
                tempDir,
                "${DateUtils.getTimeStr(System.currentTimeMillis())}.jpg"
            )
            var out = FileOutputStream(fusionFile);
            // 压缩Bitmap到PNG格式并写入文件
            result.compress(Bitmap.CompressFormat.PNG, 100, out);
            // 关闭输出流
            out.flush();
            out.close();
            Log.e("saveFile", "saveFile 保存完成 fusionFile=" + fusionFile.path)
            runOnUiThread {
                //  binding.images.setImageBitmap(rotate)
                closeLoadingDialog()
                /*  val intent = Intent(this, TakeMomentsDisplayActivity::class.java)
                    intent.putExtra("file",fusionFile.path)
                    intent.putExtra("state",1)
                    intent.putExtra("hide",true)
                    startActivity(intent)*/
                if (types == 2) {
                    binding.surfaceView.visibility = View.VISIBLE
                    binding.selectImage.visibility = View.GONE
                }
                stopCamera()
                startCamera(surface!!, type)
            }

            /*     if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

                     saveImageToGallery(
                         BitmapFactory.decodeFile(fusionFile.absolutePath),
                         fusionFile.name
                     )
                 }else{
                     saveImage(fusionFile.name,BitmapFactory.decodeFile(fusionFile.absolutePath))
                 }*/
        } catch (e: Exception) {
            Log.e("saveFile", "saveFile 保存失败 e=" + e.message)
        }
        // 1. 加载原始图片和边框图片
        /*  val originalBitmap = BitmapFactory.decodeResource(
              resources, R.drawable.your_image
          )*/

    }

    private fun stopCamera() {
        mCamera!!.stopPreview();
        mCamera!!.release();
        mCamera = null;
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
            Log.e("saveImage", "saveFile 保存成功")
        } catch (e: IOException) {
            Log.e("saveImage", "saveFile 保存失败 e=" + e.message)
            e.printStackTrace()
        }
    }

    /**
     * @param type  0 后置摄像头   1前置摄像头
     */
    private fun startCamera(surface: SurfaceTexture, type: Int) {
        val backCamera: Pair<Camera.CameraInfo, Int> = getBackCamera(type)!!
        val backCameraId = backCamera.second
        mBackCameraInfo = backCamera.first
        mCamera = Camera.open(backCameraId)
        getSupportList(mCamera)
        cameraDisplayRotation(type)

        try {
            mCamera!!.setPreviewTexture(surface)
            mCamera!!.startPreview()
            mCamera!!.autoFocus(object : AutoFocusCallback {
                override fun onAutoFocus(
                    success: Boolean,
                    camera: Camera?
                ) {
                    Log.e("autoFocus", "success=$success")
                }

            })
        } catch (ioe: IOException) {

        }
    }


    fun flipBitmapHorizontally(src: Bitmap): Bitmap {
        // 创建一个新的Matrix对象
        val matrix = Matrix()


        // 准备矩阵参数，水平翻转
        matrix.preScale(-1.0f, 1.0f)


        // 创建一个新的Bitmap对象，它是原始Bitmap的左右颠倒版本
        val result = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)

        return result
    }

    public fun rotate(b: Bitmap, degrees: Float): Bitmap {
        var b1: Bitmap? = null
        var m = Matrix();
        var widths = b.width / 2
        var height = b.getHeight() / 2
        m.setRotate(degrees)
        m.setRotate(
            degrees, widths.toFloat(),
            height.toFloat()
        );

        var b2 = Bitmap.createBitmap(
            b, 0, 0, b.getWidth(),

            b.getHeight(), m, true
        );

        if (b != b2) {

            b.recycle();

            b1 = b2;

        } else {
            b1 = b
        }



        return b1

    }

    private fun getBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }


    private fun setImage(width: Int, height: Int) {

        Log.e("width", "width=" + width)
        val params = binding.frameLayout.layoutParams
        params.width = width
        params.height = height
        binding.frameLayout.layoutParams = params
    }

    override fun onClick(v: View?) {
        var id = v?.id
        when (id) {
            binding.selectMember.id -> showPopupWindow()
        }
    }

    private fun showPopupWindow() {
        // val takePhotosMembersList = viewModel.getTakePhotosMembersList()
        var popupWindow = TakePhotosMemberPopupView(
            this, getNavigationBarHeight(),
            takePhotosMembersList, this
        )
        popupWindow.show(binding.main)
    }

    override fun onSurfaceTextureAvailable(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        this.surface = surface
        val backCamera: Pair<Camera.CameraInfo, Int> = getBackCamera(type)!!
        val backCameraId = backCamera.second
        mBackCameraInfo = backCamera.first
        mCamera = Camera.open(backCameraId)
        getSupportList(mCamera)
        cameraDisplayRotation(type)

        try {
            mCamera!!.setPreviewTexture(surface)
            mCamera!!.startPreview()
            mCamera!!.autoFocus(object : AutoFocusCallback {
                override fun onAutoFocus(
                    success: Boolean,
                    camera: Camera?
                ) {
                    Log.e("autoFocus", "success=$success")
                }

            })
        } catch (ioe: IOException) {

        }
    }

    fun cameraDisplayRotation(type: Int) {
        val rotation: Int = this.windowManager
            .defaultDisplay.rotation

        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result = 0
        if (type == 1) {
            result = (mBackCameraInfo!!.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (mBackCameraInfo!!.orientation - degrees + 360) % 360;
        }
        Log.e("result", "result=$result")
        mCamera!!.setDisplayOrientation(result);
    }

    private fun getSupportList(mCamera: Camera?) {
        var select = false
        val parameters = mCamera?.getParameters()
        val supportedSizes = parameters?.supportedPreviewSizes
        val focusModes = parameters!!.getSupportedFocusModes()
        /* focusModes.forEach {
             Log.e("focusModes","$it")
         }*/

        val sizes = parameters!!.getSupportedPictureSizes()

        //Log.e("supportedSizes", "supportedSizes==${Gson().toJson(supportedSizes)}")
        for (i in supportedSizes!!.indices) {
            val size = supportedSizes[i]
            if (size.width == size.height) {
                Log.e("CameraSizes", "size Width: " + size.width + "size Height: " + size.height)
                select = true
                try {
                    parameters.setPreviewSize(size.width, size.height)

                    for (size1 in sizes) {
                        if (size1.width == size1.height) {
                            Log.e(
                                "CameraSizes",
                                "size1 Width: " + size1.width + " size1 Height: " + size1.height
                            )
                            parameters.setPictureSize(size1.width, size1.height)
                            break
                        }
                    }
                    //   parameters.setPictureSize(size.width, size.height)
                    Log.e("size.width", "${size.width}")
                    if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO;
                    }

                    mCamera.parameters = parameters

                } catch (e: Exception) {
                    Log.e("getSupportList", "e=${e.message}")
                    val defaultParams: Camera.Parameters? = mCamera.getParameters()
                    mCamera.setParameters(defaultParams)
                }

                break
            }
        }
        if (!select) {
            for (i in supportedSizes.indices) {
                val size = supportedSizes[i]
                if (size.width <= width && size.height <= width) {
                    Log.e(
                        "CameraSizes",
                        "size Width: " + size.width + "size Height: " + size.height
                    )
                    try {
                        parameters.setPreviewSize(size.width, size.height)
                        // parameters.setPictureSize(size.width, size.height)
                        for (size1 in sizes) {
                            if (size1.width <= width && size1.height <= width) {
                                Log.e(
                                    "CameraSizes",
                                    "size1 Width: " + size1.width + "size1 Height: " + size1.height
                                )
                                parameters.setPictureSize(size1.width, size1.height)
                                break
                            }
                        }
                        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                            parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO;
                        }
                        mCamera.parameters = parameters
                    } catch (e: Exception) {

                    }
                    break
                }
            }
        }

    }


    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        mCamera!!.stopPreview();
        mCamera!!.release();
        mCamera = null;
        return true;
    }

    override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) {

    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    private fun getBackCamera(type: Int): Pair<Camera.CameraInfo, Int>? {
        val cameraInfo: Camera.CameraInfo = Camera.CameraInfo()
        val numberOfCameras: Int = Camera.getNumberOfCameras()

        for (i in 0..<numberOfCameras) {
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing === type) {
                return Pair<Camera.CameraInfo, Int>(
                    cameraInfo,
                    i
                )
            }
        }
        return null
    }

    override fun onSelectImageUrl(pos: Int) {
        this.pos = pos
        val bean = takePhotosMembersList[pos]
        binding.name.text = "#${bean.number} ${bean.name}"
        //
        Glide.with(this).asBitmap()
            .load("${bean.imgae}").into(object : CustomTarget<Bitmap>() {
                // 成功获取 Bitmap 回调
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    // resource 即为目标 Bitmap，可直接使用
                    // 例如：设置到 ImageView / 保存到本地 / 处理图片等
                    // imageView.setImageBitmap(resource)
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
                        this@FanInteractionActivity,
                        getString(R.string.image_loading_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}
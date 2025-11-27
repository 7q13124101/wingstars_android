package com.wingstars.user.code

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsControllerCompat
import com.wingstars.base.base.BaseActivity
import com.wingstars.user.BaseApplication
import com.wingstars.user.R
import com.wingstars.user.databinding.ActivityMemBarCodeBinding

class MemBarCodeActivity : BaseActivity(){
    private lateinit var binding: ActivityMemBarCodeBinding
    private var phone: String?=null
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMemBarCodeBinding.inflate(layoutInflater)
        setTitleFoot(binding.root)
        phone = intent.getStringExtra("phone")
//        initData()
        initView()
        // SET MÀU SAU CÙNG
//        window.statusBarColor = Color.BLACK
//        window.navigationBarColor = Color.BLACK
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        window.statusBarColor = getColor(R.color.color_DE9DBA)

// Cho layout trải sát status bar
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    private fun initData(){
//        BaseApplication.shared()!!.showLoadingUI(true, this)
//        val id = MMKV.defaultMMKV().decodeString("crm_member_id")
//        val phone = MMKV.defaultMMKV().decodeString("member_phone")
//        API?.shared?.api?.let {
//            //Member > 会员QRCode
//            val observer =
//                it.crmGenQRCode("${BaseApplication.HOST_CRM}/api/v1/basic/member/${id}/gen-qrcode",
//                    phone?.let { it1 -> CRMGenQRCodeRequest(it1) })
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread()
//            )?.subscribe(
//                { next ->
//                    if (next.success) {
//                        next.data?.let { rd ->
//                            val qRCodeBitmap = generateQRCode(rd.MEMQRCODE)
//                            if (qRCodeBitmap != null) {
//                                binding.ivQrSmall.setImageBitmap(qRCodeBitmap)
//                                binding.ivQrLarger.setImageBitmap(qRCodeBitmap)
//                            }
//                        }
//                    } else {
//                        BaseApplication.shared()!!.closeLoadingDialog()
//                        //Toast.makeText(BaseApplication.shared()!!, next.message, Toast.LENGTH_LONG).show()
//                    }
//                },
//                { error ->
//                    BaseApplication.shared()!!.closeLoadingDialog()
//                    var msg = error.message.toString()
//
//                    if (error is HttpException) {
//                        try{
//                            val gson = Gson()
//                            val type = object : TypeToken<CRMBaseFailResponse>() {}.type
//                            val failResponse = gson.fromJson<CRMBaseFailResponse>(
//                                error.response()?.errorBody()?.string(), type
//                            )
//                            if (failResponse != null) {
//                                failResponse.message?.let {
//                                    msg = it
//                                }
//                            }
//                        }catch (e: Exception){
//
//                        }
//                    }
//
//                    msg.let { it1 ->
//                        //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
//                    }
//                }
//            )
//        }
//        //会员条码
//        API.shared?.api?.let {
//            val observer =
//                it.crmGetMemberContact("${BaseApplication.HOST_CRM}/api/v1/basic/member/${id}/contact")
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread()
//            )?.subscribe(
//                { next ->
//                    if (next.success) {
//                        if(next.data != null && next.data!!.ExtraData != null){
//                            if (next.data.ExtraData.invoice_option == "mobileCarrier") {
//                                if (next.data.ExtraData.invoice_number.isNullOrEmpty()) {
//                                    binding.llNullCode.visibility = View.VISIBLE
//                                    binding.llBarLagerCode.visibility = View.GONE
//                                } else {
//                                    binding.llNullCode.visibility = View.GONE
//                                    binding.llBarLagerCode.visibility = View.VISIBLE
//                                    binding.tvCode.text = next.data.ExtraData.invoice_number
//                                    binding.tvCodeSmall.text = next.data.ExtraData.invoice_number
//                                    val barCodeBitmap = generateBarcode(next.data.ExtraData.invoice_number)
//                                    if (barCodeBitmap != null) {
//                                        binding.ivBarSmall.setImageBitmap(barCodeBitmap)
//                                        binding.ivBarLager.setImageBitmap(barCodeBitmap)
//                                    }
//                                }
//                            } else {
//                                binding.llNullCode.visibility = View.VISIBLE
//                                binding.llBarLagerCode.visibility = View.GONE
//                            }
//                        }
//                    } else {
//                        BaseApplication.shared()!!.closeLoadingDialog()
//                        //Toast.makeText(BaseApplication.shared()!!, next.message, Toast.LENGTH_LONG).show()
//                    }
//                },
//                { error ->
//                    BaseApplication.shared()!!.closeLoadingDialog()
//                    var msg = error.message.toString()
//                    if (error is HttpException) {
//                        try{
//                            val gson = Gson()
//                            val type = object : TypeToken<CRMBaseFailResponse>() {}.type
//                            val failResponse = gson.fromJson<CRMBaseFailResponse>(
//                                error.response()?.errorBody()?.string(), type
//                            )
//                            if (failResponse != null) {
//                                failResponse.message?.let {
//                                    msg = it
//                                }
//                            }
//                        }catch (e: Exception){
//
//                        }
//                    }
//
//                    msg.let { it1 ->
//                        //Toast.makeText(BaseApplication.shared()!!, "$it1", Toast.LENGTH_LONG).show()
//                    }
//                }
//            )
//        }
    }
    override fun initView() {
        binding.ivClose.setOnClickListener { finish() }
    }
}
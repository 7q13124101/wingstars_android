package com.wingstars.count.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.wingstars.count.R
import com.wingstars.count.activity.ExchangeDetailsActivity
import com.wingstars.count.activity.GiftDetailsActivity
import com.wingstars.count.adapter.UnusedCouponAdapter
import com.wingstars.count.databinding.FragmentNotUsedBinding
import com.wingstars.count.viewmodel.NotUsedViewModel
import com.wingstars.base.net.NetworkMonitorNew
import com.wingstars.base.net.beans.CRMCouponsResponse
import com.wingstars.count.Repository.ActivityStatusEnum
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class NotUsedFragment : Fragment() {
    private var _binding: FragmentNotUsedBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NotUsedViewModel
    private lateinit var unusedCouponAdapter: UnusedCouponAdapter
    private var currentDataList: List<CRMCouponsResponse> = listOf()
    private var isDataLoaded = false

    private var qrCodeDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotUsedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[NotUsedViewModel::class.java]

        setupRecyclerView()
        setupRefreshLayout()
        initScrollListener()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        if (!isDataLoaded) {
            loadData()
            isDataLoaded = true
        }
    }

    private fun navigateToDetail(position: Int) {
        if (position < 0 || position >= currentDataList.size) return

        val selectedItem = currentDataList[position]
        val couponType = selectedItem.coupon?.couponType ?: 0
        val gson = com.google.gson.Gson()
        val listToSend = ArrayList<com.wingstars.base.net.beans.CRMCouponsAvailableResponse>()

        for (item in currentDataList) {
            val jsonString = gson.toJson(item.coupon)
            val converted = gson.fromJson(jsonString, com.wingstars.base.net.beans.CRMCouponsAvailableResponse::class.java)
            converted.couponCode = item.couponCode

            listToSend.add(converted)
        }

        val intent = if (couponType == 1) {
            Intent(requireActivity(), GiftDetailsActivity::class.java)
        } else {
            Intent(requireActivity(), ExchangeDetailsActivity::class.java)
        }
        intent.putExtra("data", listToSend[position])
        intent.putExtra("status", ActivityStatusEnum.UNUSED_REDEMPTION.name)
        intent.putExtra("couponCode", selectedItem.couponCode)
        intent.putExtra("EXTRA_LIST_DATA", listToSend)
        intent.putExtra("EXTRA_CURRENT_INDEX", position)

        startActivity(intent)
    }

    private fun setupRecyclerView() {
        unusedCouponAdapter = UnusedCouponAdapter(mutableListOf()) { data ->
            val index = currentDataList.indexOf(data)
            if (index != -1) {
                navigateToDetail(index)
            }
        }

        binding.rvNotUsed.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = unusedCouponAdapter
        }

        unusedCouponAdapter.onBarcodeClick = { itemData ->
            val index = currentDataList.indexOf(itemData)
            if (index != -1 && currentDataList.isNotEmpty()) {
                showBarcodeDialog(index, currentDataList)
            }
        }

        binding.top.setOnClickListener {
            binding.scrollView.smoothScrollTo(0, 0)
        }
    }

    private fun initScrollListener() {
        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY == 0) {
                if (binding.top.isVisible) {
                    binding.top.visibility = View.GONE
                }
            } else {
                if (binding.top.isGone) {
                    binding.top.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupRefreshLayout() {
        binding.srlNotUsed.setOnRefreshListener { refreshLayout ->
            loadData()
        }
        binding.srlNotUsed.setEnableLoadMore(false)
    }

    private fun loadData() {
        if (NetworkMonitorNew.getInstance(requireActivity()).currentNetworkState.isConnected) {
            viewModel.getNotUsedCouponsData()
        } else {
            binding.srlNotUsed.finishRefresh(false)
            binding.llEmpty.visibility = View.VISIBLE
            binding.rvNotUsed.visibility = View.GONE
        }
    }

    private fun setupObservers() {
        viewModel.notUsedCouponsData.observe(viewLifecycleOwner) { list ->
            binding.srlNotUsed.finishRefresh(true)

            if (!list.isNullOrEmpty()) {
                currentDataList = list
                unusedCouponAdapter.setData(list)
                binding.llEmpty.visibility = View.GONE
                binding.rvNotUsed.visibility = View.VISIBLE
            } else {
                currentDataList = emptyList()
                unusedCouponAdapter.setData(emptyList())
                binding.llEmpty.visibility = View.VISIBLE
                binding.rvNotUsed.visibility = View.GONE
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                if (binding.srlNotUsed.state.isOpening) {
                    binding.srlNotUsed.finishRefresh()
                }
            }
        }
        viewModel.couponQRCode.observe(viewLifecycleOwner) { qrData ->
            if (!qrData.isNullOrEmpty()) {
                if (qrCodeDialog != null && qrCodeDialog!!.isShowing) {
                    val ivQrCode = qrCodeDialog!!.findViewById<ImageView>(R.id.iv_qr_code)

                    if (ivQrCode != null) {
                        if (qrData.startsWith("http")) {
                            Glide.with(this)
                                .load(qrData)
                                .dontAnimate()
                                .into(ivQrCode)
                        } else {
                            val bitmap = createQRCodeBitmap(qrData, 1000, 1000)
                            if (bitmap != null) {
                                ivQrCode.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showBarcodeDialog(startPosition: Int, dataList: List<CRMCouponsResponse>) {
        if (dataList.isEmpty()) return

        val context = requireContext()
        qrCodeDialog = Dialog(context, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog)
        qrCodeDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        qrCodeDialog?.setContentView(R.layout.dialog_exchange_barcode)

        qrCodeDialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            decorView.setPadding(0, 0, 0, 0)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
        }

        val dialog = qrCodeDialog!!

        val llexchangedetail = dialog.findViewById<View>(R.id.ll_exchange_detail)
        val btnClose = dialog.findViewById<ImageView>(R.id.iv_close_dialog)
        val btnNext = dialog.findViewById<AppCompatButton>(R.id.btn_next)
        val btnPrev = dialog.findViewById<AppCompatButton>(R.id.btn_prev)
        val tvName = dialog.findViewById<TextView>(R.id.tv_exchange_name)
        val tvPeriod1 = dialog.findViewById<TextView>(R.id.tv_exchange_period1)
        val ivImage = dialog.findViewById<ImageView>(R.id.iv_goods_image)
        val ivQrCode = dialog.findViewById<ImageView>(R.id.iv_qr_code)
        val tvQrEnlarge = dialog.findViewById<TextView>(R.id.tv_qr_code)
        val labelContainer = dialog.findViewById<View>(R.id.label)
        val labelTv = dialog.findViewById<TextView>(R.id.label_tv)

        var currentDialogPosition = startPosition

        fun zoomQrCode() {
            val zoomDialog = Dialog(context)
            zoomDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            zoomDialog.setContentView(R.layout.dialog_zoom_qr)
            zoomDialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                decorView.setPadding(0, 0, 0, 0)
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
            val ivZoomedQr = zoomDialog.findViewById<ImageView>(R.id.iv_zoomed_qr)
            val btnCloseZoom = zoomDialog.findViewById<ImageView>(R.id.iv_close_zoom)

            if (ivQrCode.drawable != null) {
                ivZoomedQr.setImageDrawable(ivQrCode.drawable.constantState?.newDrawable())
            }
            btnCloseZoom.setOnClickListener { zoomDialog.dismiss() }
            zoomDialog.show()
        }

        ivQrCode.setOnClickListener { zoomQrCode() }
        tvQrEnlarge.setOnClickListener { zoomQrCode() }

        fun formatDate(dateStr: String?): String {
            if (dateStr.isNullOrEmpty()) return ""

            return try {
                val date = OffsetDateTime.parse(dateStr)
                val outputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
                date.format(outputFormatter)
            } catch (e: Exception) {
                ""
            }
        }

        fun updateDialogUI() {
            val item = dataList[currentDialogPosition]
            val start = formatDate(item.coupon?.couponStartDate)
            val end = formatDate(item.coupon?.couponEndDate)
            tvName.text = item.coupon?.couponName
//            tvPeriod1.text = "兌換時間：${item.coupon?.couponStartDate ?: ""}~${item.coupon?.couponEndDate ?: ""}"
            tvPeriod1.text = "兌換時間：$start~$end"
            Glide.with(context)
                .load(item.coupon?.coverImage)
                .into(ivImage)

            val codeString = item.couponCode ?: ""
            if (codeString.isNotEmpty()) {
//                ivQrCode.setImageResource(R.drawable.ic_qr_code_placeholder)
                viewModel.crmCouponQRCode(codeString)
            } else {
                ivQrCode.setImageResource(R.drawable.ic_qr_code_placeholder)
            }

            val eligibleMembersStr = item.coupon?.eligibleMembersStr
            if (!eligibleMembersStr.isNullOrEmpty() && eligibleMembersStr != getString(R.string.all_members)) {
                labelContainer?.visibility = View.VISIBLE
                labelTv?.text = eligibleMembersStr
            } else {
                labelContainer?.visibility = View.GONE
            }

            if (currentDialogPosition == 0) {
                btnPrev.isEnabled = false
                btnPrev.setTextColor(ContextCompat.getColor(context, R.color.color_101828))
            } else {
                btnPrev.isEnabled = true
                btnPrev.setTextColor(ContextCompat.getColor(context, R.color.color_E2518D))
            }

            if (currentDialogPosition == dataList.size - 1) {
                btnNext.isEnabled = false
                btnNext.alpha = 0.5f
            } else {
                btnNext.isEnabled = true
                btnNext.alpha = 1.0f
            }
        }

        llexchangedetail.setOnClickListener {
            dialog.dismiss()
            navigateToDetail(currentDialogPosition)
        }

        updateDialogUI()

        btnClose.setOnClickListener { dialog.dismiss() }

        btnNext.setOnClickListener {
            if (currentDialogPosition < dataList.size - 1) {
                currentDialogPosition++
                updateDialogUI()
            }
        }

        btnPrev.setOnClickListener {
            if (currentDialogPosition > 0) {
                currentDialogPosition--
                updateDialogUI()
            }
        }

        dialog.show()
    }

    private fun createQRCodeBitmap(content: String, width: Int, height: Int): android.graphics.Bitmap? {
        return try {
            val bitMatrix = com.google.zxing.MultiFormatWriter().encode(
                content,
                com.google.zxing.BarcodeFormat.QR_CODE,
                width,
                height
            )
            val w = bitMatrix.width
            val h = bitMatrix.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    pixels[y * w + x] = if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                }
            }
            android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, w, 0, 0, w, h)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (qrCodeDialog != null && qrCodeDialog!!.isShowing) {
            qrCodeDialog!!.dismiss()
        }
    }
}
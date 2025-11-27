package com.wingstars.home.adapter // Đặt package name cho đúng

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wingstars.home.R // Import R của module :home

class ProductAdapter(private val context: Context, private val dataList: List<Int>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    private val productImages = listOf(
        R.drawable.img_product_01,
        R.drawable.img_product_02,
        R.drawable.img_product_03,
        R.drawable.img_product_04,

        )

    // 2. Danh sách Tiêu đề tương ứng (Bạn hãy sửa lại nội dung text ở đây nhé)
    private val productTitles = listOf(
        "2025 WS LOGO卡冊", // Tương ứng img_style_01
        "2025 WS 女孩卡冊",
        "2025 巴士迴力車｜WS款",
        "2025 巴士迴力車｜WS款",
    )
    private val productPrices = listOf(
        "\$200", // Tương ứng img_style_01
        "\$300",
        "\$200",
        "\$200",
    )
    // Tạo một ViewHolder đơn giản
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // (Bạn sẽ lấy các view từ item_product.xml ở đây)
        // val productName: TextView = view.findViewById(R.id.tv_product_name)
        val productImage: ImageView = view.findViewById(R.id.img_product)
        val productTitle: TextView = view.findViewById(R.id.tv_product_name)
        val productPrice: TextView = view.findViewById(R.id.tv_product_price)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Sử dụng layout item_product của bạn
        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Lấy dữ liệu (hiện tại chỉ là số Int)
        val imageIndex = position % productImages.size
        val titleIndex = position % productTitles.size
        val priceIndex = position % productPrices.size

        holder.productImage.setImageResource(productImages[imageIndex])
        holder.productTitle.text = productTitles[titleIndex]
        holder.productPrice.text = productPrices[titleIndex]
        // (Bạn sẽ bind dữ liệu vào view ở đây)
        // holder.productName.text = "Sản phẩm $item"
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
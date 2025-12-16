package com.wingstars.base.net.beans

import java.io.Serializable
import java.util.regex.Pattern
import android.text.Html

data class IteneraryResponse(
    val id: Int,
    val title: Title,
    val content: Content,
    val acf: Acf?,
    // 1. SỬA: Đổi từ JsonElement? thành Any? để hỗ trợ Serializable
    val yoast_head_json: Any?,
    val calendar_category: List<Int>?
) : Serializable {

    data class Title(
        val rendered: String
    ) : Serializable

    data class Content(
        val rendered: String
    ) : Serializable

    data class Acf(
        val date: String?,
        val map: String?,
        val Precautions: String?
    ) : Serializable

    // --- CÁC HÀM TIỆN ÍCH ---

    val titleF: String
        get() = title.rendered

    val dateF: String
        get() = acf?.date ?: ""

    val locationF: String
        get() = acf?.map ?: ""

    val precautionsF: String
        get() = acf?.Precautions ?: ""

    // 2. SỬA: Cập nhật logic lấy ảnh từ Any (Map/List)
    val imageF: String
        get() {
            try {
                // Kiểm tra nếu nó là một Map (tương đương JsonObject)
                if (yoast_head_json is Map<*, *>) {
                    // Lấy field "og_image"
                    val ogImage = yoast_head_json["og_image"]

                    // Kiểm tra nếu ogImage là một List (JsonArray) và không rỗng
                    if (ogImage is List<*> && ogImage.isNotEmpty()) {
                        // Lấy phần tử đầu tiên
                        val firstImage = ogImage[0]
                        // Kiểm tra phần tử đầu là Map và lấy "url"
                        if (firstImage is Map<*, *>) {
                            return firstImage["url"]?.toString() ?: ""
                        }
                    }
                }
                // Nếu là List (trường hợp API trả về []) thì bỏ qua, trả về rỗng
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

    val contentRaw: String
        get() = content.rendered

    fun getContentForWebView(): String {
        val css = """
            <style>
                body { 
                    font-size: 16px; 
                    line-height: 1.6; 
                    color: #333333; 
                    padding: 0; 
                    margin: 0; 
                    font-family: sans-serif;
                }
                img { 
                    max-width: 100%; 
                    height: auto; 
                    border-radius: 8px; 
                    margin: 10px 0; 
                    display: block;
                }
                p { margin-bottom: 12px; }
            </style>
        """.trimIndent()

        return """
            <html>
            <head><meta name="viewport" content="width=device-width, initial-scale=1.0">$css</head>
            <body>${content.rendered}</body>
            </html>
        """.trimIndent()
    }

    val contentPlainText: String
        get() {
            if (content.rendered.isEmpty()) return ""
            var text = content.rendered.replace("<br>", "\n").replace("</p>", "\n\n")
            val htmlPattern = Pattern.compile("<[^>]+>")
            text = htmlPattern.matcher(text).replaceAll("")
            text = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
            return text.trim()
        }
}
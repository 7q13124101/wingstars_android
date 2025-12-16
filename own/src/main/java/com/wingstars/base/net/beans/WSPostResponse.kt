package com.wingstars.base.net.beans

import java.io.Serializable


data class WSPostResponse(
    val id: Int,
    val date: String,                   //日期
    val title: Title,                   //標題
    val content: Content,               //內文
    val link: String,
    val yoast_head_json: YoastHeadJson, //圖片
) : Serializable {
    val titleF: String                  //title format
        get() {
            return title.rendered
        }

    val dateF: String                   //date format
        get() {
            val d = if (date == null) {
                ""
            } else if (date.length > 10) {
                date.substring(0, 10)
            } else {
                date
            }

            return d.replace('-', '.')
        }

    val contentF: String                //content format
        get() {
            return content.rendered
        }

    val urlF: String                    //yoast_head_json.og_image[0].url format
        get() {
            return if(yoast_head_json.og_image.count() > 0){
                yoast_head_json.og_image[0].url
            } else {
                ""
            }
        }

    data class Title(
        val rendered: String,           //活動標題
    ) : java.io.Serializable

    data class Content(
        val rendered: String,           //活動資訊
    ) : java.io.Serializable

    data class YoastHeadJson(
        val og_image: List<OGImage>,    //圖片
    ) : java.io.Serializable {
        data class OGImage(
            val width: Int,
            val height: Int,
            val url: String,            //圖片地址
            val type: String,
        ) : java.io.Serializable
    }
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
                iframe { max-width: 100%; }
                figure { margin: 0; }
            </style>
        """.trimIndent()

        return """
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                $css
            </head>
            <body>
                ${content.rendered}
            </body>
            </html>
        """.trimIndent()
    }
}

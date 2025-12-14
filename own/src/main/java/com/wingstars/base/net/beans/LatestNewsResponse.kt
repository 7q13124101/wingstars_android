package com.wingstars.base.net.beans

import java.io.Serializable
import java.util.regex.Pattern

data class LatestNewsResponse(
    val id: Int,
    val date: String,
    val link: String,
    val title: Title,
    val content: Content,
    val yoast_head_json: YoastHeadJson,
) : Serializable {

    // ... (Các data class Title, Content, YoastHeadJson giữ nguyên như cũ) ...
    data class Title(val rendered: String) : Serializable
    data class Content(val rendered: String) : Serializable
    data class YoastHeadJson(val canonical: String, val og_image: List<OgImage>?) : Serializable {
        data class OgImage(val url: String) : Serializable
    }
    val dateF: String
        get() {
            return if(date.length > 10) {
                date.substring(0, 10)
            } else {
                date
            }
        }

    val titleF: String
        get() {
            return if(title?.rendered != null) {
                title?.rendered!!
            } else {
                ""
            }
        }

    val imageF: String
        get() = yoast_head_json
            ?.og_image
            ?.firstOrNull()
            ?.url
            .orEmpty()

    // ... (Các hàm dateF, titleF, imageF giữ nguyên) ...

    // --- THÊM CÁC HÀM XỬ LÝ CONTENT DƯỚI ĐÂY ---

    /**
     * 1. Lấy nội dung HTML thô (Raw HTML)
     * Dùng để load vào WebView.
     */
    val contentRaw: String
        get() = content.rendered

    /**
     * 2. Lấy nội dung HTML đã được tối ưu hóa cho Mobile (WebView)
     * - Thêm CSS để ảnh không bị tràn màn hình.
     * - Chỉnh font chữ, khoảng cách dòng.
     */
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

    /**
     * 3. Lấy nội dung văn bản thuần túy (Plain Text)
     * - Loại bỏ hết các thẻ HTML (<p>, <img>, <a>...).
     * - Dùng khi bạn muốn hiển thị tóm tắt (preview) trên TextView.
     */
    val contentPlainText: String
        get() {
            if (content.rendered.isEmpty()) return ""

            // Thay thế các thẻ xuống dòng phổ biến bằng khoảng trắng hoặc xuống dòng thật
            var text = content.rendered
                .replace("<br>", "\n")
                .replace("</p>", "\n\n")

            // Loại bỏ tất cả các thẻ HTML còn lại
            val htmlPattern = Pattern.compile("<[^>]+>")
            text = htmlPattern.matcher(text).replaceAll("")

            // Giải mã các ký tự đặc biệt (như &nbsp;)
            text = android.text.Html.fromHtml(text, android.text.Html.FROM_HTML_MODE_LEGACY).toString()

            return text.trim()
        }
}

/* Response:
[
    {
        "id": 71473,
        "date": "2025-09-10T11:52:21",
        "date_gmt": "2025-09-10T03:52:21",
        "guid": {
            "rendered": "https://61.218.209.209/?p=71473"
        },
        "modified": "2025-10-17T01:31:41",
        "modified_gmt": "2025-10-16T17:31:41",
        "slug": "%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80",
        "status": "publish",
        "type": "post",
        "link": "https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/",
        "title": {
            "rendered": "南大附小130週年 台鋼獵鷹球星現身激發學子追夢　"
        },
        "content": {
            "rendered": "\n<p>〔記者王姝琇／台南報導〕為迎接創校130週年，南大附小今（25）日舉辦「球夢起飛．與鷹同行」籃球交流活動，特邀PLG台鋼獵鷹球星蓋比、李允傑親自指導，帶領學生做技術訓練與5對5實戰對抗，讓孩子們零距離體驗職籃等級的專業風采。</p>\n\n\n\n<p>蓋比與李允傑指導學生，也談及自身成長歷程與訓練心法，鼓勵學生勇於追夢、堅持到底。南大附小男籃校隊全程投入，透過職業球員的親自示範與互動教學，到分組對抗賽實戰演練，現場氣氛熱烈，也展現出小球員們高度熱情與團隊精神。</p>\n\n\n\n<figure class=\"wp-block-image size-large\"><img loading=\"lazy\" decoding=\"async\" width=\"1024\" height=\"683\" src=\"https://tmedia22645531.blob.core.windows.net/wp-media/2025/09/541515672_18049334528643694_3499476786022436988_n-1024x683.jpg\" alt=\"\" class=\"wp-image-71476\" srcset=\"https://.blob.core.windows.net/wp-media/2025/09/541515672_18049334528643694_3499476786022436988_n-1024x683.jpg 1024w, https://.blob.core.windows.net/wp-media/2025/09/541515672_18049334528643694_3499476786022436988_n-300x200.jpg 300w, https://.blob.core.windows.net/wp-media/2025/09/541515672_18049334528643694_3499476786022436988_n-768x513.jpg 768w, https://.blob.core.windows.net/wp-media/2025/09/541515672_18049334528643694_3499476786022436988_n-600x400.jpg 600w, https://.blob.core.windows.net/wp-media/2025/09/541515672_18049334528643694_3499476786022436988_n.jpg 1440w\" sizes=\"(max-width: 1024px) 100vw, 1024px\" /></figure>\n\n\n\n<p>南大附小校長楊怡婷表示，希望孩子們能把握難得的學習機會，從職業球員的身上學到的不只是球技，更是面對困難時堅持到底的態度，勇敢追夢、熱愛挑戰，這正是130週年校慶精神的最佳展現。</p>\n\n\n\n<p>楊怡婷說，男生籃球校隊歷年來不僅在多項賽事中屢創佳績、為校爭光，亦為本校歷史悠久且具代表性的特色體育團隊。為彰顯傳承精神，本次籃球交流活動特別作為創校130週年系列活動的首發，旨在提升學生的運動素養，激發籃球熱情，並拓展視野，開啟屬於南大附小學子的熱血籃球篇章。</p>\n\n\n\n<p>贊助單位群益金鼎證券資深副總裁劉向麗開幕致詞時，勉勵學生勇敢追夢、不畏挑戰，也肯定南大附小對運動教育的重視與用心；同時也感謝台鋼獵鷹球團公關主任莊于瑩，與其團隊全力協助活動規劃與執行，促成本次交流圓滿順利。</p>\n\n\n\n<div class=\"wp-block-cover\"><span aria-hidden=\"true\" class=\"wp-block-cover__background has-background-dim\"></span><img loading=\"lazy\" decoding=\"async\" width=\"800\" height=\"576\" class=\"wp-block-cover__image-background wp-image-71561\" alt=\"\" src=\"https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢-2.jpg\" data-object-fit=\"cover\" srcset=\"https://.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢-2.jpg 800w, https://.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢-2-300x216.jpg 300w, https://.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢-2-768x553.jpg 768w, https://.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢-2-600x432.jpg 600w\" sizes=\"(max-width: 800px) 100vw, 800px\" /><div class=\"wp-block-cover__inner-container is-layout-flow wp-block-cover-is-layout-flow\">\n<p class=\"has-text-align-center has-large-font-size\"></p>\n</div></div>\n\n\n\n<p class=\"has-text-align-center\">南大附小今（25）日舉辦「球夢起飛．與鷹同行」籃球交流活動。（南大附小提供）</p>\n\n\n\n<figure class=\"wp-block-image size-full\"><img loading=\"lazy\" decoding=\"async\" width=\"800\" height=\"532\" src=\"https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢-3.jpg\" alt=\"\" class=\"wp-image-71562\" srcset=\"https://.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢-3.jpg 800w, https://.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢-3-300x200.jpg 300w, https://.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢-3-768x511.jpg 768w, https://.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢-3-600x399.jpg 600w\" sizes=\"(max-width: 800px) 100vw, 800px\" /></figure>\n\n\n\n<p class=\"has-text-align-center\">PLG台鋼獵鷹球星蓋比親自指導南大附小男籃校隊訓練。（南大附小提供）</p>\n",
            "protected": false
        },
        "excerpt": {
            "rendered": "<p>〔記者王姝琇／台南報導〕為迎接創校130週年，南大附小今（25）日舉辦「球夢起飛．與鷹同行」籃球交流活動，特邀 [&hellip;]</p>\n",
            "protected": false
        },
        "author": 17969,
        "featured_media": 71563,
        "comment_status": "closed",
        "ping_status": "closed",
        "sticky": false,
        "template": "",
        "format": "standard",
        "meta": {
            "_monsterinsights_skip_tracking": false,
            "_monsterinsights_sitenote_active": false,
            "_monsterinsights_sitenote_note": "",
            "_monsterinsights_sitenote_category": 0,
            "footnotes": ""
        },
        "categories": [
            23
        ],
        "tags": [
            352,
            351
        ],
        "class_list": [
            "post-71473",
            "post",
            "type-post",
            "status-publish",
            "format-standard",
            "has-post-thumbnail",
            "hentry",
            "category-pellet",
            "tag-tsgghosthawks",
            "tag-351",
            "entry",
            "has-media",
            "owp-thumbs-layout-horizontal",
            "owp-btn-normal",
            "owp-tabs-layout-horizontal",
            "has-no-thumbnails",
            "has-product-nav"
        ],
        "acf": [],
        "yoast_head": "<!-- This site is optimized with the Yoast SEO plugin v20.10 - https://yoast.com/wordpress/plugins/seo/ -->\n<title>南大附小130週年 台鋼獵鷹球星現身激發學子追夢　 - TSG WingStars</title>\n<!-- Admin only notice: this page does not show a meta description because it does not have one, either write it for this page specifically or go into the [Yoast SEO - 設定] menu and set up a template. -->\n<meta name=\"robots\" content=\"index, follow, max-snippet:-1, max-image-preview:large, max-video-preview:-1\" />\n<link rel=\"canonical\" href=\"https://61.218.209.209/南大附小130週年-台鋼獵鷹球星現身激發學子追夢　/\" />\n<meta property=\"og:locale\" content=\"zh_TW\" />\n<meta property=\"og:type\" content=\"article\" />\n<meta property=\"og:title\" content=\"南大附小130週年 台鋼獵鷹球星現身激發學子追夢　 - TSG WingStars\" />\n<meta property=\"og:description\" content=\"〔記者王姝琇／台南報導〕為迎接創校130週年，南大附小今（25）日舉辦「球夢起飛．與鷹同行」籃球交流活動，特邀 [&hellip;]\" />\n<meta property=\"og:url\" content=\"https://61.218.209.209/南大附小130週年-台鋼獵鷹球星現身激發學子追夢　/\" />\n<meta property=\"og:site_name\" content=\"TSG WingStars\" />\n<meta property=\"article:publisher\" content=\"https://www.facebook.com/TSGHAWKS/\" />\n<meta property=\"article:published_time\" content=\"2025-09-10T03:52:21+00:00\" />\n<meta property=\"article:modified_time\" content=\"2025-10-16T17:31:41+00:00\" />\n<meta property=\"og:image\" content=\"https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢.jpg\" />\n\t<meta property=\"og:image:width\" content=\"800\" />\n\t<meta property=\"og:image:height\" content=\"532\" />\n\t<meta property=\"og:image:type\" content=\"image/jpeg\" />\n<meta name=\"author\" content=\"newsoftst\" />\n<meta name=\"twitter:card\" content=\"summary_large_image\" />\n<meta name=\"twitter:label1\" content=\"Written by\" />\n\t<meta name=\"twitter:data1\" content=\"newsoftst\" />\n\t<meta name=\"twitter:label2\" content=\"預估閱讀時間\" />\n\t<meta name=\"twitter:data2\" content=\"1 分鐘\" />\n<script type=\"application/ld+json\" class=\"yoast-schema-graph\">{\"@context\":\"https://schema.org\",\"@graph\":[{\"@type\":\"Article\",\"@id\":\"https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/#article\",\"isPartOf\":{\"@id\":\"https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/\"},\"author\":{\"name\":\"newsoftst\",\"@id\":\"https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43\"},\"headline\":\"南大附小130週年 台鋼獵鷹球星現身激發學子追夢　\",\"datePublished\":\"2025-09-10T03:52:21+00:00\",\"dateModified\":\"2025-10-16T17:31:41+00:00\",\"mainEntityOfPage\":{\"@id\":\"https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/\"},\"wordCount\":2,\"publisher\":{\"@id\":\"https://61.218.209.209/#organization\"},\"keywords\":[\"TSGGhostHawks\",\"台鋼獵鷹\"],\"articleSection\":[\"球團\"],\"inLanguage\":\"zh-TW\"},{\"@type\":\"WebPage\",\"@id\":\"https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/\",\"url\":\"https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/\",\"name\":\"南大附小130週年 台鋼獵鷹球星現身激發學子追夢　 - TSG WingStars\",\"isPartOf\":{\"@id\":\"https://61.218.209.209/#website\"},\"datePublished\":\"2025-09-10T03:52:21+00:00\",\"dateModified\":\"2025-10-16T17:31:41+00:00\",\"breadcrumb\":{\"@id\":\"https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/#breadcrumb\"},\"inLanguage\":\"zh-TW\",\"potentialAction\":[{\"@type\":\"ReadAction\",\"target\":[\"https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/\"]}]},{\"@type\":\"BreadcrumbList\",\"@id\":\"https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/#breadcrumb\",\"itemListElement\":[{\"@type\":\"ListItem\",\"position\":1,\"name\":\"首頁\",\"item\":\"https://61.218.209.209/\"},{\"@type\":\"ListItem\",\"position\":2,\"name\":\"南大附小130週年 台鋼獵鷹球星現身激發學子追夢　\"}]},{\"@type\":\"WebSite\",\"@id\":\"https://61.218.209.209/#website\",\"url\":\"https://61.218.209.209/\",\"name\":\"TSG WingStars\",\"description\":\"台鋼啦啦隊\",\"publisher\":{\"@id\":\"https://61.218.209.209/#organization\"},\"potentialAction\":[{\"@type\":\"SearchAction\",\"target\":{\"@type\":\"EntryPoint\",\"urlTemplate\":\"https://61.218.209.209/?s={search_term_string}\"},\"query-input\":\"required name=search_term_string\"}],\"inLanguage\":\"zh-TW\"},{\"@type\":\"Organization\",\"@id\":\"https://61.218.209.209/#organization\",\"name\":\"TSG WingStars\",\"url\":\"https://61.218.209.209/\",\"logo\":{\"@type\":\"ImageObject\",\"inLanguage\":\"zh-TW\",\"@id\":\"https://61.218.209.209/#/schema/logo/image/\",\"url\":\"https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg\",\"contentUrl\":\"https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg\",\"width\":778,\"height\":428,\"caption\":\"TSG WingStars\"},\"image\":{\"@id\":\"https://61.218.209.209/#/schema/logo/image/\"},\"sameAs\":[\"https://www.facebook.com/TSGHAWKS/\",\"https://www.instagram.com/tsg_hawks/\"]},{\"@type\":\"Person\",\"@id\":\"https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43\",\"name\":\"newsoftst\",\"image\":{\"@type\":\"ImageObject\",\"inLanguage\":\"zh-TW\",\"@id\":\"https://61.218.209.209/#/schema/person/image/\",\"url\":\"https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g\",\"contentUrl\":\"https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g\",\"caption\":\"newsoftst\"}}]}</script>\n<!-- / Yoast SEO plugin. -->",
        "yoast_head_json": {
            "title": "南大附小130週年 台鋼獵鷹球星現身激發學子追夢　 - TSG WingStars",
            "robots": {
                "index": "index",
                "follow": "follow",
                "max-snippet": "max-snippet:-1",
                "max-image-preview": "max-image-preview:large",
                "max-video-preview": "max-video-preview:-1"
            },
            "canonical": "https://61.218.209.209/南大附小130週年-台鋼獵鷹球星現身激發學子追夢　/",
            "og_locale": "zh_TW",
            "og_type": "article",
            "og_title": "南大附小130週年 台鋼獵鷹球星現身激發學子追夢　 - TSG WingStars",
            "og_description": "〔記者王姝琇／台南報導〕為迎接創校130週年，南大附小今（25）日舉辦「球夢起飛．與鷹同行」籃球交流活動，特邀 [&hellip;]",
            "og_url": "https://61.218.209.209/南大附小130週年-台鋼獵鷹球星現身激發學子追夢　/",
            "og_site_name": "TSG WingStars",
            "article_publisher": "https://www.facebook.com/TSGHAWKS/",
            "article_published_time": "2025-09-10T03:52:21+00:00",
            "article_modified_time": "2025-10-16T17:31:41+00:00",
            "og_image": [
                {
                    "width": 800,
                    "height": 532,
                    "url": "https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/09/南大附小130週年-台鋼獵鷹球星現身激發學子追夢.jpg",
                    "type": "image/jpeg"
                }
            ],
            "author": "newsoftst",
            "twitter_card": "summary_large_image",
            "twitter_misc": {
                "Written by": "newsoftst",
                "預估閱讀時間": "1 分鐘"
            },
            "schema": {
                "@context": "https://schema.org",
                "@graph": [
                    {
                        "@type": "Article",
                        "@id": "https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/#article",
                        "isPartOf": {
                            "@id": "https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/"
                        },
                        "author": {
                            "name": "newsoftst",
                            "@id": "https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43"
                        },
                        "headline": "南大附小130週年 台鋼獵鷹球星現身激發學子追夢　",
                        "datePublished": "2025-09-10T03:52:21+00:00",
                        "dateModified": "2025-10-16T17:31:41+00:00",
                        "mainEntityOfPage": {
                            "@id": "https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/"
                        },
                        "wordCount": 2,
                        "publisher": {
                            "@id": "https://61.218.209.209/#organization"
                        },
                        "keywords": [
                            "TSGGhostHawks",
                            "台鋼獵鷹"
                        ],
                        "articleSection": [
                            "球團"
                        ],
                        "inLanguage": "zh-TW"
                    },
                    {
                        "@type": "WebPage",
                        "@id": "https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/",
                        "url": "https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/",
                        "name": "南大附小130週年 台鋼獵鷹球星現身激發學子追夢　 - TSG WingStars",
                        "isPartOf": {
                            "@id": "https://61.218.209.209/#website"
                        },
                        "datePublished": "2025-09-10T03:52:21+00:00",
                        "dateModified": "2025-10-16T17:31:41+00:00",
                        "breadcrumb": {
                            "@id": "https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/#breadcrumb"
                        },
                        "inLanguage": "zh-TW",
                        "potentialAction": [
                            {
                                "@type": "ReadAction",
                                "target": [
                                    "https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/"
                                ]
                            }
                        ]
                    },
                    {
                        "@type": "BreadcrumbList",
                        "@id": "https://61.218.209.209/%e5%8d%97%e5%a4%a7%e9%99%84%e5%b0%8f130%e9%80%b1%e5%b9%b4-%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e7%90%83%e6%98%9f%e7%8f%be%e8%ba%ab%e6%bf%80%e7%99%bc%e5%ad%b8%e5%ad%90%e8%bf%bd%e5%a4%a2%e3%80%80/#breadcrumb",
                        "itemListElement": [
                            {
                                "@type": "ListItem",
                                "position": 1,
                                "name": "首頁",
                                "item": "https://61.218.209.209/"
                            },
                            {
                                "@type": "ListItem",
                                "position": 2,
                                "name": "南大附小130週年 台鋼獵鷹球星現身激發學子追夢　"
                            }
                        ]
                    },
                    {
                        "@type": "WebSite",
                        "@id": "https://61.218.209.209/#website",
                        "url": "https://61.218.209.209/",
                        "name": "TSG WingStars",
                        "description": "台鋼啦啦隊",
                        "publisher": {
                            "@id": "https://61.218.209.209/#organization"
                        },
                        "potentialAction": [
                            {
                                "@type": "SearchAction",
                                "target": {
                                    "@type": "EntryPoint",
                                    "urlTemplate": "https://61.218.209.209/?s={search_term_string}"
                                },
                                "query-input": "required name=search_term_string"
                            }
                        ],
                        "inLanguage": "zh-TW"
                    },
                    {
                        "@type": "Organization",
                        "@id": "https://61.218.209.209/#organization",
                        "name": "TSG WingStars",
                        "url": "https://61.218.209.209/",
                        "logo": {
                            "@type": "ImageObject",
                            "inLanguage": "zh-TW",
                            "@id": "https://61.218.209.209/#/schema/logo/image/",
                            "url": "https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg",
                            "contentUrl": "https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg",
                            "width": 778,
                            "height": 428,
                            "caption": "TSG WingStars"
                        },
                        "image": {
                            "@id": "https://61.218.209.209/#/schema/logo/image/"
                        },
                        "sameAs": [
                            "https://www.facebook.com/TSGHAWKS/",
                            "https://www.instagram.com/tsg_hawks/"
                        ]
                    },
                    {
                        "@type": "Person",
                        "@id": "https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43",
                        "name": "newsoftst",
                        "image": {
                            "@type": "ImageObject",
                            "inLanguage": "zh-TW",
                            "@id": "https://61.218.209.209/#/schema/person/image/",
                            "url": "https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g",
                            "contentUrl": "https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g",
                            "caption": "newsoftst"
                        }
                    }
                ]
            }
        },
        "_links": {
            "self": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/71473"
                }
            ],
            "collection": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts"
                }
            ],
            "about": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/types/post"
                }
            ],
            "author": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/users/17969"
                }
            ],
            "replies": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/comments?post=71473"
                }
            ],
            "version-history": [
                {
                    "count": 5,
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/71473/revisions"
                }
            ],
            "predecessor-version": [
                {
                    "id": 72054,
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/71473/revisions/72054"
                }
            ],
            "wp:featuredmedia": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/media/71563"
                }
            ],
            "wp:attachment": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/media?parent=71473"
                }
            ],
            "wp:term": [
                {
                    "taxonomy": "category",
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/categories?post=71473"
                },
                {
                    "taxonomy": "post_tag",
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/tags?post=71473"
                }
            ],
            "curies": [
                {
                    "name": "wp",
                    "href": "https://api.w.org/{rel}",
                    "templated": true
                }
            ]
        }
    },
    {
        "id": 69993,
        "date": "2025-06-30T10:33:00",
        "date_gmt": "2025-06-30T02:33:00",
        "guid": {
            "rendered": "https://20.189.240.127/?p=69993"
        },
        "modified": "2025-10-17T01:27:24",
        "modified_gmt": "2025-10-16T17:27:24",
        "slug": "plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8",
        "status": "publish",
        "type": "post",
        "link": "https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/",
        "title": {
            "rendered": "PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力"
        },
        "content": {
            "rendered": "\n<p>2025年P. LEAGUE+新人選秀會今（2）日登場，台鋼獵鷹手握榜眼籤，選進來自Blue Mountain Christian University，同時出身金華國中的潛力鋒衛<a href=\"https://tw.news.yahoo.com/tag/%E6%9E%97%E5%AE%87%E8%AC%99\">林宇謙</a>，展現球隊針對未來戰力布局的明確方向與決心。</p>\n\n\n\n<p>林宇謙身高192公分、體重88公斤，司職2、3號位之間的「搖擺人」，具備現代籃球所需的多功能性與戰術彈性。他擁有豐富的國際賽經驗，兼具出色的視野、節奏感與防守判斷，是獲得台鋼教練團高度評價的潛力新秀。</p>\n\n\n\n<p id=\"caption-attachment-46913\">台鋼天鷹本土球員/VOL</p>\n\n\n\n<p>儘管從國際舞台回到國內將面臨不同挑戰，陳建禎並不畏懼。他坦言，隊友大多為 2000 年後出生，年齡相差一輪，初期與新生代球員還不熟悉，但他有信心透過訓練與交流快速整合團隊。「其實今天很多人都是第一次見面，但我們 7 月 1 日就要正式開訓，接下來會有更多時間彼此磨合。」他也提到，球隊已安排選手宿舍與訓練計畫，能幫助年輕球員更快熟悉彼此，也讓他自己扮演好領導角色。「畢竟我年紀也擺在那了（笑），但希望能藉由經驗帶領球隊往前走。」</p>\n\n\n\n<p>回顧旅外生涯，陳建禎感性表示：「沒想到真的已經 10 年了，能在退役前回來參加台灣的職業聯賽，是我沒想過的事，也覺得現在回來的時機剛剛好。」他也感謝台鋼團隊的信任與支持，「球團給了我很大的信心，讓我可以專心準備新賽季、在場上發揮最好表現。」</p>\n\n\n\n<p>林宇謙自金華國中畢業後赴美發展，初期以禁區為主戰場，隨著技術進步，逐步轉型為內外兼備的鋒衛球員，並強化控球能力、外線投射與防守效率。</p>\n\n\n\n<p>曾入選U18中華隊培訓名單，惟因疫情影響未能披掛上陣，實戰潛能仍備受關注。</p>\n\n\n\n<p>台鋼獵鷹總教練柯納表示：「宇謙是一位能勝任多項任務的2、3號位球員，除了擁有十足的拼勁外，更重要的是他具備強烈的求知欲。」</p>\n\n\n\n<p>此外，柯納說，「他的身體素質出色，球風也非常適合我們的體系。現階段，他的首要任務是全力投入訓練、爭取上場機會。雖然需要一些時間適應職業節奏，但我對他充滿信心，相信他能在本季為球隊後場帶來深度與活力。」</p>\n\n\n\n<p></p>\n",
            "protected": false
        },
        "excerpt": {
            "rendered": "<p>2025年P. LEAGUE+新人選秀會今（2）日登場，台鋼獵鷹手握榜眼籤，選進來自Blue Mountain [&hellip;]</p>\n",
            "protected": false
        },
        "author": 17969,
        "featured_media": 71590,
        "comment_status": "closed",
        "ping_status": "closed",
        "sticky": false,
        "template": "",
        "format": "standard",
        "meta": {
            "_monsterinsights_skip_tracking": false,
            "_monsterinsights_sitenote_active": false,
            "_monsterinsights_sitenote_note": "",
            "_monsterinsights_sitenote_category": 0,
            "footnotes": ""
        },
        "categories": [
            24
        ],
        "tags": [
            352,
            351
        ],
        "class_list": [
            "post-69993",
            "post",
            "type-post",
            "status-publish",
            "format-standard",
            "has-post-thumbnail",
            "hentry",
            "category-event-registration",
            "tag-tsgghosthawks",
            "tag-351",
            "entry",
            "has-media",
            "owp-thumbs-layout-horizontal",
            "owp-btn-normal",
            "owp-tabs-layout-horizontal",
            "has-no-thumbnails",
            "has-product-nav"
        ],
        "acf": [],
        "yoast_head": "<!-- This site is optimized with the Yoast SEO plugin v20.10 - https://yoast.com/wordpress/plugins/seo/ -->\n<title>PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力 - TSG WingStars</title>\n<meta name=\"description\" content=\"台鋼天鷹職業排球隊昨（29）日舉辦陳建禎加盟記者會。從中國到日本旅外十年的資深國手陳建禎，分享他回台加盟的心路歷程，以及為何選擇在 TPVL 開創元年之際回到家鄉，肩負起領軍重任。\" />\n<meta name=\"robots\" content=\"index, follow, max-snippet:-1, max-image-preview:large, max-video-preview:-1\" />\n<link rel=\"canonical\" href=\"https://61.218.209.209/plg／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全/\" />\n<meta property=\"og:locale\" content=\"zh_TW\" />\n<meta property=\"og:type\" content=\"article\" />\n<meta property=\"og:title\" content=\"PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力 - TSG WingStars\" />\n<meta property=\"og:description\" content=\"台鋼天鷹職業排球隊昨（29）日舉辦陳建禎加盟記者會。從中國到日本旅外十年的資深國手陳建禎，分享他回台加盟的心路歷程，以及為何選擇在 TPVL 開創元年之際回到家鄉，肩負起領軍重任。\" />\n<meta property=\"og:url\" content=\"https://61.218.209.209/plg／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全/\" />\n<meta property=\"og:site_name\" content=\"TSG WingStars\" />\n<meta property=\"article:publisher\" content=\"https://www.facebook.com/TSGHAWKS/\" />\n<meta property=\"article:published_time\" content=\"2025-06-30T02:33:00+00:00\" />\n<meta property=\"article:modified_time\" content=\"2025-10-16T17:27:24+00:00\" />\n<meta property=\"og:image\" content=\"https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/06/PLG／台鋼獵鷹榜眼選旅美林宇謙-1.webp\" />\n\t<meta property=\"og:image:width\" content=\"959\" />\n\t<meta property=\"og:image:height\" content=\"502\" />\n\t<meta property=\"og:image:type\" content=\"image/webp\" />\n<meta name=\"author\" content=\"newsoftst\" />\n<meta name=\"twitter:card\" content=\"summary_large_image\" />\n<meta name=\"twitter:label1\" content=\"Written by\" />\n\t<meta name=\"twitter:data1\" content=\"newsoftst\" />\n\t<meta name=\"twitter:label2\" content=\"預估閱讀時間\" />\n\t<meta name=\"twitter:data2\" content=\"1 分鐘\" />\n<script type=\"application/ld+json\" class=\"yoast-schema-graph\">{\"@context\":\"https://schema.org\",\"@graph\":[{\"@type\":\"Article\",\"@id\":\"https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/#article\",\"isPartOf\":{\"@id\":\"https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/\"},\"author\":{\"name\":\"newsoftst\",\"@id\":\"https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43\"},\"headline\":\"PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力\",\"datePublished\":\"2025-06-30T02:33:00+00:00\",\"dateModified\":\"2025-10-16T17:27:24+00:00\",\"mainEntityOfPage\":{\"@id\":\"https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/\"},\"wordCount\":9,\"publisher\":{\"@id\":\"https://61.218.209.209/#organization\"},\"keywords\":[\"TSGGhostHawks\",\"台鋼獵鷹\"],\"articleSection\":[\"活動\"],\"inLanguage\":\"zh-TW\"},{\"@type\":\"WebPage\",\"@id\":\"https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/\",\"url\":\"https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/\",\"name\":\"PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力 - TSG WingStars\",\"isPartOf\":{\"@id\":\"https://61.218.209.209/#website\"},\"datePublished\":\"2025-06-30T02:33:00+00:00\",\"dateModified\":\"2025-10-16T17:27:24+00:00\",\"description\":\"台鋼天鷹職業排球隊昨（29）日舉辦陳建禎加盟記者會。從中國到日本旅外十年的資深國手陳建禎，分享他回台加盟的心路歷程，以及為何選擇在 TPVL 開創元年之際回到家鄉，肩負起領軍重任。\",\"breadcrumb\":{\"@id\":\"https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/#breadcrumb\"},\"inLanguage\":\"zh-TW\",\"potentialAction\":[{\"@type\":\"ReadAction\",\"target\":[\"https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/\"]}]},{\"@type\":\"BreadcrumbList\",\"@id\":\"https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/#breadcrumb\",\"itemListElement\":[{\"@type\":\"ListItem\",\"position\":1,\"name\":\"首頁\",\"item\":\"https://61.218.209.209/\"},{\"@type\":\"ListItem\",\"position\":2,\"name\":\"PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力\"}]},{\"@type\":\"WebSite\",\"@id\":\"https://61.218.209.209/#website\",\"url\":\"https://61.218.209.209/\",\"name\":\"TSG WingStars\",\"description\":\"台鋼啦啦隊\",\"publisher\":{\"@id\":\"https://61.218.209.209/#organization\"},\"potentialAction\":[{\"@type\":\"SearchAction\",\"target\":{\"@type\":\"EntryPoint\",\"urlTemplate\":\"https://61.218.209.209/?s={search_term_string}\"},\"query-input\":\"required name=search_term_string\"}],\"inLanguage\":\"zh-TW\"},{\"@type\":\"Organization\",\"@id\":\"https://61.218.209.209/#organization\",\"name\":\"TSG WingStars\",\"url\":\"https://61.218.209.209/\",\"logo\":{\"@type\":\"ImageObject\",\"inLanguage\":\"zh-TW\",\"@id\":\"https://61.218.209.209/#/schema/logo/image/\",\"url\":\"https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg\",\"contentUrl\":\"https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg\",\"width\":778,\"height\":428,\"caption\":\"TSG WingStars\"},\"image\":{\"@id\":\"https://61.218.209.209/#/schema/logo/image/\"},\"sameAs\":[\"https://www.facebook.com/TSGHAWKS/\",\"https://www.instagram.com/tsg_hawks/\"]},{\"@type\":\"Person\",\"@id\":\"https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43\",\"name\":\"newsoftst\",\"image\":{\"@type\":\"ImageObject\",\"inLanguage\":\"zh-TW\",\"@id\":\"https://61.218.209.209/#/schema/person/image/\",\"url\":\"https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g\",\"contentUrl\":\"https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g\",\"caption\":\"newsoftst\"}}]}</script>\n<!-- / Yoast SEO plugin. -->",
        "yoast_head_json": {
            "title": "PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力 - TSG WingStars",
            "description": "台鋼天鷹職業排球隊昨（29）日舉辦陳建禎加盟記者會。從中國到日本旅外十年的資深國手陳建禎，分享他回台加盟的心路歷程，以及為何選擇在 TPVL 開創元年之際回到家鄉，肩負起領軍重任。",
            "robots": {
                "index": "index",
                "follow": "follow",
                "max-snippet": "max-snippet:-1",
                "max-image-preview": "max-image-preview:large",
                "max-video-preview": "max-video-preview:-1"
            },
            "canonical": "https://61.218.209.209/plg／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全/",
            "og_locale": "zh_TW",
            "og_type": "article",
            "og_title": "PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力 - TSG WingStars",
            "og_description": "台鋼天鷹職業排球隊昨（29）日舉辦陳建禎加盟記者會。從中國到日本旅外十年的資深國手陳建禎，分享他回台加盟的心路歷程，以及為何選擇在 TPVL 開創元年之際回到家鄉，肩負起領軍重任。",
            "og_url": "https://61.218.209.209/plg／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全/",
            "og_site_name": "TSG WingStars",
            "article_publisher": "https://www.facebook.com/TSGHAWKS/",
            "article_published_time": "2025-06-30T02:33:00+00:00",
            "article_modified_time": "2025-10-16T17:27:24+00:00",
            "og_image": [
                {
                    "width": 959,
                    "height": 502,
                    "url": "https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/06/PLG／台鋼獵鷹榜眼選旅美林宇謙-1.webp",
                    "type": "image/webp"
                }
            ],
            "author": "newsoftst",
            "twitter_card": "summary_large_image",
            "twitter_misc": {
                "Written by": "newsoftst",
                "預估閱讀時間": "1 分鐘"
            },
            "schema": {
                "@context": "https://schema.org",
                "@graph": [
                    {
                        "@type": "Article",
                        "@id": "https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/#article",
                        "isPartOf": {
                            "@id": "https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/"
                        },
                        "author": {
                            "name": "newsoftst",
                            "@id": "https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43"
                        },
                        "headline": "PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力",
                        "datePublished": "2025-06-30T02:33:00+00:00",
                        "dateModified": "2025-10-16T17:27:24+00:00",
                        "mainEntityOfPage": {
                            "@id": "https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/"
                        },
                        "wordCount": 9,
                        "publisher": {
                            "@id": "https://61.218.209.209/#organization"
                        },
                        "keywords": [
                            "TSGGhostHawks",
                            "台鋼獵鷹"
                        ],
                        "articleSection": [
                            "活動"
                        ],
                        "inLanguage": "zh-TW"
                    },
                    {
                        "@type": "WebPage",
                        "@id": "https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/",
                        "url": "https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/",
                        "name": "PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力 - TSG WingStars",
                        "isPartOf": {
                            "@id": "https://61.218.209.209/#website"
                        },
                        "datePublished": "2025-06-30T02:33:00+00:00",
                        "dateModified": "2025-10-16T17:27:24+00:00",
                        "description": "台鋼天鷹職業排球隊昨（29）日舉辦陳建禎加盟記者會。從中國到日本旅外十年的資深國手陳建禎，分享他回台加盟的心路歷程，以及為何選擇在 TPVL 開創元年之際回到家鄉，肩負起領軍重任。",
                        "breadcrumb": {
                            "@id": "https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/#breadcrumb"
                        },
                        "inLanguage": "zh-TW",
                        "potentialAction": [
                            {
                                "@type": "ReadAction",
                                "target": [
                                    "https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/"
                                ]
                            }
                        ]
                    },
                    {
                        "@type": "BreadcrumbList",
                        "@id": "https://61.218.209.209/plg%ef%bc%8f%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e6%a6%9c%e7%9c%bc%e9%81%b8%e6%97%85%e7%be%8e%e6%9e%97%e5%ae%87%e8%ac%99%ef%bc%81192%e5%85%ac%e5%88%86%e9%8b%92%e8%a1%9b%e5%b1%95%e7%8f%be%e5%85%a8/#breadcrumb",
                        "itemListElement": [
                            {
                                "@type": "ListItem",
                                "position": 1,
                                "name": "首頁",
                                "item": "https://61.218.209.209/"
                            },
                            {
                                "@type": "ListItem",
                                "position": 2,
                                "name": "PLG／台鋼獵鷹榜眼選旅美林宇謙！192公分鋒衛展現全能潛力"
                            }
                        ]
                    },
                    {
                        "@type": "WebSite",
                        "@id": "https://61.218.209.209/#website",
                        "url": "https://61.218.209.209/",
                        "name": "TSG WingStars",
                        "description": "台鋼啦啦隊",
                        "publisher": {
                            "@id": "https://61.218.209.209/#organization"
                        },
                        "potentialAction": [
                            {
                                "@type": "SearchAction",
                                "target": {
                                    "@type": "EntryPoint",
                                    "urlTemplate": "https://61.218.209.209/?s={search_term_string}"
                                },
                                "query-input": "required name=search_term_string"
                            }
                        ],
                        "inLanguage": "zh-TW"
                    },
                    {
                        "@type": "Organization",
                        "@id": "https://61.218.209.209/#organization",
                        "name": "TSG WingStars",
                        "url": "https://61.218.209.209/",
                        "logo": {
                            "@type": "ImageObject",
                            "inLanguage": "zh-TW",
                            "@id": "https://61.218.209.209/#/schema/logo/image/",
                            "url": "https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg",
                            "contentUrl": "https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg",
                            "width": 778,
                            "height": 428,
                            "caption": "TSG WingStars"
                        },
                        "image": {
                            "@id": "https://61.218.209.209/#/schema/logo/image/"
                        },
                        "sameAs": [
                            "https://www.facebook.com/TSGHAWKS/",
                            "https://www.instagram.com/tsg_hawks/"
                        ]
                    },
                    {
                        "@type": "Person",
                        "@id": "https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43",
                        "name": "newsoftst",
                        "image": {
                            "@type": "ImageObject",
                            "inLanguage": "zh-TW",
                            "@id": "https://61.218.209.209/#/schema/person/image/",
                            "url": "https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g",
                            "contentUrl": "https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g",
                            "caption": "newsoftst"
                        }
                    }
                ]
            }
        },
        "_links": {
            "self": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/69993"
                }
            ],
            "collection": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts"
                }
            ],
            "about": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/types/post"
                }
            ],
            "author": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/users/17969"
                }
            ],
            "replies": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/comments?post=69993"
                }
            ],
            "version-history": [
                {
                    "count": 4,
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/69993/revisions"
                }
            ],
            "predecessor-version": [
                {
                    "id": 71591,
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/69993/revisions/71591"
                }
            ],
            "wp:featuredmedia": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/media/71590"
                }
            ],
            "wp:attachment": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/media?parent=69993"
                }
            ],
            "wp:term": [
                {
                    "taxonomy": "category",
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/categories?post=69993"
                },
                {
                    "taxonomy": "post_tag",
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/tags?post=69993"
                }
            ],
            "curies": [
                {
                    "name": "wp",
                    "href": "https://api.w.org/{rel}",
                    "templated": true
                }
            ]
        }
    },
    {
        "id": 70147,
        "date": "2025-06-29T09:11:00",
        "date_gmt": "2025-06-29T01:11:00",
        "guid": {
            "rendered": "https://20.189.240.127/?p=70147"
        },
        "modified": "2025-10-17T01:27:52",
        "modified_gmt": "2025-10-16T17:27:52",
        "slug": "%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4",
        "status": "publish",
        "type": "post",
        "link": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/",
        "title": {
            "rendered": "台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館"
        },
        "content": {
            "rendered": "\n<p>記者杜奕君／綜合報導</p>\n\n\n\n<p>台鋼獵鷹球團今（8）日宣布，4月27日及28日將迎來本賽季主場季後賽，對戰台北富邦勇士，爭奪總冠軍賽門票。值得一提的是，獵鷹例行賽主場成功大學由於台南全中運及全大運賽事，檔期無法配合使用，季後賽首輪確定將主場移往鳳山體育館，並喊出「南人雄心」口號。</p>\n\n\n\n<p>由於原主場國立成功大學中正堂已排定全國中等學校運動會及全國大專校院運動會等全國性賽事，無法舉辦比賽，球團積極尋找台南適合的場館，但受限於檔期及座位數問題，未能找到合適場地。最終，為提供球迷更優質的觀賽體驗，決定將兩場季後賽移師至高雄市鳳山體育館，並期待南部球迷進場支持，共同為台鋼獵鷹加油！</p>\n\n\n\n<figure class=\"wp-block-image size-full\"><img loading=\"lazy\" decoding=\"async\" width=\"359\" height=\"450\" src=\"https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/06/台鋼獵鷹季後賽首輪宣布換主場-2.jpg\" alt=\"\" class=\"wp-image-71593\" srcset=\"https://.blob.core.windows.net/ghosthawks/2025/06/台鋼獵鷹季後賽首輪宣布換主場-2.jpg 359w, https://.blob.core.windows.net/ghosthawks/2025/06/台鋼獵鷹季後賽首輪宣布換主場-2-239x300.jpg 239w\" sizes=\"(max-width: 359px) 100vw, 359px\" /><figcaption class=\"wp-element-caption\"><strong>台鋼獵鷹季後賽首輪主場票價出爐。（圖／獵鷹提供）</strong></figcaption></figure>\n\n\n\n<p>▲台鋼獵鷹總經理王澄緯表示，關於季後賽主場的選定，球團經過多番考量後，最終確定以鳳山體育館作為主場。近年來，南部職業球隊的發展顯示出在地球迷對職業運動的高度支持，例如台鋼雄鷹的成立、統一獅的長期投入，以及高雄鋼鐵人還有台電企業排球的在地經營等，皆展現出對南部球迷的重視，也能看見南部人對運動的熱情與支持。</p>\n\n\n\n<p>此次台鋼獵鷹以台南為起點，與熱情城市高雄連結，秉持「南人雄心」精神，展現台鋼獵鷹全隊拼戰到底的決心，並肩負球迷的期待，力拚晉級並成功奪下PLG職籃南部球隊首冠，</p>\n\n\n\n<p></p>\n",
            "protected": false
        },
        "excerpt": {
            "rendered": "<p>記者杜奕君／綜合報導 台鋼獵鷹球團今（8）日宣布，4月27日及28日將迎來本賽季主場季後賽，對戰台北富邦勇士， [&hellip;]</p>\n",
            "protected": false
        },
        "author": 17969,
        "featured_media": 71592,
        "comment_status": "closed",
        "ping_status": "closed",
        "sticky": false,
        "template": "",
        "format": "standard",
        "meta": {
            "_monsterinsights_skip_tracking": false,
            "_monsterinsights_sitenote_active": false,
            "_monsterinsights_sitenote_note": "",
            "_monsterinsights_sitenote_category": 0,
            "footnotes": ""
        },
        "categories": [
            25
        ],
        "tags": [
            352,
            351
        ],
        "class_list": [
            "post-70147",
            "post",
            "type-post",
            "status-publish",
            "format-standard",
            "has-post-thumbnail",
            "hentry",
            "category-commodity",
            "tag-tsgghosthawks",
            "tag-351",
            "entry",
            "has-media",
            "owp-thumbs-layout-horizontal",
            "owp-btn-normal",
            "owp-tabs-layout-horizontal",
            "has-no-thumbnails",
            "has-product-nav"
        ],
        "acf": [],
        "yoast_head": "<!-- This site is optimized with the Yoast SEO plugin v20.10 - https://yoast.com/wordpress/plugins/seo/ -->\n<title>台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館 - TSG WingStars</title>\n<meta name=\"description\" content=\"台灣職業排球聯盟，台鋼天鷹29日正式宣布，前台灣男排主攻手、旅日球員，有「台灣隊長」之稱的陳建禎正式加盟，成為新賽季陣中最具份量的戰力，同時也將擔任球隊首任隊長，肩負領導年輕選手、建立球隊文化的重任。\" />\n<meta name=\"robots\" content=\"index, follow, max-snippet:-1, max-image-preview:large, max-video-preview:-1\" />\n<link rel=\"canonical\" href=\"https://61.218.209.209/台鋼獵鷹季後賽首輪宣布換主場/\" />\n<meta property=\"og:locale\" content=\"zh_TW\" />\n<meta property=\"og:type\" content=\"article\" />\n<meta property=\"og:title\" content=\"台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館 - TSG WingStars\" />\n<meta property=\"og:description\" content=\"台灣職業排球聯盟，台鋼天鷹29日正式宣布，前台灣男排主攻手、旅日球員，有「台灣隊長」之稱的陳建禎正式加盟，成為新賽季陣中最具份量的戰力，同時也將擔任球隊首任隊長，肩負領導年輕選手、建立球隊文化的重任。\" />\n<meta property=\"og:url\" content=\"https://61.218.209.209/台鋼獵鷹季後賽首輪宣布換主場/\" />\n<meta property=\"og:site_name\" content=\"TSG WingStars\" />\n<meta property=\"article:publisher\" content=\"https://www.facebook.com/TSGHAWKS/\" />\n<meta property=\"article:published_time\" content=\"2025-06-29T01:11:00+00:00\" />\n<meta property=\"article:modified_time\" content=\"2025-10-16T17:27:52+00:00\" />\n<meta property=\"og:image\" content=\"https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/06/台鋼獵鷹季後賽首輪宣布換主場-1.jpg\" />\n\t<meta property=\"og:image:width\" content=\"360\" />\n\t<meta property=\"og:image:height\" content=\"450\" />\n\t<meta property=\"og:image:type\" content=\"image/jpeg\" />\n<meta name=\"author\" content=\"newsoftst\" />\n<meta name=\"twitter:card\" content=\"summary_large_image\" />\n<meta name=\"twitter:label1\" content=\"Written by\" />\n\t<meta name=\"twitter:data1\" content=\"newsoftst\" />\n\t<meta name=\"twitter:label2\" content=\"預估閱讀時間\" />\n\t<meta name=\"twitter:data2\" content=\"1 分鐘\" />\n<script type=\"application/ld+json\" class=\"yoast-schema-graph\">{\"@context\":\"https://schema.org\",\"@graph\":[{\"@type\":\"Article\",\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/#article\",\"isPartOf\":{\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/\"},\"author\":{\"name\":\"newsoftst\",\"@id\":\"https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43\"},\"headline\":\"台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館\",\"datePublished\":\"2025-06-29T01:11:00+00:00\",\"dateModified\":\"2025-10-16T17:27:52+00:00\",\"mainEntityOfPage\":{\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/\"},\"wordCount\":1,\"publisher\":{\"@id\":\"https://61.218.209.209/#organization\"},\"keywords\":[\"TSGGhostHawks\",\"台鋼獵鷹\"],\"articleSection\":[\"商品\"],\"inLanguage\":\"zh-TW\"},{\"@type\":\"WebPage\",\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/\",\"url\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/\",\"name\":\"台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館 - TSG WingStars\",\"isPartOf\":{\"@id\":\"https://61.218.209.209/#website\"},\"datePublished\":\"2025-06-29T01:11:00+00:00\",\"dateModified\":\"2025-10-16T17:27:52+00:00\",\"description\":\"台灣職業排球聯盟，台鋼天鷹29日正式宣布，前台灣男排主攻手、旅日球員，有「台灣隊長」之稱的陳建禎正式加盟，成為新賽季陣中最具份量的戰力，同時也將擔任球隊首任隊長，肩負領導年輕選手、建立球隊文化的重任。\",\"breadcrumb\":{\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/#breadcrumb\"},\"inLanguage\":\"zh-TW\",\"potentialAction\":[{\"@type\":\"ReadAction\",\"target\":[\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/\"]}]},{\"@type\":\"BreadcrumbList\",\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/#breadcrumb\",\"itemListElement\":[{\"@type\":\"ListItem\",\"position\":1,\"name\":\"首頁\",\"item\":\"https://61.218.209.209/\"},{\"@type\":\"ListItem\",\"position\":2,\"name\":\"台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館\"}]},{\"@type\":\"WebSite\",\"@id\":\"https://61.218.209.209/#website\",\"url\":\"https://61.218.209.209/\",\"name\":\"TSG WingStars\",\"description\":\"台鋼啦啦隊\",\"publisher\":{\"@id\":\"https://61.218.209.209/#organization\"},\"potentialAction\":[{\"@type\":\"SearchAction\",\"target\":{\"@type\":\"EntryPoint\",\"urlTemplate\":\"https://61.218.209.209/?s={search_term_string}\"},\"query-input\":\"required name=search_term_string\"}],\"inLanguage\":\"zh-TW\"},{\"@type\":\"Organization\",\"@id\":\"https://61.218.209.209/#organization\",\"name\":\"TSG WingStars\",\"url\":\"https://61.218.209.209/\",\"logo\":{\"@type\":\"ImageObject\",\"inLanguage\":\"zh-TW\",\"@id\":\"https://61.218.209.209/#/schema/logo/image/\",\"url\":\"https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg\",\"contentUrl\":\"https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg\",\"width\":778,\"height\":428,\"caption\":\"TSG WingStars\"},\"image\":{\"@id\":\"https://61.218.209.209/#/schema/logo/image/\"},\"sameAs\":[\"https://www.facebook.com/TSGHAWKS/\",\"https://www.instagram.com/tsg_hawks/\"]},{\"@type\":\"Person\",\"@id\":\"https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43\",\"name\":\"newsoftst\",\"image\":{\"@type\":\"ImageObject\",\"inLanguage\":\"zh-TW\",\"@id\":\"https://61.218.209.209/#/schema/person/image/\",\"url\":\"https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g\",\"contentUrl\":\"https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g\",\"caption\":\"newsoftst\"}}]}</script>\n<!-- / Yoast SEO plugin. -->",
        "yoast_head_json": {
            "title": "台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館 - TSG WingStars",
            "description": "台灣職業排球聯盟，台鋼天鷹29日正式宣布，前台灣男排主攻手、旅日球員，有「台灣隊長」之稱的陳建禎正式加盟，成為新賽季陣中最具份量的戰力，同時也將擔任球隊首任隊長，肩負領導年輕選手、建立球隊文化的重任。",
            "robots": {
                "index": "index",
                "follow": "follow",
                "max-snippet": "max-snippet:-1",
                "max-image-preview": "max-image-preview:large",
                "max-video-preview": "max-video-preview:-1"
            },
            "canonical": "https://61.218.209.209/台鋼獵鷹季後賽首輪宣布換主場/",
            "og_locale": "zh_TW",
            "og_type": "article",
            "og_title": "台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館 - TSG WingStars",
            "og_description": "台灣職業排球聯盟，台鋼天鷹29日正式宣布，前台灣男排主攻手、旅日球員，有「台灣隊長」之稱的陳建禎正式加盟，成為新賽季陣中最具份量的戰力，同時也將擔任球隊首任隊長，肩負領導年輕選手、建立球隊文化的重任。",
            "og_url": "https://61.218.209.209/台鋼獵鷹季後賽首輪宣布換主場/",
            "og_site_name": "TSG WingStars",
            "article_publisher": "https://www.facebook.com/TSGHAWKS/",
            "article_published_time": "2025-06-29T01:11:00+00:00",
            "article_modified_time": "2025-10-16T17:27:52+00:00",
            "og_image": [
                {
                    "width": 360,
                    "height": 450,
                    "url": "https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/06/台鋼獵鷹季後賽首輪宣布換主場-1.jpg",
                    "type": "image/jpeg"
                }
            ],
            "author": "newsoftst",
            "twitter_card": "summary_large_image",
            "twitter_misc": {
                "Written by": "newsoftst",
                "預估閱讀時間": "1 分鐘"
            },
            "schema": {
                "@context": "https://schema.org",
                "@graph": [
                    {
                        "@type": "Article",
                        "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/#article",
                        "isPartOf": {
                            "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/"
                        },
                        "author": {
                            "name": "newsoftst",
                            "@id": "https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43"
                        },
                        "headline": "台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館",
                        "datePublished": "2025-06-29T01:11:00+00:00",
                        "dateModified": "2025-10-16T17:27:52+00:00",
                        "mainEntityOfPage": {
                            "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/"
                        },
                        "wordCount": 1,
                        "publisher": {
                            "@id": "https://61.218.209.209/#organization"
                        },
                        "keywords": [
                            "TSGGhostHawks",
                            "台鋼獵鷹"
                        ],
                        "articleSection": [
                            "商品"
                        ],
                        "inLanguage": "zh-TW"
                    },
                    {
                        "@type": "WebPage",
                        "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/",
                        "url": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/",
                        "name": "台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館 - TSG WingStars",
                        "isPartOf": {
                            "@id": "https://61.218.209.209/#website"
                        },
                        "datePublished": "2025-06-29T01:11:00+00:00",
                        "dateModified": "2025-10-16T17:27:52+00:00",
                        "description": "台灣職業排球聯盟，台鋼天鷹29日正式宣布，前台灣男排主攻手、旅日球員，有「台灣隊長」之稱的陳建禎正式加盟，成為新賽季陣中最具份量的戰力，同時也將擔任球隊首任隊長，肩負領導年輕選手、建立球隊文化的重任。",
                        "breadcrumb": {
                            "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/#breadcrumb"
                        },
                        "inLanguage": "zh-TW",
                        "potentialAction": [
                            {
                                "@type": "ReadAction",
                                "target": [
                                    "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/"
                                ]
                            }
                        ]
                    },
                    {
                        "@type": "BreadcrumbList",
                        "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e7%8d%b5%e9%b7%b9%e5%ad%a3%e5%be%8c%e8%b3%bd%e9%a6%96%e8%bc%aa%e5%ae%a3%e5%b8%83%e6%8f%9b%e4%b8%bb%e5%a0%b4/#breadcrumb",
                        "itemListElement": [
                            {
                                "@type": "ListItem",
                                "position": 1,
                                "name": "首頁",
                                "item": "https://61.218.209.209/"
                            },
                            {
                                "@type": "ListItem",
                                "position": 2,
                                "name": "台鋼獵鷹季後賽首輪宣布換主場　「南人雄心」前進鳳山體育館"
                            }
                        ]
                    },
                    {
                        "@type": "WebSite",
                        "@id": "https://61.218.209.209/#website",
                        "url": "https://61.218.209.209/",
                        "name": "TSG WingStars",
                        "description": "台鋼啦啦隊",
                        "publisher": {
                            "@id": "https://61.218.209.209/#organization"
                        },
                        "potentialAction": [
                            {
                                "@type": "SearchAction",
                                "target": {
                                    "@type": "EntryPoint",
                                    "urlTemplate": "https://61.218.209.209/?s={search_term_string}"
                                },
                                "query-input": "required name=search_term_string"
                            }
                        ],
                        "inLanguage": "zh-TW"
                    },
                    {
                        "@type": "Organization",
                        "@id": "https://61.218.209.209/#organization",
                        "name": "TSG WingStars",
                        "url": "https://61.218.209.209/",
                        "logo": {
                            "@type": "ImageObject",
                            "inLanguage": "zh-TW",
                            "@id": "https://61.218.209.209/#/schema/logo/image/",
                            "url": "https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg",
                            "contentUrl": "https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg",
                            "width": 778,
                            "height": 428,
                            "caption": "TSG WingStars"
                        },
                        "image": {
                            "@id": "https://61.218.209.209/#/schema/logo/image/"
                        },
                        "sameAs": [
                            "https://www.facebook.com/TSGHAWKS/",
                            "https://www.instagram.com/tsg_hawks/"
                        ]
                    },
                    {
                        "@type": "Person",
                        "@id": "https://61.218.209.209/#/schema/person/3ef9b6d9be3d20314691e4c14cbe2e43",
                        "name": "newsoftst",
                        "image": {
                            "@type": "ImageObject",
                            "inLanguage": "zh-TW",
                            "@id": "https://61.218.209.209/#/schema/person/image/",
                            "url": "https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g",
                            "contentUrl": "https://secure.gravatar.com/avatar/d06fa19de6cc4dcf6976461beaef9229?s=96&d=mm&r=g",
                            "caption": "newsoftst"
                        }
                    }
                ]
            }
        },
        "_links": {
            "self": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/70147"
                }
            ],
            "collection": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts"
                }
            ],
            "about": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/types/post"
                }
            ],
            "author": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/users/17969"
                }
            ],
            "replies": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/comments?post=70147"
                }
            ],
            "version-history": [
                {
                    "count": 4,
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/70147/revisions"
                }
            ],
            "predecessor-version": [
                {
                    "id": 71596,
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/70147/revisions/71596"
                }
            ],
            "wp:featuredmedia": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/media/71592"
                }
            ],
            "wp:attachment": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/media?parent=70147"
                }
            ],
            "wp:term": [
                {
                    "taxonomy": "category",
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/categories?post=70147"
                },
                {
                    "taxonomy": "post_tag",
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/tags?post=70147"
                }
            ],
            "curies": [
                {
                    "name": "wp",
                    "href": "https://api.w.org/{rel}",
                    "templated": true
                }
            ]
        }
    },
    {
        "id": 37571,
        "date": "2025-05-31T10:06:47",
        "date_gmt": "2025-05-31T02:06:47",
        "guid": {
            "rendered": "https://20.189.240.127/?p=37571"
        },
        "modified": "2025-10-17T01:30:52",
        "modified_gmt": "2025-10-16T17:30:52",
        "slug": "%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be",
        "status": "publish",
        "type": "post",
        "link": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/",
        "title": {
            "rendered": "PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷"
        },
        "content": {
            "rendered": "\n<p>台灣職籃P. LEAGUE+台北富邦勇士隊24日靠著賴廷恩（持球者）末節獨攬9分，助隊終場以91比79逆轉擊退台鋼獵鷹隊，在5戰3勝制季後賽首戰拔得頭籌。（台北富邦勇士提供）中央社記者黃巧雯傳真 114年4月24日</p>\n\n\n\n<p></p>\n\n\n\n<p>（中央社記者黃巧雯台北24日電）台灣職籃P. LEAGUE+台鋼獵鷹今天前3節幾乎一路領先，但在第4節崩盤，台北富邦勇士靠賴廷恩末節獨攬9分，以91比79逆轉勝出，在5戰3勝制季後賽首戰拔得頭籌。</p>\n\n\n\n<p>曾在PLG締造3連霸的勇士，上季無緣季後賽，本季重返季後賽行列，首輪對戰獵鷹，今天在主場台北和平籃球館展開5戰3勝制系列賽首戰。</p>\n\n\n\n<p>獵鷹開賽靠翟蒙（De&#8217;Mon Brooks）、白薩聯手拉出1波20比6攻勢，反觀主場作戰的勇士進攻當機，前9分鐘僅得6分，且單節出現7次失誤，讓獵鷹首節取得25比14領先。</p>\n\n\n\n<p>勇士次節展現禁區優勢，在威希（Jeff Withey）與沃特斯（Brandon Walters）帶領下，趁著獵鷹洋將翟蒙、鉑金（Nick Perkins）陷入犯規麻煩，追成34比39。</p>\n\n\n\n<p>勇士團隊三分球歷經前10投0中，靠著柏德（Jabari Bird）第3節還剩4分34分進球，團隊三分球總算開張，隨後洪楷傑再進1顆，而獵鷹憑藉著多點開花攻勢，帶著6分領先進入決勝節。</p>\n\n\n\n<p>勇士決勝節吹起反攻號角，先是靠著周桂羽在倒數6分37秒拋投得分，首度超前比分，隨後賴廷恩不僅與洪楷傑輪流提供火力支援，還扮演穿針引線的角色，單節送出4助攻，幫助球隊成功扭轉戰局，兩隊明天將在同場地進行第2戰。</p>\n\n\n\n<p>勇士賴廷恩攻下14分中，有9分集中在末節，成為球隊贏球功臣之一，威希、柏德各得18分、17分。</p>\n\n\n\n<p>獵鷹有5人得分雙位數，白薩15分最多，鉑金14分次之。（編輯：管中維）1140424</p>\n",
            "protected": false
        },
        "excerpt": {
            "rendered": "<p>台灣職籃P. LEAGUE+台北富邦勇士隊24日靠著賴廷恩（持球者）末節獨攬9分，助隊終場以91比79逆轉擊退 [&hellip;]</p>\n",
            "protected": false
        },
        "author": 8,
        "featured_media": 71584,
        "comment_status": "closed",
        "ping_status": "closed",
        "sticky": false,
        "template": "",
        "format": "standard",
        "meta": {
            "_monsterinsights_skip_tracking": false,
            "_monsterinsights_sitenote_active": false,
            "_monsterinsights_sitenote_note": "",
            "_monsterinsights_sitenote_category": 0,
            "footnotes": ""
        },
        "categories": [
            22
        ],
        "tags": [
            352,
            351
        ],
        "class_list": [
            "post-37571",
            "post",
            "type-post",
            "status-publish",
            "format-standard",
            "has-post-thumbnail",
            "hentry",
            "category-competition",
            "tag-tsgghosthawks",
            "tag-351",
            "entry",
            "has-media",
            "owp-thumbs-layout-horizontal",
            "owp-btn-normal",
            "owp-tabs-layout-horizontal",
            "has-no-thumbnails",
            "has-product-nav"
        ],
        "acf": [],
        "yoast_head": "<!-- This site is optimized with the Yoast SEO plugin v20.10 - https://yoast.com/wordpress/plugins/seo/ -->\n<title>PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷 - TSG WingStars</title>\n<meta name=\"description\" content=\"在台鋼天鷹職業排球隊首次公開測試會的現場，我們捕捉到一位身影格外忙碌的身影──來自荷蘭的翰克（Henk Gootjes）教練。他不僅親自觀察每一位球員的表現，還不時低頭記錄，時而與教練團成員低語討論。儘管語言不同，他卻用敏銳的觀察力與豐富的經驗，試圖從二十多位參與測試的選手中找出能夠成為天鷹一員的「關鍵拼圖」。\" />\n<meta name=\"robots\" content=\"index, follow, max-snippet:-1, max-image-preview:large, max-video-preview:-1\" />\n<link rel=\"canonical\" href=\"https://61.218.209.209/台鋼天鷹首辦測試會，總教練翰克：我們不只在找/\" />\n<meta property=\"og:locale\" content=\"zh_TW\" />\n<meta property=\"og:type\" content=\"article\" />\n<meta property=\"og:title\" content=\"PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷 - TSG WingStars\" />\n<meta property=\"og:description\" content=\"在台鋼天鷹職業排球隊首次公開測試會的現場，我們捕捉到一位身影格外忙碌的身影──來自荷蘭的翰克（Henk Gootjes）教練。他不僅親自觀察每一位球員的表現，還不時低頭記錄，時而與教練團成員低語討論。儘管語言不同，他卻用敏銳的觀察力與豐富的經驗，試圖從二十多位參與測試的選手中找出能夠成為天鷹一員的「關鍵拼圖」。\" />\n<meta property=\"og:url\" content=\"https://61.218.209.209/台鋼天鷹首辦測試會，總教練翰克：我們不只在找/\" />\n<meta property=\"og:site_name\" content=\"TSG WingStars\" />\n<meta property=\"article:publisher\" content=\"https://www.facebook.com/TSGHAWKS/\" />\n<meta property=\"article:published_time\" content=\"2025-05-31T02:06:47+00:00\" />\n<meta property=\"article:modified_time\" content=\"2025-10-16T17:30:52+00:00\" />\n<meta property=\"og:image\" content=\"https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/05/PLG勇士逆轉擊敗獵鷹.jpg\" />\n\t<meta property=\"og:image:width\" content=\"1024\" />\n\t<meta property=\"og:image:height\" content=\"768\" />\n\t<meta property=\"og:image:type\" content=\"image/jpeg\" />\n<meta name=\"twitter:card\" content=\"summary_large_image\" />\n<meta name=\"twitter:label1\" content=\"Written by\" />\n\t<meta name=\"twitter:data1\" content=\"\" />\n\t<meta name=\"twitter:label2\" content=\"預估閱讀時間\" />\n\t<meta name=\"twitter:data2\" content=\"1 分鐘\" />\n<script type=\"application/ld+json\" class=\"yoast-schema-graph\">{\"@context\":\"https://schema.org\",\"@graph\":[{\"@type\":\"Article\",\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/#article\",\"isPartOf\":{\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/\"},\"author\":{\"name\":\"\",\"@id\":\"\"},\"headline\":\"PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷\",\"datePublished\":\"2025-05-31T02:06:47+00:00\",\"dateModified\":\"2025-10-16T17:30:52+00:00\",\"mainEntityOfPage\":{\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/\"},\"wordCount\":16,\"publisher\":{\"@id\":\"https://61.218.209.209/#organization\"},\"keywords\":[\"TSGGhostHawks\",\"台鋼獵鷹\"],\"articleSection\":[\"賽事\"],\"inLanguage\":\"zh-TW\"},{\"@type\":\"WebPage\",\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/\",\"url\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/\",\"name\":\"PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷 - TSG WingStars\",\"isPartOf\":{\"@id\":\"https://61.218.209.209/#website\"},\"datePublished\":\"2025-05-31T02:06:47+00:00\",\"dateModified\":\"2025-10-16T17:30:52+00:00\",\"description\":\"在台鋼天鷹職業排球隊首次公開測試會的現場，我們捕捉到一位身影格外忙碌的身影──來自荷蘭的翰克（Henk Gootjes）教練。他不僅親自觀察每一位球員的表現，還不時低頭記錄，時而與教練團成員低語討論。儘管語言不同，他卻用敏銳的觀察力與豐富的經驗，試圖從二十多位參與測試的選手中找出能夠成為天鷹一員的「關鍵拼圖」。\",\"breadcrumb\":{\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/#breadcrumb\"},\"inLanguage\":\"zh-TW\",\"potentialAction\":[{\"@type\":\"ReadAction\",\"target\":[\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/\"]}]},{\"@type\":\"BreadcrumbList\",\"@id\":\"https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/#breadcrumb\",\"itemListElement\":[{\"@type\":\"ListItem\",\"position\":1,\"name\":\"首頁\",\"item\":\"https://61.218.209.209/\"},{\"@type\":\"ListItem\",\"position\":2,\"name\":\"PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷\"}]},{\"@type\":\"WebSite\",\"@id\":\"https://61.218.209.209/#website\",\"url\":\"https://61.218.209.209/\",\"name\":\"TSG WingStars\",\"description\":\"台鋼啦啦隊\",\"publisher\":{\"@id\":\"https://61.218.209.209/#organization\"},\"potentialAction\":[{\"@type\":\"SearchAction\",\"target\":{\"@type\":\"EntryPoint\",\"urlTemplate\":\"https://61.218.209.209/?s={search_term_string}\"},\"query-input\":\"required name=search_term_string\"}],\"inLanguage\":\"zh-TW\"},{\"@type\":\"Organization\",\"@id\":\"https://61.218.209.209/#organization\",\"name\":\"TSG WingStars\",\"url\":\"https://61.218.209.209/\",\"logo\":{\"@type\":\"ImageObject\",\"inLanguage\":\"zh-TW\",\"@id\":\"https://61.218.209.209/#/schema/logo/image/\",\"url\":\"https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg\",\"contentUrl\":\"https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg\",\"width\":778,\"height\":428,\"caption\":\"TSG WingStars\"},\"image\":{\"@id\":\"https://61.218.209.209/#/schema/logo/image/\"},\"sameAs\":[\"https://www.facebook.com/TSGHAWKS/\",\"https://www.instagram.com/tsg_hawks/\"]},{\"@type\":\"Person\",\"@id\":\"\"}]}</script>\n<!-- / Yoast SEO plugin. -->",
        "yoast_head_json": {
            "title": "PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷 - TSG WingStars",
            "description": "在台鋼天鷹職業排球隊首次公開測試會的現場，我們捕捉到一位身影格外忙碌的身影──來自荷蘭的翰克（Henk Gootjes）教練。他不僅親自觀察每一位球員的表現，還不時低頭記錄，時而與教練團成員低語討論。儘管語言不同，他卻用敏銳的觀察力與豐富的經驗，試圖從二十多位參與測試的選手中找出能夠成為天鷹一員的「關鍵拼圖」。",
            "robots": {
                "index": "index",
                "follow": "follow",
                "max-snippet": "max-snippet:-1",
                "max-image-preview": "max-image-preview:large",
                "max-video-preview": "max-video-preview:-1"
            },
            "canonical": "https://61.218.209.209/台鋼天鷹首辦測試會，總教練翰克：我們不只在找/",
            "og_locale": "zh_TW",
            "og_type": "article",
            "og_title": "PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷 - TSG WingStars",
            "og_description": "在台鋼天鷹職業排球隊首次公開測試會的現場，我們捕捉到一位身影格外忙碌的身影──來自荷蘭的翰克（Henk Gootjes）教練。他不僅親自觀察每一位球員的表現，還不時低頭記錄，時而與教練團成員低語討論。儘管語言不同，他卻用敏銳的觀察力與豐富的經驗，試圖從二十多位參與測試的選手中找出能夠成為天鷹一員的「關鍵拼圖」。",
            "og_url": "https://61.218.209.209/台鋼天鷹首辦測試會，總教練翰克：我們不只在找/",
            "og_site_name": "TSG WingStars",
            "article_publisher": "https://www.facebook.com/TSGHAWKS/",
            "article_published_time": "2025-05-31T02:06:47+00:00",
            "article_modified_time": "2025-10-16T17:30:52+00:00",
            "og_image": [
                {
                    "width": 1024,
                    "height": 768,
                    "url": "https://tmedia22645531.blob.core.windows.net/ghosthawks/2025/05/PLG勇士逆轉擊敗獵鷹.jpg",
                    "type": "image/jpeg"
                }
            ],
            "twitter_card": "summary_large_image",
            "twitter_misc": {
                "Written by": "",
                "預估閱讀時間": "1 分鐘"
            },
            "schema": {
                "@context": "https://schema.org",
                "@graph": [
                    {
                        "@type": "Article",
                        "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/#article",
                        "isPartOf": {
                            "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/"
                        },
                        "author": {
                            "name": "",
                            "@id": ""
                        },
                        "headline": "PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷",
                        "datePublished": "2025-05-31T02:06:47+00:00",
                        "dateModified": "2025-10-16T17:30:52+00:00",
                        "mainEntityOfPage": {
                            "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/"
                        },
                        "wordCount": 16,
                        "publisher": {
                            "@id": "https://61.218.209.209/#organization"
                        },
                        "keywords": [
                            "TSGGhostHawks",
                            "台鋼獵鷹"
                        ],
                        "articleSection": [
                            "賽事"
                        ],
                        "inLanguage": "zh-TW"
                    },
                    {
                        "@type": "WebPage",
                        "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/",
                        "url": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/",
                        "name": "PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷 - TSG WingStars",
                        "isPartOf": {
                            "@id": "https://61.218.209.209/#website"
                        },
                        "datePublished": "2025-05-31T02:06:47+00:00",
                        "dateModified": "2025-10-16T17:30:52+00:00",
                        "description": "在台鋼天鷹職業排球隊首次公開測試會的現場，我們捕捉到一位身影格外忙碌的身影──來自荷蘭的翰克（Henk Gootjes）教練。他不僅親自觀察每一位球員的表現，還不時低頭記錄，時而與教練團成員低語討論。儘管語言不同，他卻用敏銳的觀察力與豐富的經驗，試圖從二十多位參與測試的選手中找出能夠成為天鷹一員的「關鍵拼圖」。",
                        "breadcrumb": {
                            "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/#breadcrumb"
                        },
                        "inLanguage": "zh-TW",
                        "potentialAction": [
                            {
                                "@type": "ReadAction",
                                "target": [
                                    "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/"
                                ]
                            }
                        ]
                    },
                    {
                        "@type": "BreadcrumbList",
                        "@id": "https://61.218.209.209/%e5%8f%b0%e9%8b%bc%e5%a4%a9%e9%b7%b9%e9%a6%96%e8%be%a6%e6%b8%ac%e8%a9%a6%e6%9c%83%ef%bc%8c%e7%b8%bd%e6%95%99%e7%b7%b4%e7%bf%b0%e5%85%8b%ef%bc%9a%e6%88%91%e5%80%91%e4%b8%8d%e5%8f%aa%e5%9c%a8%e6%89%be/#breadcrumb",
                        "itemListElement": [
                            {
                                "@type": "ListItem",
                                "position": 1,
                                "name": "首頁",
                                "item": "https://61.218.209.209/"
                            },
                            {
                                "@type": "ListItem",
                                "position": 2,
                                "name": "PLG勇士逆轉擊敗獵鷹 季後賽首戰告捷"
                            }
                        ]
                    },
                    {
                        "@type": "WebSite",
                        "@id": "https://61.218.209.209/#website",
                        "url": "https://61.218.209.209/",
                        "name": "TSG WingStars",
                        "description": "台鋼啦啦隊",
                        "publisher": {
                            "@id": "https://61.218.209.209/#organization"
                        },
                        "potentialAction": [
                            {
                                "@type": "SearchAction",
                                "target": {
                                    "@type": "EntryPoint",
                                    "urlTemplate": "https://61.218.209.209/?s={search_term_string}"
                                },
                                "query-input": "required name=search_term_string"
                            }
                        ],
                        "inLanguage": "zh-TW"
                    },
                    {
                        "@type": "Organization",
                        "@id": "https://61.218.209.209/#organization",
                        "name": "TSG WingStars",
                        "url": "https://61.218.209.209/",
                        "logo": {
                            "@type": "ImageObject",
                            "inLanguage": "zh-TW",
                            "@id": "https://61.218.209.209/#/schema/logo/image/",
                            "url": "https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg",
                            "contentUrl": "https://61.218.209.209/wp-content/uploads/2022/11/TSG-e1669103418887.jpg",
                            "width": 778,
                            "height": 428,
                            "caption": "TSG WingStars"
                        },
                        "image": {
                            "@id": "https://61.218.209.209/#/schema/logo/image/"
                        },
                        "sameAs": [
                            "https://www.facebook.com/TSGHAWKS/",
                            "https://www.instagram.com/tsg_hawks/"
                        ]
                    },
                    {
                        "@type": "Person",
                        "@id": ""
                    }
                ]
            }
        },
        "_links": {
            "self": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/37571"
                }
            ],
            "collection": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts"
                }
            ],
            "about": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/types/post"
                }
            ],
            "author": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/users/8"
                }
            ],
            "replies": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/comments?post=37571"
                }
            ],
            "version-history": [
                {
                    "count": 8,
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/37571/revisions"
                }
            ],
            "predecessor-version": [
                {
                    "id": 71588,
                    "href": "https://61.218.209.209/wp-json/wp/v2/posts/37571/revisions/71588"
                }
            ],
            "wp:featuredmedia": [
                {
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/media/71584"
                }
            ],
            "wp:attachment": [
                {
                    "href": "https://61.218.209.209/wp-json/wp/v2/media?parent=37571"
                }
            ],
            "wp:term": [
                {
                    "taxonomy": "category",
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/categories?post=37571"
                },
                {
                    "taxonomy": "post_tag",
                    "embeddable": true,
                    "href": "https://61.218.209.209/wp-json/wp/v2/tags?post=37571"
                }
            ],
            "curies": [
                {
                    "name": "wp",
                    "href": "https://api.w.org/{rel}",
                    "templated": true
                }
            ]
        }
    }
]
 */
package com.wingstars.member.activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.wingstars.base.base.BaseActivity
import com.wingstars.base.net.NetBase
import com.wingstars.member.databinding.ActivityWebBinding
import java.util.regex.Pattern
import com.wingstars.member.R


class WebActivity : BaseActivity() {
    private lateinit var binding: ActivityWebBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setTitleFoot(binding.root, statusBarColor = R.color.white)
        initView()
    }

    override fun initView() {
        val stringExtra = intent.getStringExtra("title")
        val webUrl= intent.getStringExtra("webUrl")
        val fullText = intent.getStringExtra("fullText")
        fullText?.let {
        var replacedStr = replaceSrcWithDataUploadPath(fullText)
        //println("替换后的文本：\n$replacedStr")
        //Log.e("fullText","替换后的文本fullText=$replacedStr")
        }

        binding.title.setTitle(stringExtra!!)
        binding.title.setBackClickListener{
            if (binding.webView.canGoBack()) {
                binding.webView.goBack();
            } else {
                finish()
            }
        }
        // 2. 配置WebView设置（关键）
        val webSettings: WebSettings = binding.webView.settings
        webSettings.loadsImagesAutomatically = true
        // 允许混合内容（HTTPS页面加载HTTP图片）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webSettings.defaultTextEncodingName = "UTF-8" // 设置编码为UTF-8，避免中文乱码
        webSettings.javaScriptEnabled = true // 如果HTML中无JS，建议关闭以提升安全性
        webSettings.domStorageEnabled = true // 支持DOM存储，适配复杂HTML
        // 3. 准备包含HTML标签的内容
       // val htmlContent =  "<div style=\"text-align: center; background: #dcf5ff; padding: 20px 0;\">\n<h3>商業合作</h3>\n<h4><strong>若為各大品牌、外部廠商的合作邀約、各類物品/服務供應商，請依格式填寫您的需求，並寄到以下信箱，謝謝。</strong></h4>\n<h4><strong>E-mail</strong><strong>：</strong><strong><a href=\"mailto:bkcoop@burgerking.com.tw\">bkcoop@burgerking.com.tw</a></strong></h4>\n</div>\n<table style=\"border-collapse: collapse; width: 100%; height: 482.656px;\" border=\"1\"><colgroup><col style=\"width: 50%;\"><col style=\"width: 50%;\"></colgroup>\n<tbody>\n<tr style=\"height: 69.5312px;\">\n<td>\n<h4>合作類別</h4>\n</td>\n<td>&nbsp;</td>\n</tr>\n<tr style=\"height: 69.5312px;\">\n<td>\n<h4>單位名稱</h4>\n</td>\n<td>&nbsp;</td>\n</tr>\n<tr style=\"height: 69.5312px;\">\n<td>\n<h4><strong>負責窗口</strong></h4>\n</td>\n<td>&nbsp;</td>\n</tr>\n<tr style=\"height: 69.5312px;\">\n<td>\n<h4><strong>聯繫方式</strong></h4>\n</td>\n<td>&nbsp;</td>\n</tr>\n<tr style=\"height: 69.5312px;\">\n<td>\n<h4><strong>合作說明</strong></h4>\n</td>\n<td>&nbsp;</td>\n</tr>\n</tbody>\n</table>\n<p>&nbsp;</p>"
       // val  htmlContent  ="<div data-page-id=\"Mh86fSiH7dxAObcjQ1ccHgiDntb\" data-lark-html-role=\"root\" data-docx-has-block-data=\"false\">\n<h1 class=\"ace-line ace-line old-record-id-Mh86fSiH7dxAObcjQ1ccHgiDntb\">會員等級說明（案例版）</h1>\n<div class=\"ace-line ace-line old-record-id-Dmp9fztJJdnJoscri4Hc4Vacnwe\">歡迎加入【品牌名稱】會員體系！為感謝您的長期支持與信任，我們推出階梯式會員等級服務，依據會員成長值（消費、互動等行為累積）劃分不同等級，等級越高可享權益越豐厚。本體系旨在通過差異化的專屬服務與激勵，實現「會員因被重視而忠誠，品牌因忠誠而成長」的雙向共贏。以下是詳細的等級規則說明。</div>\n<h2 class=\"heading-2 ace-line old-record-id-AMS9fdrYLdqiNycWiwZcEt87nfb\">一、等級劃分與核心規則</h2>\n<div class=\"ace-line ace-line old-record-id-ARUJfVzN5diGT4cNaeKcbP1kn6g\">本會員體系共設5個等級，從低到高依次為：註冊會員、白銀會員、黃金會員、鉑金會員、鑽石會員。等級評定核心依據為「成長值」，成長值可通過消費、互動、老帶新等行為累積，具體累積規則見下文。所有等級均採用「動態管理機制」，每自然季度末（3月、6月、9月、12月最後一天）進行等級復核，根據近12個月的成長值情況調整會員等級，確保等級與會員價值匹配。</div>\n<div class=\"ace-line ace-line old-record-id-OemkfAQcedDUERc65cccylHJn0g\">成長值累積規則：</div>\n<ul class=\"list-bullet1\">\n<li class=\"ace-line ace-line old-record-id-PtZPf2AHPdC9T6clwvhcinFFnZd\" data-list=\"bullet\">\n<div>消費累積：每消費1元可累積1成長值（特殊商品如特價品、贈品除外，具體以訂單結算頁為準）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-EXGAfHSyvdz6QBcY8Hcc7j2PnUa\" data-list=\"bullet\">\n<div>互動累積：每日登錄官方平台（APP/小程序）可累積2成長值，發布有效評價（帶圖+10字以上）可累積10成長值，每月互動累積上限為100成長值；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-LgzNfGiondonh2cFvbYcWhYynCo\" data-list=\"bullet\">\n<div>老帶新累積：成功邀請1位新用戶註冊並完成首單消費，邀請者可累積50成長值，無每月上限；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-QcMzfV3wSdUIT2cHVRWcNU0envd\" data-list=\"bullet\">\n<div>特殊累積：參與品牌線下活動、新產品測評等專屬任務，可獲得50-200成長值不等的額外獎勵，具體以活動規則為準。</div>\n</li>\n</ul>\n<h2 class=\"heading-2 ace-line old-record-id-EJ9pfcWXsdAdTrcbWTSc10AcnYW\">二、各等級詳細說明</h2>\n<h3 class=\"heading-3 ace-line old-record-id-CEUnfoo05dpFUWcXqDdc4iXpnQd\">（一）註冊會員：入門即享，輕鬆開啟</h3>\n<div class=\"ace-line ace-line old-record-id-LAtgf6fmzdkU5ucUB2hcEZ3enZf\">1. 升級條件：完成官方平台註冊，無需累積成長值，註冊成功即自動激活；</div>\n<div class=\"ace-line ace-line old-record-id-Vp1vfGVc3d52ZTcJ0hQcukFpnbg\">2. 核心權益：</div>\n<ul class=\"list-bullet1\">\n<li class=\"ace-line ace-line old-record-id-EtU9fXzhcdhmF6cZ28UcpkkGndz\" data-list=\"bullet\">\n<div>基礎福利：註冊即享新人禮包（含3元無門檻券+兩張滿30減8元券，自註冊之日起7天內有效）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-NXhSff7tMdXFoscjmuCcXzXnnyb\" data-list=\"bullet\">\n<div>積分權益：消費可同步累積積分（1元=1積分），積分可用於兌換指定商品或抵扣現金（100積分=5元）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-BgVEfF8REdUdk4caLFuclpRsnof\" data-list=\"bullet\">\n<div>資訊權益：免費訂閱新產品資訊、會員專屬活動提醒；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-Z4TVfYZx2dye3mccwATcpjgLndb\" data-list=\"bullet\">\n<div>服務權益：享受官方標準客服服務，售後退換貨流程簡化。</div>\n</li>\n</ul>\n<h3 class=\"heading-3 ace-line old-record-id-C6xMfY6kJdwYNzcNvIBcFFIXnad\">（二）白銀會員：高頻暢享，實惠升級</h3>\n<div class=\"ace-line ace-line old-record-id-LRk4f5H3UdwRQ4ce9utcTnxOn7b\">1. 升級條件：近12個月內累積成長值滿500分；</div>\n<div class=\"ace-line ace-line old-record-id-IcPtfDzZYdA30bcHVMVcnoOinzf\">2. 核心權益（含註冊會員全部權益）：</div>\n<ul class=\"list-bullet1\">\n<li class=\"ace-line ace-line old-record-id-Wgnuf6J6GdTBVzcD7WrcPvTWnBh\" data-list=\"bullet\">\n<div>折扣權益：日常消費享9.5折優惠（特價商品除外）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-VIbefM0IDdNjzScQ2AncdINYn1e\" data-list=\"bullet\">\n<div>配送權益：單筆訂單滿29元享免費配送服務，每月可享3次；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-H0bOfVeald2YHtcLvrJcEg0InHf\" data-list=\"bullet\">\n<div>積分權益：消費積分累積倍率提升至1.2倍（即1元=1.2積分）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-K1UyfMigIdyuW0cIIv1cThOZnNh\" data-list=\"bullet\">\n<div>生日權益：生日當月可領取生日專屬券（滿50減20元），生日當天消費享雙倍成長值；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-INGFfcAyLdAFzGcatlfcwn7HnBc\" data-list=\"bullet\">\n<div>服務權益：開通專屬客服通道，諮詢響應時間不超過5分鐘。</div>\n</li>\n</ul>\n<h3 class=\"heading-3 ace-line old-record-id-FyM6fBdsMdEusWcVt5bc84yUnNf\">（三）黃金會員：超値特權，進階之選</h3>\n<div class=\"ace-line ace-line old-record-id-DiKZf5EFydbBuOcg7cqcwjNkn7g\">1. 升級條件：近12個月內累積成長值滿1500分；</div>\n<div class=\"ace-line ace-line old-record-id-X3mef6Ys6dccmZce9CKcixRFnNd\">2. 核心權益（含白銀會員全部權益）：</div>\n<ul class=\"list-bullet1\">\n<li class=\"ace-line ace-line old-record-id-R1SffpUDpd91fYcVQIOc27XFnib\" data-list=\"bullet\">\n<div>折扣權益：日常消費享9折優惠，每月可領取1張8.5折特權券（單筆訂單限用1張，滿100元可用）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-FvvOf1eWfd47iHcFJ21cRobBnAd\" data-list=\"bullet\">\n<div>配送權益：單筆訂單滿19元享免費配送服務，每月無使用次數限制；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-B2Mmf0EFgdY48NcdPYJcwL05nVe\" data-list=\"bullet\">\n<div>積分權益：消費積分累積倍率提升至1.5倍，每月可參與1次「積分秒殺」專屬活動；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-CE85fKrlsdWB0Ec47Aicb12MnSf\" data-list=\"bullet\">\n<div>新產品權益：新產品上市優先購買權，每月可獲贈1份新產品試吃裝（需手動在會員中心領取，限量發放）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-YWBAfUgkldguX0cKQDmcWutRnAg\" data-list=\"bullet\">\n<div>儲值權益：儲值100元及以上享5%返利（例：儲值100元實得105元）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-XnkEffXy3d31WycmKIPcuszsn4g\" data-list=\"bullet\">\n<div>活動權益：優先受邀參與品牌線下體驗活動（如新產品發布會、DIY工坊等）。</div>\n</li>\n</ul>\n<h3 class=\"heading-3 ace-line old-record-id-KUDbfStEpdwnHVcJf06czjE2nWb\">（四）鉑金會員：尊享服務，價值凸顯</h3>\n<div class=\"ace-line ace-line old-record-id-ZIAyfe2n4dzclRcRqQfcnAmQnef\">1. 升級條件：近12個月內累積成長值滿5000分；</div>\n<div class=\"ace-line ace-line old-record-id-OwKRfxJpVdHmJmc57TfcA6bAnJf\">2. 核心權益（含黃金會員全部權益）：</div>\n<ul class=\"list-bullet1\">\n<li class=\"ace-line ace-line old-record-id-EImSfOVK5dsZ4UcWZDwcccu6n1f\" data-list=\"bullet\">\n<div>折扣權益：日常消費享8.5折優惠，每月可領取2張8折特權券（滿200元可用）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-BX2pfH4ZmdlHgxcGnRTcnh1ingy\" data-list=\"bullet\">\n<div>配送權益：全年無門檻免費配送（不限訂單金額、不限使用次數）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-OutzfmXm9dE77RcFvdbciv5gnsg\" data-list=\"bullet\">\n<div>積分權益：消費積分累積倍率提升至1.8倍，積分兌換無上限，可兌換部分限量定製商品；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-E74dfgLVLd5JbucndYNcrQK3nmg\" data-list=\"bullet\">\n<div>售後權益：享受「先行賠付」服務，商品品質問題無需退回即可直接補發或退款，售後處理時效縮短至24小時內；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-Uhk4fZ7O6dcAk0cXZawci8LinOe\" data-list=\"bullet\">\n<div>儲值權益：儲值100元及以上享8%返利，儲值500元及以上額外贈送專屬禮品（如品牌定製週邊）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-DBYYfEyhXdc2dwcTBdgcD4l0n9c\" data-list=\"bullet\">\n<div>專屬權益：配備1對1專屬客服，提供定製化購物諮詢服務；每年可享2次免費商品升級服務（如小份升級中份）。</div>\n</li>\n</ul>\n<h3 class=\"heading-3 ace-line old-record-id-S2qxffkf9d0bjocHNPScPB58nhf\">（五）鑽石會員：頂級尊崇，專屬定製</h3>\n<div class=\"ace-line ace-line old-record-id-QEHIfoNpIdgVN0cxZX7ctcYmn1d\">1. 升級條件：近12個月內累積成長值滿15000分，或連續12個月保持鉑金會員等級且年度消費滿8000元；</div>\n<div class=\"ace-line ace-line old-record-id-QHukftv8CdU7REcQCOyc66cWnQg\">2. 核心權益（含鉑金會員全部權益）：</div>\n<ul class=\"list-bullet1\">\n<li class=\"ace-line ace-line old-record-id-Ppu4fOiVjdeFL5cyr7ScMjm8nAh\" data-list=\"bullet\">\n<div>折扣權益：日常消費享8折優惠，每年可領取5張7.5折至尊券（無消費金額限制，特價商品除外）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-PE3dfTla6dQwj9cjbf8cDtGon9d\" data-list=\"bullet\">\n<div>積分權益：消費積分累積倍率提升至2倍，每月可享1次「積分翻倍」特權（指定日期使用）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-R5MCfX0yrdJK8Ec2KUmcb9mLnSe\" data-list=\"bullet\">\n<div>定製權益：可參與品牌產品共創投票，對新產品設計、功能提出建議；每年可獲贈1份專屬定製禮品（印有會員姓名/專屬標識，需提前30天預約定製）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-NlEEfRruRdJmrqcOXouc199bnld\" data-list=\"bullet\">\n<div>稀缺權益：限量款商品優先搶購權（新產品發售前24小時專屬搶購通道）；每年可受邀參與1次品牌高端私享活動（如設計師見面會、年度答謝晚宴等）；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-ZeBRfg76NdHEhPcdnUOcQXuPnEd\" data-list=\"bullet\">\n<div>服務權益：享受私人管家服務，從訂單諮詢、定製需求到售後維權全程專屬對接；每年可享3次「免費退換貨+上門取件」服務，無理由退換貨期限延長至30天；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-A0HWfn4sBdKkdRcMwPkclOqynHh\" data-list=\"bullet\">\n<div>保級權益：若年度消費滿10000元，可直接保級下一年度鑽石會員，無需重新累積成長值；</div>\n</li>\n<li class=\"ace-line ace-line old-record-id-CmCJf8PTwdIRXic1kzYcxeFUneg\" data-list=\"bullet\">\n<div>裂變權益：分享專屬推廣碼，好友消費可獲10%提成（以積分形式發放），同時自身可累積額外成長值。</div>\n</li>\n</ul>\n<h2 class=\"heading-2 ace-line old-record-id-WjuYf6yM9dIfOjcjVo5cxGFrnXd\">三、等級升降級說明</h2>\n<h3 class=\"heading-3 ace-line old-record-id-JhHNfsCCNdbCjGcHSmicPFchnjf\">（一）升級規則</h3>\n<div class=\"ace-line ace-line old-record-id-DSJbfldw8dmncrcYTT2cUiqfnRg\">1. 即時升級：當會員成長值達到更高等級的升級條件時，系統將即時自動升級，並通過簡訊、APP消息推送升級通知，同時發放「升級禮包」（各等級升級禮包內容不同，具體以通知為準）；</div>\n<div class=\"ace-line ace-line old-record-id-DBrXf6iTxdeLtRcT9H8cy0KRnKI\">2. 升級可視化：會員中心將即時顯示「當前成長值+距下一等級所需成長值」，配合進度條直觀展示升級進度，助力會員明確成長目標。</div>\n<h3 class=\"heading-3 ace-line old-record-id-MZMtf0pn7danvocIB13ca7NnnMh\">（二）降級規則</h3>\n<div class=\"ace-line ace-line old-record-id-Vnq2fU69Sdq0FMcTNocc5HOGnLd\">1. 復核降級：每季度末復核時，若會員近12個月的成長值未達到當前等級的維持條件，將自動降級至對應成長值的等級；</div>\n<div class=\"ace-line ace-line old-record-id-BUbefI5MWdnn6XcK2IccI888nGf\">2. 降級提醒：降級前30天，系統將通過簡訊、APP消息向會員發送降級預警通知，告知當前成長值缺口及補成長值的方式，給予會員補救機會；</div>\n<div class=\"ace-line ace-line old-record-id-ZjO0fHSXAdv2EecX9ohcsCc9nlf\">3. 權益銜接：降級後，原等級專屬權益將立即失效，同步激活新等級對應的權益，已領取未使用的等級專屬券（如鑽石會員7.5折券）仍可在有效期內使用，不受降級影響。</div>\n<h2 class=\"heading-2 ace-line old-record-id-Kd93fUmSEdVoUJcg9Sqc2fN2nKg\">四、其他說明</h2>\n<div class=\"ace-line ace-line old-record-id-HE1kfExWodJ2aIccjUAc3RlFnze\">1. 成長值有效期：成長值自累積之日起12個月內有效，逾期未使用的成長值將自動清零，清零前30天系統將發送提醒；</div>\n<div class=\"ace-line ace-line old-record-id-CeaKfgtP1dIACkcLj9Bcv5S7nsd\">2. 權益使用限制：所有會員權益均僅限本人使用，不可轉讓、出售；權益有效期以券面/活動規則為準，逾期未使用自動失效，不補發、不折現；</div>\n<div class=\"ace-line ace-line old-record-id-Ofm4fkfYLdbJd5cu9KRcVy3qncg\">3. 體系調整：品牌有權根據市場環境、運營需求對會員等級規則、權益內容進行調整，調整後將提前7天通過官方平台公示，公示後新規則自動生效；</div>\n<div class=\"ace-line ace-line old-record-id-HOUmf0DVtdqF8hctBt7cA4rZnfc\">4. 諮詢渠道：若對會員等級、成長值、權益使用有疑問，可通過官方APP/小程序客服通道、客服熱線（400-XXXX-XXXX）諮詢，工作日9:00-21:00专人對接。</div>\n<div class=\"ace-line ace-line old-record-id-ExkEfBhoZdccMTcN6ELcrxpvnpc\">本說明最終解釋權歸【品牌名稱】所有，自發布之日起正式實施。</div>\n<div class=\"ace-line ace-line old-record-id-MJukfFRmhdvBTnciK8ic4jq2ntg\">&nbsp;</div>\n</div>"
        //val   htmlContent = "<div id=\"row-160324576013741\" class=\"gm-row\">\n<div class=\"normal-c0\">\n<p><span style=\"color: #c2e0f4; font-size: 30px;\">關於我們</span></p>\n</div>\n</div>\n<div id=\"row-160324585525865\" class=\"gm-row\">\n<div class=\"bgcf\">&nbsp;</div>\n<div class=\"normal-c0\">\n<h1><strong>GREAT FOOD COMES FIRST</strong></h1>\n</div>\n</div>\n<div id=\"row-16032460449038\" class=\"gm-row\">\n<div class=\"bgcf\">&nbsp;</div>\n</div>\n<div id=\"row-160324604773630\" class=\"gm-row\">\n<div class=\"bgcf\">&nbsp;</div>\n</div>\n<div id=\"row-160324697469348\" class=\"gm-row\">\n<div class=\"bgcf\">&nbsp;</div>\n<div class=\"orchidseed-c container\">\n<div class=\"row\">\n<div class=\"col-md-12\">\n<div class=\"sec-title\">\n<div class=\"\">\n<h6>漢堡王<sup>&reg;</sup>始終抱持著提供給顧客合理的價格、高品質的產品、快速的服務以及乾淨的環境，目前為全球第二大的速食連鎖業，共超過17,000多個門市。漢堡王<sup>&reg;</sup>的招牌華堡(Whopper)，採用火烤以及豐富的內容，從1957年推出以來就成為高品質漢堡的代名詞！漢堡王於1990年在台灣成立第一家門市，並持續積極展店中。<br><br>漢堡王<sup>&reg;</sup>華堡採用100%火烤純牛肉搭配口感紮實的5吋漢堡，肉片皆採全球統一的火烤設備在370度高溫下快速火烤，保留肉汁原味，多汁而不油膩，再加上新鮮番茄、洋蔥、酸黃瓜及爽口蔬菜，讓你每一口咬下都吃的到牛肉的火烤美味!</h6>\n</div>\n<hr></div>\n</div>\n</div>\n</div>\n</div>\n<div id=\"row-160324974532753\" class=\"gm-row\">\n<div class=\"bgcf\">&nbsp;</div>\n<div class=\"normal-c0\">\n<h1><strong>CONTACT</strong></h1>\n</div>\n</div>\n<div id=\"row-160324982593578\" class=\"gm-row\">\n<div class=\"bgcf\">&nbsp;</div>\n<div class=\"orchidseed-c container\">\n<div class=\"row\">\n<div class=\"col-md-12\">\n<div class=\"sec-title\">\n<div class=\"\">\n<p class=\"contact\">&nbsp;服務時間： 週一至週日：9:00-21:00<br>&nbsp;客服電話：&nbsp;<a href=\"tel:0800-251-286\">0800-251-286</a><br>&nbsp;客服信箱：&nbsp;<a href=\"mailto:contact@burgerking.com.tw\">contact@burgerking.com.tw</a></p>\n<ul class=\"contact_info\">\n<li>\n<p>公司名稱：台鋼漢堡王股份有限公司</p>\n</li>\n<li>統一編號 : 23309178</li>\n<li>\n<p>地址：台灣台北市松山區敦化南路一段2號7樓C室</p>\n</li>\n<li>\n<p>若您對漢堡王產品有相關諮詢或退換貨的需求，請洽客服專線或來信。</p>\n</li>\n</ul>\n</div>\n</div>\n</div>\n</div>\n</div>\n</div>\n<p>&nbsp;</p>\n<p><img src=\"../../uploads/article/temp_1767583961182_20260105113241.png\" alt=\"\" width=\"1500\" height=\"768\"></p>"

        binding.webView.webChromeClient = object : WebChromeClient() {
            // 核心回调：进度变化时触发（progress范围0-100）
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
              //  Log.e("newProgress","newProgress=$newProgress")
                binding.progressBar.progress = newProgress
                if (newProgress >=0&&newProgress<100) {
                    if (binding.progressBar.visibility==View.GONE){
                        binding.progressBar.visibility = View.VISIBLE
                    }
                } else  {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        binding.webView.webViewClient = object: WebViewClient() {


            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.progress = 0
                //Log.e("webViewClient","onPageStarted url=$url")
                //showLoading(true)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
              //  Log.e("webViewClient","onPageFinished url=$url")
                binding.progressBar.progress = 100
                binding.progressBar.visibility = View.GONE

            }
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            //    Log.e("shouldOverrideUrlLoading","shouldOverrideUrlLoading=$url")
                if (url!!.startsWith("mailto:")){
                    try {
                        var emailIntent = Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse(url))
                        startActivity(emailIntent);
                    }catch (e: Exception){

                    }
                    return true
                }else if (url!!.startsWith("tel:")){
                    try {
                        val dialIntent = Intent(Intent.ACTION_DIAL)
                        dialIntent.setData(Uri.parse(url))
                        startActivity(dialIntent)
                    }catch (e: Exception){

                    }
                    return true
                }
                return super.shouldOverrideUrlLoading(view, url)
            }
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                //Log.e("onReceivedSslError","onReceivedSslError=${error.toString()}")
                handler!!.cancel()
            }
        }

        //binding.webView.loadDataWithBaseURL(null, replacedStr!!, "text/html", "UTF-8", null)
        webUrl?.let {
            binding.webView.loadUrl(webUrl)
        }
    }

    /**
     * 核心方法：替换img标签中src的值为同标签的data-upload-path的值
     * @param originalStr 原始包含img标签的字符串
     * @return 替换后的字符串
     */
    private fun replaceSrcWithDataUploadPath(fullText: String): String {
        //Log.e("fullText","fullText=$fullText")
        val regex1 = "data-upload-path\\s*=([\"']?)([^\"'>\\s]+)\\1"
        val pattern1 = Pattern.compile(regex1)
        val matcher1 = pattern1.matcher(fullText)
        val newPathPrefix = "${NetBase.HOST_NEWSOFT}/api"
        val sb1 = StringBuffer()

        // 遍历所有匹配项，动态替换值
        while (matcher1.find()) {
            val quote = matcher1.group(1) // 获取属性值的引号（单/双/空）
            val oldPath = matcher1.group(2) // 获取旧的路径值
            // 提取文件名，拼接新路径（比如/old/path/1.jpg → /new/upload/path/1.jpg）
            val fileName = oldPath
            val newPath = "$newPathPrefix$fileName"
            // 替换：保留属性名和引号格式，仅替换值
            matcher1.appendReplacement(sb1, "data-upload-path=$quote$newPath$quote")
        }
        // 追加剩余未匹配的内容
        matcher1.appendTail(sb1)

        // 输出结果
        var originalStr = sb1.toString()
        // 正则规则：匹配img标签中同时包含src和data-upload-path的内容
        // 兼容属性顺序、引号格式、属性间空格
        val regex = """<img\s+(?:[^>]*?)(?:src=([\"']?)([^\"'>\s]+)\1|data-upload-path=([\"']?)([^\"'>\s]+)\3)+[^>]*?>"""
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE) // 忽略大小写（兼容IMG/Img等）
        val matcher = pattern.matcher(originalStr)

        val sb = StringBuffer()
        while (matcher.find()) {
            val matchedTag = matcher.group() // 获取当前匹配的整个img标签

            // 第一步：从匹配的标签中提取data-upload-path的值
            val pathMatcher = Pattern.compile("data-upload-path=([\"']?)([^\"'>\\s]+)\\1").matcher(matchedTag)
            val newPath = if (pathMatcher.find()) {
                pathMatcher.group(2) // 提取data-upload-path的属性值
            } else {
                // 没有找到data-upload-path，保留原标签
                matcher.appendReplacement(sb, matchedTag)
                continue
            }

            // 第二步：替换该标签中的src值为newPath，保留原src的引号格式
            val srcMatcher = Pattern.compile("src=([\"']?).+?\\1").matcher(matchedTag)
            val replacedTag = if (srcMatcher.find()) {
                val srcQuote = srcMatcher.group(1) // 原src的引号（单/双/空）
                // 替换src的值，保留引号格式
                srcMatcher.replaceFirst("src=$srcQuote$newPath$srcQuote")
            } else {
                // 没有src属性，保留原标签
                matchedTag
            }

            // 将替换后的标签写入缓冲区
            matcher.appendReplacement(sb, replacedTag)
        }
        matcher.appendTail(sb) // 追加未匹配的内容

        return sb.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (binding.webView!= null) {
            binding.webView.destroy();
        }
    }
}
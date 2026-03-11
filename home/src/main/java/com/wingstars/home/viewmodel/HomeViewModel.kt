package com.wingstars.home.viewmodel // Đặt package name cho đúng

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wingstars.base.net.API
import com.wingstars.base.net.NetBase
import com.wingstars.base.net.beans.WSCalendarNResponse
import com.wingstars.base.net.beans.WSCalendarResponse
import com.wingstars.base.net.beans.WSFashionCategoryResponse
import com.wingstars.base.net.beans.WSFashionResponse
import com.wingstars.base.net.beans.WSMemberResponse
import com.wingstars.base.net.beans.WSPostResponse
import com.wingstars.base.net.beans.WSProductResponse
import com.wingstars.base.net.beans.YoutubeUiData
import com.wingstars.member.bean.WSMemberRankBean
import com.wingstars.member.bean.WSRankBean
import com.wingstars.member.bean.WSRankBean.ACFBean
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Calendar

class HomeViewModel : ViewModel() {

    // LiveData cũ
    val homeDataList = MutableLiveData<MutableList<Int>>()
    val newsDataList = MutableLiveData<MutableList<WSPostResponse>>()
    val memberDataList = MutableLiveData<MutableList<Int>>()

    //    val calendarDataList = MutableLiveData<MutableList<WSCalendarResponse>>()
    val calendarDataList = MutableLiveData<MutableList<WSCalendarNResponse>>()

    val comingSoonDataList = MutableLiveData<MutableList<WSProductResponse>>()
    val productDataList = MutableLiveData<MutableList<WSProductResponse>>()
    val fashionDataList = MutableLiveData<MutableList<WSFashionResponse>>()
    var wsRankData = MutableLiveData<MutableList<WSMemberRankBean>>()
    var wsFashions = MutableLiveData<MutableList<WSFashionResponse>>()
    var wsFashionCategorysData = MutableLiveData<MutableList<WSFashionCategoryResponse>>()
    var wsMembersData = MutableLiveData<MutableList<WSMemberResponse>>()


    var isLoading = MutableLiveData<Boolean>()
    var tip = MutableLiveData<String>()


    public fun getHomeData() {
        // Dữ liệu cho 5 list cũ (sản phẩm, thành viên, v.v.)
        val dummyList = mutableListOf(1, 2, 3, 4)
        homeDataList.postValue(dummyList)

//        val newList = mutableListOf(1, 2, 3)
//        newsDataList.postValue(newList)

        val memberList = mutableListOf(1, 2, 3, 4, 5)
        memberDataList.postValue(memberList)
        getLatestNewsData()
        getNewCalendarData()
//        getCalendarData()
        getComingSoonData()
        getProductsData()
        getFashionsData()
        getYoutubeData()
    }

    fun getLatestNewsData() {
        isLoading.postValue(true)
        API.shared?.api?.let {
            val observerT = it.wsPosts()
            observerT?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    isLoading.postValue(false)
                    var itemTypeList: MutableList<WSPostResponse> = mutableListOf()
                    itemTypeList.clear()
                    itemTypeList.addAll(next)
                    newsDataList.postValue(itemTypeList)
                },
                { error ->
                    isLoading.postValue(false)
//                    error.message?.let { it1 ->
//                        Toast.makeText(
//                            BaseApplication.shared()!!,
//                            it1?.toString(),
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
                }
            )
        }

        //utApi()
    }

    //    fun getCalendarData() {
//        API.shared?.api?.let { api ->
//            api.wsSchedule(3, 1)
//                .subscribeOn(Schedulers.io())
//                .map { list ->
////                    list.filter { item -> isToday(item.st_dateF) }
//                    list.filter { overlapsToday(it.st_dateF, it.ed_dateF) }
//                }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { todayList ->
//                        Log.d("getWsCalendarsData", "today size=${todayList.size}")
//                        calendarDataList.postValue(todayList as MutableList<WSCalendarResponse>?)
//                    },
//                    { error ->
//                        Log.e("getWsCalendarsData", error.toString())
//                        error.printStackTrace()
//                    }
//                )
//        }
//    }
    fun getNewCalendarData() {
        val date = Calendar.getInstance().get(Calendar.YEAR).toString() + "-" +
                String.format("%02d", Calendar.getInstance().get(Calendar.MONTH) + 1) + "-" +
                String.format("%02d", Calendar.getInstance().get(Calendar.DATE))
        val monthParam = HashMap<String, String>().apply {
            put("date", date)
        }

        API.shared?.api?.let {
            val observer = it.wsCalendarN(monthParam)
            observer?.subscribeOn(Schedulers.io())
                ?.unsubscribeOn(Schedulers.io())
                ?.map { list ->
                    list.map { item ->
                        // Tạo bản sao (copy) và chỉ lấy 16 ký tự đầu cho start_date
                        val formattedDate = if (item.start_date.length >= 16) {
                            item.start_date.substring(0, 16)
                        } else {
                            item.start_date
                        }
                        item.copy(start_date = formattedDate)
                    }
                }
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ next ->
                    val limitedList = next.take(3).toMutableList()
                    calendarDataList.postValue(limitedList)
                }, { error ->
                    error.printStackTrace()
                })
        }
    }

    //即将贩售商品
    fun getComingSoonData() {
        API.shared?.api?.let { api ->
            val status = "future"
            val observer = api.wsProducts(status, 10, 1)
            observer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        val list = mutableListOf<WSProductResponse>()
                        list.addAll(next)
                        comingSoonDataList.postValue(list)
                    },
                    { error ->
                        error.printStackTrace()
                    }
                )
        }
    }

    fun getProductsData() {
        API.shared?.api?.let { api ->
            // Thay vì dùng wsProducts() fix cứng 4 cái, ta dùng hàm có param để gọi 20 cái
            val observer = api.wsProducts("publish", 20, 1)

            observer
                .subscribeOn(Schedulers.io())
                // Phân rã List trả về thành từng object sản phẩm lẻ
                .flatMapIterable { it }
                // Lọc: Chỉ giữ lại những sản phẩm có giá > 0
                .filter { product ->
                    val priceStr = product.price
                    // Kiểm tra giá không null, không rỗng và lớn hơn 0
                    !priceStr.isNullOrEmpty() && (priceStr.toDoubleOrNull() ?: 0.0) > 0.0
                }
                // Chỉ lấy đúng 4 sản phẩm sau khi đã lọc
                .take(4)
                // Gom 4 sản phẩm đó lại thành 1 List
                .toList()
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { validProducts ->
                        // Đẩy danh sách đã lọc sạch sẽ lên LiveData để UI tự động cập nhật
                        productDataList.postValue(validProducts.toMutableList())
                    },
                    { error ->
                        error.printStackTrace()
                    }
                )
        }
    }

    fun getFashionsData() {
        val params = HashMap<String, Int>()
        API.shared?.api?.let { api ->
            val observer = api.wsFashions(params, 6, 1)
            observer
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { next ->
                        val list = mutableListOf<WSFashionResponse>()
                        list.addAll(next)
                        fashionDataList.postValue(list)
                    },
                    { error ->
                        error.printStackTrace()
                    }
                )
        }
    }

    public fun getRenderedList() {
        isLoading.postValue(true)
        API.shared?.api?.let {
            val observer = it.wsRank()
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (!next.isNullOrEmpty()) {
                        var data = mutableListOf<WSMemberRankBean>()

                        next.forEach { board ->
                            val title = board.title?.rendered ?: ""
                            val acf = board.acf

                            if (acf != null) {
                                for (i in 1..5) {
                                    val rankBean = acf.rankBean(i)
                                    if (rankBean != null && !rankBean.name.isNullOrEmpty()) {
                                        var bean = WSMemberRankBean(
                                            title = title,
                                            name = rankBean.name,
                                            volume = rankBean.volume
                                        )
                                        data.add(bean)
                                    }
                                }
                            }
                        }
                        getWsMembersData(data)
                        wsPhotos(data)


                    } else {
                        isLoading.postValue(false)
                    }
                },
                { error ->
                    isLoading.postValue(false)
                }
            )
        }
    }

    private fun wsPhotos(data: MutableList<WSMemberRankBean>) {
        API.shared?.api?.let {
            val observer = it.wsPhotos(100, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    isLoading.postValue(false)
                    if (!next.isNullOrEmpty()) {
                        data.forEach {
                            val acf = it.name
                            if (acf != null) {
                                val imageList = next.filter { it.title.rendered.trim() == acf }
                                if (!imageList.isNullOrEmpty()) {
                                    val acf1 = imageList[0].acf
                                    if (acf1 != null) {
                                        it.number = acf1.number
                                    }
                                    var yoast_head_json = imageList[0].yoast_head_json
                                    if (yoast_head_json != null) {
                                        val ogImage = yoast_head_json.og_image
                                        if (!ogImage.isNullOrEmpty()) {
                                            it.image = ogImage[0].url
                                        }
                                    }
                                }
                            }
                        }
                        wsRankData.postValue(data)

                    } else {
                        isLoading.postValue(false)
                    }
                },
                { error ->
                    isLoading.postValue(false)
                }
            )
        }
    }

    val youtubeVideoList = MutableLiveData<List<YoutubeUiData>>()
//    val isLoading = MutableLiveData<Boolean>()

    fun getYoutubeData() {
        isLoading.postValue(true)

        // Đổi Channel ID (UC...) thành Playlist ID (UU...)
        var uploadPlaylistId = NetBase.YOUTUBE_CHANNEL_ID
        if (uploadPlaylistId.startsWith("UC")) {
            uploadPlaylistId = "UU" + uploadPlaylistId.substring(2)
        }

        API.shared?.api?.let { api ->
            // Gọi hàm getYoutubePlaylistItemsDirect chỉ tốn 1 ĐIỂM
            api.getYoutubePlaylistItemsDirect(
                "snippet",
                uploadPlaylistId,
                4, // Chỉ lấy 4 video mới nhất cho Trang chủ
                NetBase.YOUTUBE_API_KEY
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        isLoading.postValue(false)
                        val rawItems = response.items

                        if (!rawItems.isNullOrEmpty()) {
                            val uiList = mutableListOf<YoutubeUiData>()

                            // Xử lý dữ liệu thô sang dữ liệu đẹp
                            rawItems.forEach { item ->
                                val snippet = item.snippet

                                if (snippet != null) {
                                    val title = snippet.title ?: ""

                                    // Lấy ảnh giống như cũ
                                    val image = snippet.thumbnails?.maxres?.url
                                        ?: snippet.thumbnails?.high?.url
                                        ?: snippet.thumbnails?.medium?.url
                                        ?: snippet.thumbnails?.default?.url
                                        ?: ""

                                    // VideoID nằm trong resourceId
                                    val videoId = snippet.resourceId?.videoId ?: ""

                                    // Ngày đăng lấy từ publishedAt thay vì publishTime
                                    val rawDate = snippet.publishedAt ?: ""
                                    val formattedDate = if (rawDate.length >= 10) {
                                        rawDate.substring(0, 10).replace("-", ".")
                                    } else {
                                        rawDate
                                    }

                                    // Tạo link youtube
                                    val videoLink = "https://www.youtube.com/watch?v=$videoId"

                                    uiList.add(
                                        YoutubeUiData(
                                            title,
                                            image,
                                            formattedDate,
                                            videoLink
                                        )
                                    )
                                }
                            }
                            Log.d("YoutubeViewModel", "youtubeVideoList: $uiList")

                            // Bắn dữ liệu sang UI
                            youtubeVideoList.postValue(uiList)
                        }
                    },
                    { error ->
                        isLoading.postValue(false)
                        error.printStackTrace() // Log lỗi nếu có
                    }
                )
        }
    }

    public fun wsFashions() {
        API.shared?.api?.let {
            val emptyHashMap: java.util.HashMap<String?, Int?>? = HashMap()
            val observer = it.wsFashions(emptyHashMap, 3, 1)
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (!next.isNullOrEmpty()) {
                        //Log.e("wsFashions", "${next}")
                        wsFashions.postValue(next)
                    }
                },
                { error ->

                }
            )
        }
    }

    public fun wsFashionCategorys() {
        API.shared?.api?.let {
            val observer = it.wsFashionCategorys()
            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { next ->
                    if (!next.isNullOrEmpty()) {
                        wsFashionCategorysData.postValue(next)
                        wsFashions()
                    }
                },
                { error ->

                }
            )
        }
    }

    fun getWsMembersData(rankData: MutableList<WSMemberRankBean>) {
        //Log.e("getWsMembersData", "getWsMembersData")

        API.shared?.api?.let {
            val observer = it.wsMembers(100, 1)

            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
                AndroidSchedulers.mainThread()
            )?.subscribe(
                { allMembers ->

                    if (!allMembers.isNullOrEmpty()) {
                        val filteredList = allMembers.filter { member ->
                            rankData.any { rankBean ->
                                rankBean.name?.trim() == member.titleF?.trim()
                            }
                        }

                        val sortedList = filteredList.sortedBy { member ->
                            rankData.indexOfFirst { it.name?.trim() == member.titleF?.trim() }
                        }

                        //Log.e("getWsMembersData", "Filtered & Sorted Size: ${sortedList.size}")

                        wsMembersData.postValue(sortedList as MutableList<WSMemberResponse>?)
                    } else {
                        wsMembersData.postValue(mutableListOf())
                    }
                },
                { error ->
                    //Log.e("getWsMembersData", "error=${error.message}")
                    // Xử lý lỗi
                }
            )
        }
    }
}

private fun isToday(raw: String?): Boolean {
    if (raw.isNullOrBlank()) return false

    return try {
        val input =
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).apply {
                isLenient = false
            }
        val date = input.parse(raw) ?: return false

        val cal = java.util.Calendar.getInstance()
        cal.time = date

        val now = java.util.Calendar.getInstance()

        cal.get(java.util.Calendar.YEAR) == now.get(java.util.Calendar.YEAR) &&
                cal.get(java.util.Calendar.DAY_OF_YEAR) == now.get(java.util.Calendar.DAY_OF_YEAR)
    } catch (e: Exception) {
        false
    }
}

private fun overlapsToday(st: String?, ed: String?): Boolean {
    val fmt =
        java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).apply {
            isLenient = false
        }

    fun parse(s: String?): java.util.Date? = try {
        if (s.isNullOrBlank()) null else fmt.parse(s)
    } catch (_: Exception) {
        null
    }

    val start = parse(st) ?: return false
    val end = parse(ed) ?: start // nếu không có end thì coi như event tức thời

    val todayStart = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }.time

    val todayEnd = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, 23)
        set(java.util.Calendar.MINUTE, 59)
        set(java.util.Calendar.SECOND, 59)
        set(java.util.Calendar.MILLISECOND, 999)
    }.time

    // overlap condition: start <= todayEnd && end >= todayStart
    return !start.after(todayEnd) && !end.before(todayStart)
}


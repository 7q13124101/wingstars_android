package com.wingstars.base.net.beans

data class EvtMemberBadgeResponse(
    val code: String,                   //勳章代號
    val name: String,                   //勳章名稱
    val description: String,            //勳章描述（可選）
    val condition: String,              //觸發條件類型: initial | cumulative
    val thresholds: List<Int>,          //各階段⾨檻值
    val displayMode: String,            //進度呈現模式: count | ratio
    val badgeId: String,                //勳章唯⼀識別碼
    val progress: Int,                  //當前累積進度
    val updatedAt: String,              //最後⼀次進度更新時間
) {
    val imageNameF: String      //icon name format
        get() {
            return if(thresholdF.count() <= 1) {
                code
            } else {
                var thi = thresholdF[0]
                "${code}_${thi}"
            }
        }

    val detailImageNameF: String      //icon name format of detail view
        get() {
            return if(thresholdF.count() <= 1) {
                code
            } else {
                var thi = thresholdF[0]
                if(thi == 0) { //当前是灰图就取下一个彩图，当前是彩图就用当前图
//                    thi += 1
                    if (thresholdF.size > 1) {
                        thi = thresholdF[1]
                    } else {
                        thi = 0
                    }
                }

                "${code}_${thi}"
            }
        }

    val displayModeF: String      //displayMode format
        get() {
            return displayMode
        }

    val progressF: List<Int>    //progress format
        get() {
            val v = mutableListOf<Int>()
            if(displayModeF == "count") {
                v.add(progress)     //progressUT
            } else if(displayModeF == "ratio") {
                v.add(progress)     //progressUT
                var thv = 0
                if(thresholdF.count() >= 2) {
                    thv = thresholdF[1]
                }
                v.add(thv)
            }

            return v
        }

    val thresholdF: List<Int>   //current threshold index and value format
        get() {
            var thi = 0     //threshold index
            var thv = -1    //threshold value
            val ths = thresholds.sorted()
            ths.forEach {
                if(progress >= it) {    //progressUT
                    thi += 1
                }
            }

            if(thi >= 0 && ths.count() > 0) {
                thv = if(thi < ths.count()) {
                    ths[thi]
                } else {
                    ths[ths.count()-1]
                }
            }

            return listOf(thi, thv)
        }

//    val progressUT: Int   //progress unit test
//        get() {
//            var pt = progress
//
//            if(displayModeF == "ratio") {
//                pt = thresholds[0]
//                if(code == "EAGLE_CHECKIN"){
//                    pt = thresholds[2]
//                }
//            } else {
//                pt += 0
//            }
//
//            return pt
//        }
}

/*
    {
        "code": "EAGLE_FIRST_LOGIN",
        "name": "鷹唯有你",
        "description": "登入即可獲得彩色勳章。(不計次數)",
        "condition": "initial",
        "thresholds": [
            1
        ],
        "displayMode": "count",
        "badgeId": "ede8eb4e-12e5-49d9-99eb-c80617fdeec4",
        "progress": 1,
        "updatedAt": "2025-05-22T18:03:16.912+08:00"
    },
    {
        "code": "EAGLE_CHECKIN",
        "name": "絕對忠誠",
        "description": "當日完成｢鷹雄軍報到｣任務，即累積1次並亮燈，累積至第30次換銀章，累積至第50次換金章。呈現(累積次數)",
        "condition": "cumulative",
        "thresholds": [
            1,
            30,
            50
        ],
        "displayMode": "count",
        "badgeId": "bf6e852e-a98c-415f-b496-1c7dd0f4317b",
        "progress": 0,
        "updatedAt": null
    },
    ...
 */
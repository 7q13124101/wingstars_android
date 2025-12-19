package com.wingstars.base.net.beans

import java.io.Serializable

data class ExplainData(
    val remind: String, //新版UI中已经不见这项，暂时留置,等API获取List再决定是否舍去
    val contentList: List<ContentBean>
) : Serializable {
    data class ContentBean(
        val contentTitle: String,
        val contentDetail: String
    ) : Serializable
}

data class EvtTaskResponse(
    val id: String,                     //task ID
    val topic: String,                  //主题
    val triggerType: String,            //触发端：app | backend | pos | live
    val triggerTag: String?,            //触发标签: AppTag...
    val eventType: String,              //任务类别：exclusive | daily | limited
    val targetAudience: List<String>,   //受众，即会员级别 | All
    val startDate: String,              //起始日期
    val endDate: String,                //结束日期
    val content: String,                //活动内容
    val point: Int,                     //点数
    val pointProcess: String,           //点数发放过程
    val supportInfo: List<String>,      //应援资讯
    var statusInfo: String,//任务执行状态，unlock | unlockNot | pending | completed | reward | expired
    var isSendAPI: Boolean //发送API状态：<0,没有发送>,<1,已经调用API>
) {
    val startDateF: String      //startDate format
        get() {
            return if(startDate == null) {
                ""
            } else if(startDate.length >= 10) {
                startDate.substring(0, 10)
            } else {
                startDate
            }
        }

    val endDateF: String      //endDate format
        get() {
            return if(endDate == null) {
                ""
            } else if(endDate.length >= 10) {
                endDate.substring(0, 10)
            } else {
                endDate
            }
        }

    var status: String
        get() {
          return if(statusInfo == null){
              "unlock"
          }else{
              statusInfo
          }
        }
        set(value) {
            statusInfo = value
        }
    var isSendApiF: Boolean
        get() {
            return if(isSendAPI == null){
                false
            }else{
                isSendAPI
            }
        }
        set(value) {
            isSendAPI = value
        }
    val sectionTime: String      //时间区间：2025年X月X日(當日賽事)
        get(){
            return if(startDateF.isNotEmpty() && endDateF.isNotEmpty() ) {
                 startDateF +"〜"+endDateF
            }else if(startDateF.isNotEmpty()){
                startDateF
            }else if(endDateF.isNotEmpty()) {
                endDateF
            }else {
                ""
            }
        }
    val statusSortF: Int      //status format for sorting
        get() {
            return when(status) {
                "reward" -> 0
                "completed" -> 1
                else -> 2
            }
        }

}

/*
// 任務id uuid
type EventTaskId = string;

// 觸發類型
enum TriggerType {
  app = 'app', // 獵鷹 app 觸發
  backend = 'backend', // 後台人員編輯
  pos = 'pos', // pos 送訂單
  live = 'live' // 現場工作人員發送
}

// 觸發行為標籤清單
type ToTalTriggerTag = AppTag | BackendTag | 'none';

// 後台空投 專屬標籤
enum BackendTag {
  card = 'card', // 專屬 成功申辦指定會員
  attendance = 'attendance', // 專屬 當月全勤
}

// app 專屬標籤
enum AppTag {
  mvp = 'mvp', // 專屬 MVP
  takao = 'takao', // 專屬 應援舞台加油_TAKAO
  checkin = 'checkin' // 專屬、每日的簽到行為
  thanks = 'thanks', // 限時 開幕戰感謝有你
  fb = 'fb', // 限時 按讚官方FB
  instagram = 'instagram', // 限時 追蹤官方Instagram
  ytMember = 'ytMember', // 限時 加入 YT 會員
  yt = 'yt', // 限時 訂閱 官方 YT
}

// 任務類型
enum EventType {
  exclusive = 'exclusive', // 專屬任務
  daily = 'daily', // 每日任務
  limited = 'limited' // 限時任務
}

// 受眾, 多選 ['A001', 'A002']
enum TargetAudience {
  Card_Royal = 'A001', // 皇家
  Card_Noble = 'A002', // 尊爵
  Card_Family = 'A003', // 親子
  Card_Regular = 'A004', // 鷹國
  Normal = 'normal', // 一般會員
  All = 'all',
}
*/
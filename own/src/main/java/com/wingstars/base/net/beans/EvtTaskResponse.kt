package com.wingstars.base.net.beans

data class ExplainData(
    val remind: String, //新版UI中已经不见这项，暂时留置,等API获取List再决定是否舍去
    val contentList: List<ContentBean>
) : java.io.Serializable {
    data class ContentBean(
        val contentTitle: String,
        val contentDetail: String
    ) : java.io.Serializable
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
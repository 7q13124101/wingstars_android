package com.wingstars.base.net.beans

data class EvtMemberTaskResponse(
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
    val personalEventTaskStatus: String,    //任务执行状态，unlock | unlockNot | pending | completed | reward | expired
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
}

/*
任務活動執行狀態
enum PersonalEventTaskStatus {
  unlock = 'unlock', // 即將開放
  unlockNot = 'unlockNot', // 尚未解鎖
  pending = 'pending', // 未完成
  completed = 'completed', // 已完成
  reward = 'reward', // 已領取
  expired = 'expired', // 已過期
}

會員個人化任務事件列表數據結構
interface PersonalEventTask {
  id: EventTaskId; // 任務id
  topic: string; // 任務主題 ex: 會員專屬任務, 當日賽事進場, 開幕戰感謝有你
  triggerType: TriggerType; // 觸發類型
  triggerTag: ToTalTriggerTag; // 觸發行為標籤
  eventType: EventType; // 任務類型
  targetAudience: TargetAudience[]; // 受眾
  startDate: string; // 活動開始日期 GMT+8
  endDate: string; // 活動結束日期 GMT+8
  content: string; // 活動內容描述 ex: 第一次登入獵鷹APP 的...
  point: number; // 活動點數 ex: 獲得 5 點
  pointProcess: string; // 點數發放過程描述 ex: 1. 參加資格：活動期間內...
  supportInfo: string[]; // 應援資訊 ex: ["TAKAO：進場就有點數拿！爽看比賽還能賺獎勵，這麼划算的事還不衝？", ...]
  personalEventTaskStatus: PersonalEventTaskStatus;
}

 */
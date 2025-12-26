package com.wingstars.base.net.beans

import java.text.SimpleDateFormat
import java.util.Locale

data class CRMCouponsResponse(
    val id: String,             //
    val createdAt: String,      //
    val updatedAt: String,      //
    val couponId: String,       //coupon id
    val memberId: String,       //拥有者会员id
    val couponCode: String,     //券码，11码
    val couponStatus: Int,      //使用状态。0:未使用，1:已锁定，2:已使用，3:已过期
    val journalId: String,      //点数历程id
    val redeemedAt: String?,    //兑换时间（已使用）？
    val redeemedStore: String,  //兑换地点（已使用）？
    val coupon: Coupon?,
) {
    data class Coupon(
        val id: String,                 //coupon id
        val createdAt: String,          //新增时间
        val updatedAt: String,          //更新时间
        val couponStartDate: String,    //开始时间
        val couponEndDate: String,      //下架时间
        val couponName: String,         //coupon名称
        val coverImage: String,             //列表封面图像地址
        val galleryImages: MutableList<String>, //详情图片使用
        val couponStatus: Int,          //coupon 群组状态。 0:未上架，1:已上架，2:已下架，3:已过期
        val couponType: Int,            //1:商品券，2：活动凭证
        val description: String,        //简述
        val usageRules: String,         //使用规则
        val redeemStartAt: String,      //上架后可开始兑换coupon的时间，没设定代表上架时间到即可领取
        val redeemEndAt: String,        //兑换coupon结束时间，没设定代表下架时间后不能领取
        val redeemStore: List<String>,  //可兑换门市代码字串列表
        val productCode: String,
        val pointCost: Int,             //兑换coupon所需的点数
        val totalQuantity: Int,         //全站可发券数量上限，没设定代表无上限
        val totalIssued: Int,
        val maxPerMember: Int,          //每人最多可领券数量。预设为1。设定-1代表同一人可无限量兑换
        val cItem: Any?,                //赠品信息（如需要存放赠品货号可使用cItemId
        val couponValue: String,        //面值（目前无作用）
        val exclusive: Boolean,         //是否专属（目前无作用）
        val transferable: Boolean,      //是否可转让（目前无作用）
        val redeemPeriod: String,       //可兑换时段（目前无作用）
        val eligibleMembers: MutableList<String>, //定會員領取資格，會員要滿足其中任一種資格，才可點數兌換Coupon
        val eligibilityCriteria: String,          //限定會員點數兌換基準，目前只有memberCard 一種，欄位值若為null，則為不限定會員兌換
        val claimStartAt: String,                 //开始核销时间期间（二维码）
        val claimEndAt: String,                  //结束核销时间期间（二维码）
    ):java.io.Serializable {
        val eligibleMembersStr: String
            get() {
                return if(eligibleMembers.isNullOrEmpty()) {
                    ""
                }else if (eligibleMembers.size==1){
                    when(eligibleMembers[eligibleMembers.size-1].trim()){
                        "366a8eb1-ff7a-45ac-9a69-50321dfcd84f"->{
                            "半糖區"
                        }
                        "A003"->{
                            "半糖區"
                        }
                        "A002"->{
                            "加料區"
                        }
                        "7a3b2511-3f7a-4607-9c7b-32257becf20e"->{
                            "加料區"
                        }
                        "A001"->{
                            "少糖區"
                        }
                        "e412da17-7ad2-4049-a54d-3b09d6d3d215"->{
                            "少糖區"
                        }
                        "A004"->{
                            "人氣特調"
                        }
                        "ceccde97-fa1e-4d96-bf74-9f6ad148751a"->{
                            "人氣特調"
                        }
                        "43e223a4-18e0-4ad4-baa9-f4a4dcd97e83"->{
                            "黃金比例"
                        }
                        "c566304d-06d1-4872-8b03-74cc788b3539"->{
                            "全糖區"
                        }
                        else -> ""
                    }
                }else{
                    "獵鷹會員"
                }
            }
        val claimStartAtF: String
            get() {
                return if(claimStartAt == null) {
                    ""
                } else if(claimStartAt.length >= 10) {
                    claimStartAt.substring(0, 10)
                } else {
                    claimStartAt
                }
            }
        val claimEndAtF: String
            get() {
                return if(claimEndAt == null) {
                    ""
                } else if(claimEndAt.length >= 10) {
                    claimEndAt.substring(0, 10)
                } else {
                    claimEndAt
                }
            }
        val claimDurationF: String      //claimDuration format
            get() {
                return "${claimStartAtF}~${claimEndAtF}"
            }
        val eligibleMembersF: MutableList<String>
            get(){
                return if(eligibleMembers == null) {
                    mutableListOf()
                } else {
                    for (i in eligibleMembers.indices) {
                        if (eligibleMembers[i].trim() == "A001") {
                            eligibleMembers[i] = "鷹國皇家會員"
                        }else if (eligibleMembers[i].trim() == "A002"){
                            eligibleMembers[i] = "鷹國尊爵會員"
                        }else if (eligibleMembers[i].trim() == "A003"){
                            eligibleMembers[i] = "Takao 親子卡"
                        }else if (eligibleMembers[i].trim() == "A004"){
                            eligibleMembers[i] = "鷹國人會員"
                        }
                    }
                    eligibleMembers
                }
            }
        val redeemStartAtF: String
            get() {
                return try {
                    if (redeemStartAt.isNullOrEmpty()) return ""

                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                    val date = inputFormat.parse(redeemStartAt)
                    outputFormat.format(date!!)
                } catch (e: Exception) {
                    redeemStartAt ?: ""
                }
            }


        val redeemEndAtF: String
            get() {
                return try {
                    if (redeemEndAt.isNullOrEmpty()) return ""

                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                    val date = inputFormat.parse(redeemEndAt)
                    outputFormat.format(date!!)
                } catch (e: Exception) {
                    redeemEndAt ?: ""
                }
            }

        val redeemDurationF: String      //redeemDuration format
            get() {
                return "${redeemStartAtF}~${redeemEndAtF}"
            }
    }

    val redeemedAtF: String      //redeemedAt format
        get() {
            return if(redeemedAt == null) {
                ""
            } else if(redeemedAt.length >= 16) {
                redeemedAt.substring(0, 16).replace('T', ' ')
            } else {
                redeemedAt
            }
        }

    val couponNameF: String      //couponName format
        get() {
            return coupon?.couponName ?: ""
        }

    val imagesF: String      //images format
        get() {
            return coupon?.coverImage ?: ""
        }

    val redeemDurationF: String      //redeemDuration format
        get() {
            return coupon?.redeemDurationF ?: "~"
        }
}

/*
    {
            "id": "b0f33c34-f6d1-4f5f-b972-effc1a78d5e1",
            "createdAt": "2025-05-06T15:59:20.54475+08:00",
            "updatedAt": "2025-05-06T15:59:20.54475+08:00",
            "couponId": "8c976eda-42a3-437f-aeab-303c160dcef9",
            "memberId": "e55caa39-7a5b-4378-8d07-079fa79482ef",
            "couponCode": "5A01BXY1JON",
            "couponStatus": 0,
            "journalId": "09e205d6-ce81-48ba-8364-d1dd654d6ca1",
            "redeemedAt": null,
            "redeemedStore": "",
            "coupon": {
                "id": "8c976eda-42a3-437f-aeab-303c160dcef9",
                "createdAt": "2025-05-06T10:42:22.400787+08:00",
                "updatedAt": "2025-05-06T15:59:20.547805+08:00",
                "couponStartDate": "2025-05-27T08:00:00+08:00",
                "couponEndDate": "2025-04-27T08:00:00+08:00",
                "couponName": "實戰棒球置物架",
                "images": "https://jellbean-hawk.s3.amazonaws.com/coupons/5ffaea8d-190772.png",
                "couponValue": 0,
                "couponStatus": 1,
                "couponType": 1,
                "description": "．部分商品數量有限，換完為止。\n．兌換成功後無法轉讓、取消或更換其他商品，請確認兌換內容無誤後再行操作。\n．本券僅限於指定兌換地點使用，請於現場出示 APP 票券QRCODE條碼。\n．兌換券使用期限至2026年02月02日止，活動內容如有異動，將依球團公告為準。\n．期限內若無完成兌換，本券視同失效，點數不予退回。",
                "usageRules": "．若使用本兌換券的訂單發生退貨情況，點數不予退回。\n",
                "exclusive": false,
                "transferable": false,
                "redeemStartAt": "2025-04-27T08:00:00+08:00",
                "redeemEndAt": "2025-05-27T08:00:00+08:00",
                "redeemPeriod": "",
                "redeemStore": [
                    "001",
                    "002"
                ],
                "productCode": "5A01",
                "pointCost": 100,
                "totalQuantity": 100,
                "totalIssued": 2,
                "maxPerMember": 1,
                "cItem": null
            }
    }
*/
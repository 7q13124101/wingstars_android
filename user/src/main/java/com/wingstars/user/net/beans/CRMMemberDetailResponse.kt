package com.wingstars.user.net.beans

import java.io.Serializable

data class CRMMemberDetailResponse (
    val Id: String,                     //會員ID
    val UserId: String,                 //用戶ID
    val Name: String,
    val LineId: String,
    val Code: String,                   //會員代碼
    val Points: Int,                    //點數
    val TotalPointsEarned: Int,         //獲得總點數
    val TotalPointsRedeemed: Int,       //兌換總點數
    val TotalPointsExpired: Int,        //過期總點數
    val TotalPointsWillExpired: Int,    //即將過期總點數
    val LockedPoints: Int,              //已獲得但不可用的點數 (臨時狀態)
    val Gender: String,                 //性别
    val Phone: String,
    val Birthday: String,
    val Email: String,
    val CreatedAt: String,
    val UpdatedAt: String,              //更新時間
    val NextTokenExpiredDate: String,   //下次令牌過期日期
    val Coupon: List<Coupone>,          //優惠券
    val MemberTier: MemberTierData,     //会员等级
    val MemberCards: List<MemberCard>,  //会员卡级
) {
    data class Coupone(
        val CItem: CItemData,           //优惠券内容
        val CouponEndDate: String,      //優惠券結束日期
        val CouponName: String,         //優惠券名稱
        val CouponNum: String,          //優惠全編號
        val CouponStartDate: String,    //優惠券開始日期
        val CouponStatus: Int,          //0: 未使用, 1: 已使用, 2: 已过期
        val CouponType: Int,            //1.赠品券 2.餐点折抵 3.折购券 4.加价购 5.免运券 6.优惠价
        val CouponValue: Int,           //優惠券面值
        val Exclusive: Boolean,         //是否独家专属。如果是独家专属，则只有该会员可以使用
        val RedeemPeriod: String,       //兑换期，空表示无限制
        val RedeemStore: String,        //兑换商店，空表示无限制
    ): Serializable {
        data class CItemData(
            val CItemCount: Int,                //優惠券內容數量
            val CItemId: String,                //優惠券內容 ID
            val CItemPrice: Int,                //優惠券內容價格
            val CSubItem: List<CSubItemData>,   //優惠券子內容
        ): Serializable {
            data class CSubItemData(
                val CSubItemId: String,         //優惠券子內容 ID
                val CSubItemName: String,       //優惠券子內容名稱
                val CSubItemPrice: String,      //優惠券子內容價格
            ): Serializable
        }
    }

    data class MemberTierData(
        val TierGroupId: String,        //等級群組ID
        val TierId: String,             //等級ID
        val BenefitsAssigned: Boolean,   //已分配的福利
        val Reason: String,             //原因
        val EffectiveDate: String,      //生效日期
        val ExpirationDate: String,     //過期日期
        val TierName: String,           //3个等级：["辣薯球", "安格斯", "華堡"]
        val TierSeq: Int,               //3个等级：[1, 2, 3]
    ): Serializable

    data class MemberCard(
        val MemberTypeId: String,        //卡级：A001 | A002 | A003 | A004 | A005
        val ExpiredAt: String,           //過期日期
    ): Serializable {
        val ExpiredAtF: String      //ExpiredAt format
            get() {
                return if(ExpiredAt == null) {
                    ""
                } else if(ExpiredAt.length >= 10) {
                    ExpiredAt.substring(0, 10)
                } else {
                    ExpiredAt
                }
            }
    }

    val HighCardF: MemberCard?      //最高等级的MemberCard format
        get() {
            return MemberCards.minByOrNull { it.MemberTypeId }
        }

    val MemberCardsF: List<MemberCard>      //MemberCards format
        get() {
            return MemberCards.sortedBy { it.MemberTypeId }
        }
}
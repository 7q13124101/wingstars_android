package com.wingstars.base.net.beans

data class CRMJournalHistoryResponse(
    val CurrentPage: Int,       //分页
    val Size: Int,
    val TotalPage: Int,
    val TotalRecord: Int,
    val Journals: List<Journal>,
) {
    data class Journal(
        val Id: String,                     //
        val Type: String,                   //
        val Status: String,                 //
        val Description: String,            //
        val Points: Int,                    //
        val OrderId: String?,                //
        val Invoice: String?,                //
        val ShopName: String?,               //
        val CheckoutTime: String,           //
        val CreditedAt: String,             //
        val ExpiredAt: String?,              //
        val UUID: String?,                   //
    ):java.io.Serializable {
        val CreditedAtF: String      //CreditedAt format
            get() {
                return if(CreditedAt == null) {
                    ""
                } else if(CreditedAt.length >= 10) {
                    CreditedAt.substring(0, 10)
                } else {
                    CreditedAt
                }
            }
    }

    val JournalsGroupF: Map<Boolean, List<Journal>>   //点数历程
        get() = Journals.groupBy { it.Points > 0 }
}

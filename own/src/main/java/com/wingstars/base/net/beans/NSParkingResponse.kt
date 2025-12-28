package com.wingstars.base.net.beans

import java.io.Serializable

data class NSParkingResponse(
    val UpdateTime: String,
    val UpdateInterval: Int,
    val SrcUpdateTime: String,
    val SrcUpdateInterval: Int,
    val AuthorityCode: String,
    val ParkingAvailabilities: List<Parking>,
) {
    data class Parking(
        val CarParkID: String,          //id
        val CarParkName: ParkName,      //名称
        val TotalSpaces: Int,           //总车位数
        val AvailableSpaces: Int,       //可用车位数
        val ServiceStatus: Int,         //
        val FullStatus: Int,            //
        val ChargeStatus: Int,          //
    ): Serializable {
        data class ParkName(
            val Zh_tw: String?,         //可能是多语言
        ): Serializable {
            val NameF: String           //Zh_tw format
                get() {
                    return Zh_tw?:""
                }
        }

        val CarParkNameF: String        //CarParkName format
            get() {
                return CarParkName.NameF
            }
    }
}

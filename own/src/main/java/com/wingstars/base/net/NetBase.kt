package com.wingstars.base.net

import android.util.Log
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

object NetBase {
    const val HOST_BASE = "https://api.preciser.io"

    init {
        try {

        } catch(e: Exception){
            println("Exception: ${e.message}")
        }
    }

    fun ut() {
        API.shared?.api?.let {
//            //测试
//            val observer = it.nsPlayers()
//            observer?.subscribeOn(Schedulers.io())?.unsubscribeOn(Schedulers.io())?.observeOn(
//                AndroidSchedulers.mainThread())?.subscribe(
//                { next ->
//                    Log.d("API", "[nsPlayers] next.data.size: ${next.size}")
//
//                    for (pd in next) {
//                        println("jersey_number: ${pd.jersey_number}, name: ${pd.player_name}, position: ${pd.position}, id: ${pd.player_id}, logo_url: ${pd.logo_url}")
//                    }
//                },
//                { error ->
//                    error.message?.let { it1 ->
//                        Log.d("API", "[nsPlayers] error.message: ${it1?.toString()}")
//                    }
//                }
//            )
        }
    }
}
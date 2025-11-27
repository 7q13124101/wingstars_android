package com.wingstars.user.policyterm

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.wingstars.user.GetJsonDataUtil
import com.wingstars.user.net.beans.PrivacyPolicyResponse
import org.json.JSONObject

class PolicyTermModel : ViewModel() {

    var privacyPolicyData = MutableLiveData<PrivacyPolicyResponse>()

    fun getPrivacyPolicyJson(context: Context) {
//        val fileName = "hi.json"
        val jsonDataStr = GetJsonDataUtil().getJson(context, "hi.json")
//        Log.d("PolicyTerm", "jsonDataStr = $jsonDataStr")
        val response = PrivacyPolicyResponse(null, null, null)
        try {
            val jsonObject = JSONObject(jsonDataStr)
            response.top_title = jsonObject.optString("top_title")
            response.top_title_content = jsonObject.optString("top_title_content")
            val list = ArrayList<PrivacyPolicyResponse.PrivacyPolicyData>()
            val gson = Gson()
            val arr = jsonObject.optJSONArray("policy_data")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val item = gson.fromJson(
                        arr.getJSONObject(i).toString(),
                        PrivacyPolicyResponse.PrivacyPolicyData::class.java
                    )
                    list.add(item)
                }
            }
            response.policy_data = list
            privacyPolicyData.postValue(response)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

package com.wingstars.user.utils

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class GetJsonDataUtils {

    fun getJson(context: Context, fileName: String): String {
        val stringBuilder = StringBuilder()

        try {
            val inputStream = context.assets.open(fileName)
            val bf = BufferedReader(InputStreamReader(inputStream))

            var line: String?
            while (bf.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }

            bf.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }
}
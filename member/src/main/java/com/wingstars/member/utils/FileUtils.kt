package com.wingstars.member.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import java.io.File


class FileUtils {
    companion object{
        fun clearDirectory(dir: File?): Boolean {
            if (dir != null && dir.isDirectory()) {
                val children = dir.listFiles()
                if (children != null) {
                    for (child in children) {
                        val path = child.path
                        //Log.e("deleteAllFiles","path=${path}")
                        if (child.isDirectory()) {
                            clearDirectory(child)
                        } else {
                            child.delete()
                        }
                    }
                }
                return dir.delete()
            }
            return false
        }

        // 在外部存储的应用私有目录下创建文件夹
        fun createFolderInExternalStorage(context: Context, folderName: String,file: File): File? {
            val tempDir = File(file, folderName)
            if (!tempDir.exists()) {
                if (!tempDir.mkdirs()) {
                    //Log.e("originalBitmap", "创建临时文件夹失败")
                    return null
                }
            }
            return tempDir
        }

        fun getFileList(path: String): MutableList<String>{
            var imageData = mutableListOf<String>()
            val directory = File(path)
            if (directory.exists() && directory.isDirectory()) {
                val files = directory.listFiles()
                for (file in files!!) {
                    //Log.e("getFileList", "file=${file.path}")
                    imageData.add(file.path)
                }
            }else{
                //Log.e("getFileList", "path=$path 不存在")
            }
            //Log.e("getFileList", "imageData=${Gson().toJson(imageData)}")
            return imageData
        }

    }
}
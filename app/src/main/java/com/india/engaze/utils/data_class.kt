package com.btp.me.classroom.Class

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import com.india.engaze.screens.HomePage.MainActivity
import com.india.engaze.screens.slide.MyUploadingService
import timber.log.Timber
import java.io.File
import java.io.IOException

class ClassAttribute(
        var id: String = "",
        var name: String = "",
        var status: String = "",
        var profileImage: String = "",
        var registeredAs: String = ""
)


abstract class MessageType {
    companion object {
        //        const val NONE = 0
        const val MY_MESSAGE = 1
        const val MY_FIRST_MESSAGE = 2
        const val OTHER_MESSAGE = 3
        const val OTHER_FIRST_MESSAGE = 4
        const val MY_COMMAND = 5
        const val DATE = 6
    }
}

data class ChatMessage(
        var senderId: String = "...", var visibility: String = "...", var time: String = "0", var message: String = "...", var type: String = "...", var senderName: String = "...", var senderRollNumber: String = "...", var viewType: Int
)


abstract class MyColor {
    companion object {
        private val colorCode = arrayListOf<String>("#FFA631", "#5D8CAE", "#008000", "#FFFF00", "#00FFFF", "#875F9A", "#2ABB9B", "#A17917", "#C93756", "#FA8072")
        private val colorName = arrayListOf<String>("orange", "ultraMarine", "green", "yellow", "aqua", "purple", "jungleGreen", "brown", "crimson", "salmon")

        private val colorCount = colorCode.size

        fun chooseColor(string: String): String {
            var sum = 0
            for (ch in string) {
                sum += ch.toInt()
            }
            return colorCode[sum % colorCount] // colorCode[sumOfDigits(sum)]
        }

        private fun sumOfDigits(number: Int): Int {
            var result: Int
            if (number < 0)
                result = -number
            else
                result = number

            while (result >= colorCount) {
                var temp = 0
                while (result > 0) {
                    temp += result % 10
                    result /= 10
                }
                result = temp
            }
            return result
        }
    }
}

abstract class FileBuilderNew {

    companion object {
        fun upload(uri: Uri, activity: Activity, uploadingIntent: Intent, path:String) {
            var file = uri.lastPathSegment
            if (!file?.endsWith(".pdf")!!) {
                file += ".pdf"
            }
            val data = """{"title": "$file","link": ""}"""
            uploadingIntent.putExtra("fileUri", uri)
            uploadingIntent.putExtra("storagePath", path)
            uploadingIntent.putExtra("databasePath", path)
            uploadingIntent.putExtra("data", data)
            uploadingIntent.action = MyUploadingService.ACTION_UPLOAD
            activity.startService(uploadingIntent)
                    ?: Timber.e("At this this no activity is running")
        }
    }
//    companion object {
//        fun createFile(title: String, activity: Activity): File? {
//            try {
//                var fileName = File(activity.getExternalFilesDir(Environment.DIRECTORY_DCIM), "Engage");
//
//                if (!fileName.exists()) {
//                    fileName.mkdir()
//                }
//                fileName = File(fileName.toString(), "media")
//                if (!fileName.exists()) {
//                    fileName.mkdir()
//                }
//                fileName = File(fileName.toString(), "pdf")
//                if (!fileName.exists()) {
//                    fileName.mkdir()
//                }
//                fileName = File(fileName, title)
//
//                fileName.createNewFile()
//
//                return fileName
//            } catch (error: IOException) {
//                error.printStackTrace()
//            }
//
//            return null
//        }
//    }
}


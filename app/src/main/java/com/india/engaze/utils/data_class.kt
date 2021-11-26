package com.india.engaze.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.india.engaze.screens.slide.MyUploadingService
import timber.log.Timber

class ClassAttribute(
        var id: String = "",
        var name: String = "",
        var status: String = "",
        var profileImage: String = "",
        var registeredAs: String = "",
        var timeTable: String = "",
        var physicalStrength: String = "",
        var membersCount: Int = 0,
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
    }
}

abstract class FileBuilderNew {

    companion object {
        fun upload(uri: Uri, activity: Activity, uploadingIntent: Intent, path: String) {
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


            Toast.makeText(activity, "Uploading started check notification for its completion.", Toast.LENGTH_SHORT).show()
        }
    }
}

data class Assignment(
        var title: String? = null,
        var description: String? = null,
        var submissionDate: String? = null,
        var uploadingDate: String? = null,
        var maxMarks: String? = null,
        var link: String? = null,
        var uploadDate: String? = null
)


data class StudentAssignmentDetails(
        var link: String? = null,
        var marks: String? = null,
        var name: String? = null,
        var rollNumber: String? = null,
        var state: String? = null,
        var userId: String? = null,
        var registeredAs: String? = null
)


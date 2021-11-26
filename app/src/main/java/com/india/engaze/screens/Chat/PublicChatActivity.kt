package com.india.engaze.screens.Chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.india.engaze.utils.ChatMessage
import com.india.engaze.utils.MessageType
import com.india.engaze.screens.adapter.ChatAdapter
import com.india.engaze.screens.slide.SlideActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import com.india.engaze.screens.HomePage.MainActivity.Companion.classId
import com.india.engaze.screens.Splash.SplashActivity
import kotlinx.android.synthetic.main.activity_public_chat.*
import org.json.JSONObject
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import com.india.engaze.R
import com.india.engaze.screens.base.BaseActivity
import kotlinx.android.synthetic.main.toolbar_with_back.*

class PublicChatActivity : BaseActivity() {

    private val mRootRef = FirebaseDatabase.getInstance().reference
    private val currentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }

    private var isTeacher : Boolean= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_chat)

        if(classId == "null") finish()

        public_chat_recycler_list.setHasFixedSize(true)
        public_chat_recycler_list.layoutManager = LinearLayoutManager(this)
        initialize()
        getSendMessageFromDatabase()
        public_chat_send_button.setOnClickListener { getTypeMessage() }
    }

    private fun initialize(){
        setTitle()
        setUserName()
    }

    private fun setUserName() {
        mRootRef.child("Classroom/$classId/members/${currentUser!!.uid}").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                userName = "Anonymous"
                rollNumber = "Anonymous"
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG,"User Name data cnaged : $p0")
                userName = p0.child("name").value.toString()
                rollNumber = p0.child("rollNumber").value.toString()
                isTeacher = p0.child("as").value.toString() == "teacher"
                invalidateOptionsMenu()
            }

        })
    }

    private fun setTitle(){
        backButton.setOnClickListener { onBackPressed() }
        mRootRef.child("Classroom/$classId/name").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "No Internet connections")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.value == null || p0.value.toString() == "null")
                    finish()
                toolbar_text.text = p0.value.toString()
            }

        })

    }

    private fun getTypeMessage() {
        if (public_chat_type_message.text.isNotBlank()) {
            val message = public_chat_type_message.text.toString().trim()
            if (isCommand(message)) {
                Log.d(TAG, "Is a command : $message")
            } else {
                Log.d(TAG, "Not a command : $message")
                public_chat_type_message.text.clear()
                sendMessage(message, "message", "everyone")
            }
        }
    }

    private fun isCommand(command: String): Boolean {
        if (!command.startsWith(COMMAND_TAG))
            return false
        when (command.removePrefix(COMMAND_TAG).toLowerCase()) {
            commandList[0] ->{
                sendMessage(commandList[0],"command","me")
                Toast.makeText(this, userName,Toast.LENGTH_LONG).show()
            }
            commandList[1] -> {
                sendMessage(commandList[1], "command", "me")
                Toast.makeText(this, title, Toast.LENGTH_LONG).show()
            }
            else ->{
                Toast.makeText(this,"No Such Command Found",Toast.LENGTH_LONG).show()
                return true
            }
        }

        public_chat_type_message.text.clear()
        return true
    }

    private fun sendMessage(message: String, type: String, visibility: String) {
        val map = HashMap<String, String>()


        if (userName == "null") return


        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        val dateTime = System.currentTimeMillis()
        val date = dateFormat.parse(dateFormat.format(dateTime)).time

        Log.d("Engaze", "date $dateTime : $date.")

        map["message"] = message
        map["senderName"] = userName
        map["senderId"] = currentUser!!.uid.toString()
        map["visibility"] = visibility
        map["type"] = type
        map["time"] = dateTime.toString()
        map["senderRollNumber"] = rollNumber

        val json = JSONObject(map as Map<*, *>).toString()

        mRootRef.child("Message/$classId/$date/$dateTime").setValue(json).addOnSuccessListener {
            Log.d(TAG,"Successfully : $json")

        }.addOnFailureListener { exception ->
//            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "No internet connection : ${exception.message}")
        }
    }

    private fun getSendMessageFromDatabase() {
        mRootRef.child("Message/$classId").orderByKey().limitToLast(10).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error : ${error.message}")
//                Toast.makeText(this@PublicChatActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()//
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d(TAG,"on data : $p0")
                var previous = "null"

                val chatList = ArrayList<ChatMessage>()

                for (dayDataSnapshot in p0.children) {
                    if (dayDataSnapshot.key == null || dayDataSnapshot.value == "null") continue

                    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

                    val date = dateFormat.format(Date(dayDataSnapshot.key!!.toLong()))

                    //show date before listing the message of each day
                    chatList.add(ChatMessage(visibility = "everyone",message = date,type = "date", viewType = MessageType.DATE))

                    for (dataSnapshot in dayDataSnapshot.children) {
                        if (dataSnapshot == null || dataSnapshot.value == "null") continue

                        Log.d(TAG,"data data: $dataSnapshot")

                        val map = Gson().fromJson(dataSnapshot.value.toString(), ChatMessage::class.java)

                        map.time = timeFormat.format(Date(map.time.toLong()))


                        if (map.visibility == "me" && map.senderId != currentUser!!.uid) {
                            continue
                        }

                        if (map.type == "command") {
                            previous = "null"
                            map.viewType = MessageType.MY_COMMAND
                        } else {
                            if (map.senderId == currentUser!!.uid) {
                                map.viewType = when (previous) {map.senderId -> MessageType.MY_MESSAGE; else -> MessageType.MY_FIRST_MESSAGE
                                }
                            } else {
                                map.viewType = when (previous) {map.senderId -> MessageType.OTHER_MESSAGE; else -> MessageType.OTHER_FIRST_MESSAGE
                                }
                            }
                            previous = map.senderId
                        }
                        chatList.add(map)
                    }
                }
                val adapter = ChatAdapter(chatList)
                public_chat_recycler_list.adapter = adapter
                public_chat_recycler_list.scrollToPosition(chatList.size - 1)
                public_chat_recycler_list.post { kotlin.run { public_chat_recycler_list.scrollToPosition(chatList.size - 1) } }
            }
        })
    }



    companion object {
        private const val TAG = "Public_Chat_Activity"
        private var userName = "null"
        private var rollNumber = "null"
        private const val COMMAND_TAG = "."

        private val commandList = arrayListOf(
                "whoami",
                "classname",
                "members",
                "slide",
                "new assignment",
                "assignment",
                "leave",
                "pending request",
                "marks",
                "settings",
                "examination"
        )


    }
}

package com.example.jacksonstateuniversity
import android.content.Intent
import android.content.Intent.getIntent


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jacksonstateuniversity.Activity.FacultyActivity
import com.example.jacksonstateuniversity.Activity.GroupActivity
import com.example.jacksonstateuniversity.Activity.LoginActivity
import com.example.jacksonstateuniversity.Activity.StudentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.annotations.Nullable
import java.util.HashMap


/**
 * Created by filipp on 6/28/2016.
 */
class GroupChat : AppCompatActivity() {
    private var btn_send_msg: Button? = null
    private var input_msg: EditText? = null
    private var chat_conversation: TextView? = null
    private var user_name: String? = null
    private var room_name: String? = null
    private var user_Id: String? = null
    private var root: DatabaseReference? = null
    private var temp_key: String? = null

    private lateinit var mAthu : FirebaseAuth

    private lateinit var mdDbRef: FirebaseDatabase

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)


        supportActionBar?.title = "Group Chat"

        mAthu = FirebaseAuth.getInstance()

        btn_send_msg = findViewById<View>(R.id.btn_send) as Button
        input_msg = findViewById<View>(R.id.msg_input) as EditText
        chat_conversation = findViewById<View>(R.id.textView) as TextView

        user_name = intent.extras!!["user_name"].toString()
        user_name = intent.extras!!["user_name"].toString()
        room_name = intent.extras!!["room_name"].toString()
        user_Id = intent.extras!!["user_id"].toString()

        title = " Room - $room_name"
        root = FirebaseDatabase.getInstance().reference.child(room_name!!)


        btn_send_msg!!.setOnClickListener {

            val map: Map<String, Any> =
                HashMap()
            temp_key = root!!.push().key

            root!!.updateChildren(map)

            val messageRoot = root!!.child(temp_key!!)
            val map2: MutableMap<String, Any> = HashMap()
            val name = FirebaseDatabase.getInstance().reference.parent.toString()

            map2["name"] = user_name!!
            map2["msg"] = input_msg!!.text.toString()

            input_msg?.setText("")

            messageRoot.updateChildren(map2)
        }

        root!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                append_chat_conversation(dataSnapshot)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                append_chat_conversation(dataSnapshot)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private var chat_msg: String? = null
    private var chat_user_name: String? = null

    private fun append_chat_conversation(dataSnapshot: DataSnapshot) {
        val i: Iterator<*> = dataSnapshot.children.iterator()
        while (i.hasNext()) {
            chat_msg = (i.next() as DataSnapshot).value as String?
            chat_user_name = (i.next() as DataSnapshot).value as String?
            chat_conversation!!.append("$chat_user_name : $chat_msg \n")
        }
    }



    //Creates Logout Button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu5,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Logout Button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.groupchat){
            val intent1 = Intent(this@GroupChat, GroupActivity::class.java)
            startActivity(intent1)
            return true
        }

        if(item.itemId == R.id.student){
            val intent1 = Intent(this@GroupChat, StudentActivity::class.java)
            startActivity(intent1)
            return true
        }


        if(item.itemId == R.id.faculty){
            val intent1 = Intent(this@GroupChat, FacultyActivity::class.java)
            startActivity(intent1)
            return true
        }

        else if(item.itemId == R.id.logout){

            mAthu.signOut()
            val intent = Intent(this@GroupChat, LoginActivity::class.java)
            startActivity(intent)
            finish()

            return true
        }


        return true
    }

}
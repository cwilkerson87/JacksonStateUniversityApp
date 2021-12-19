package com.example.jacksonstateuniversity.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jacksonstateuniversity.Adaptor.MediaAdapter
import com.example.jacksonstateuniversity.Adaptor.MessageAdapter
import com.example.jacksonstateuniversity.R
import com.example.jacksonstateuniversity.Student.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

//import com.google.firebase.database.R


class ChatActivity() : AppCompatActivity() {


    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var mediaRecyclerView: RecyclerView

    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var attachmentButton: ImageView

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var mediaAdapter: MediaAdapter

    private lateinit var messageList: ArrayList<Message>
    private lateinit var mediaArray: ArrayList<String>

    private lateinit var mDbRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)




        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        supportActionBar?.title = name

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        mediaRecyclerView = findViewById(R.id.mediaList)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        attachmentButton = findViewById(R.id.attachmentButton)
        messageList = ArrayList()
        mediaArray = ArrayList()

        messageAdapter = MessageAdapter(this, messageList)
        mediaAdapter = MediaAdapter(this,mediaArray)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

        mediaRecyclerView.layoutManager = LinearLayoutManager(this)
        mediaRecyclerView.adapter = mediaAdapter


        //logic for adding data to recyclerView
        mDbRef.child("chats").child(senderRoom!!).child("messages").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                messageList.clear()

                for(postSnapshot in snapshot.children){

                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)

                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        //adding the message to database
        sendButton.setOnClickListener{
            //send message to database
            //from database message will be received to users
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)

            mDbRef.child("chats").child(senderRoom!!).child("messages").push().setValue(messageObject).addOnSuccessListener {
                mDbRef.child("chats").child(receiverRoom!!).child("messages").push().setValue(messageObject)
            }
            //make message box empty after sending
            messageBox.setText("")
        }

        attachmentButton.setOnClickListener{


            phoneGallery()


        }


        }


    //Opens Up The Gallery
    var PICK_IMAGE_INTENT =1

    private fun phoneGallery(){
        val intent = Intent()
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE_INTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_INTENT) {
                if (data!!.clipData == null) {
                    mediaArray?.add(data.data.toString())
                } else {
                    for (i in 0 until data.clipData!!.itemCount) {
                        mediaArray?.add(data.clipData!!.getItemAt(i).uri.toString())
                    }
                }
                mediaAdapter!!.notifyDataSetChanged()
            }
        }
    }


}

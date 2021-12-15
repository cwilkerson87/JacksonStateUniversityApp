package com.example.jacksonstateuniversity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.security.AccessController.getContext

//import com.google.firebase.database.R


class ChatActivity : AppCompatActivity() {


    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var attachmentButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    var receiverRoom: String? = null
    var senderRoom: String? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

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
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        attachmentButton = findViewById(R.id.attachmentButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter

//        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//
//                val data: Intent? = result.data
//
//               var selectedVideoUri: Uri? = data?.getData()
//                var userUid :String? = FirebaseAuth.getInstance().getCurrentUser()?.getUid();
//                var storageRef : StorageReference  = FirebaseStorage.getInstance().getReference();
//                 var videoRef = storageRef.child("/videos/" + userUid );
//                    //TODO: save the video in the db
//                    uploadData(selectedVideoUri);
//                }else if(requestCode == PICK_AUDIO_REQUEST){
//                    //TODO: save the audio in the db
//                }
//
//            }
//
//        }
//        fun openSomeActivityForResult() {
//            val intent = Intent(this, SomeActivity::class.java)
//            resultLauncher.launch(intent)
//
//            activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
//            ActivityResultCallback<ActivityResult>()){
//
//        }



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

        //adding the attachment to database
//        attachmentButton.setOnClickListener{
            //send attachment to database



//            val message = messageBox.text.toString()
//            val messageObject = Message(message, senderUid)
//
//            mDbRef.child("chats").child(senderRoom!!).child("messages").push().setValue(messageObject).addOnSuccessListener {
//                mDbRef.child("chats").child(receiverRoom!!).child("messages").push().setValue(messageObject)
//            }
//            //make message box empty after sending
//            messageBox.setText("")
        }


    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


    }

    }

fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    if (resultCode == AppCompatActivity.RESULT_OK) {
        if (requestCode == PICK_VIDEO_REQUEST) {
            val selectedVideoUri: Uri = data.getData()
            val userUid: String = FirebaseAuth.getInstance().getCurrentUser().getUid()
            val storageRef: StorageReference = FirebaseStorage.getInstance().getReference()
            videoRef = storageRef.child("/videos/$userUid")
            //TODO: save the video in the db
            uploadData(selectedVideoUri)
        } else if (requestCode == PICK_AUDIO_REQUEST) {
            //TODO: save the audio in the db
        }
    }



}

fun uploadData(videoUri: Uri?) {
    if (videoUri != null) {
        val uploadTask: UploadTask = videoRef.putFile(videoUri)
        uploadTask.addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot?>() {
            override fun onComplete(@NonNull task: Task<UploadTask.TaskSnapshot?>) {
                if (task.isSuccessful()) Toast.makeText(getContext(), "Upload Complete", Toast.LENGTH_SHORT).show()
                progressBarUpload.setVisibility(View.INVISIBLE)
            }
        }).addOnProgressListener(object : OnProgressListener<UploadTask.TaskSnapshot?>() {
            fun onProgress(taskSnapshot: UploadTask.TaskSnapshot?) {
                updateProgress(taskSnapshot)
            }
        })
    } else {
//        Toast.makeText(this, "Nothing to upload", Toast.LENGTH_SHORT).show()
    }
}


}
package com.example.jacksonstateuniversity.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jacksonstateuniversity.Adaptor.UserAdaptor
import com.example.jacksonstateuniversity.R
import com.example.jacksonstateuniversity.Student.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class StudentActivity : AppCompatActivity() {

    private lateinit var userRecyclerView : RecyclerView
    private lateinit var userList: ArrayList<Student>
    private lateinit var adaptor: UserAdaptor
    private lateinit var mAthu : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        mAthu = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList = ArrayList()

        adaptor = UserAdaptor(this, userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adaptor


        mDbRef.child("student").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for (postSnapshot in snapshot.children ){

                    val currentUser = postSnapshot.getValue(Student::class.java)

                    println("mAthu = ${mAthu.currentUser?.uid.toString()}  Current User = ${currentUser?.uid}")

                    if(mAthu.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }

                adaptor.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout){

            mAthu.signOut()
            val intent = Intent(this@StudentActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()

            return true
        }

        else if(item.itemId == R.id.groupchat){
            val intent1 = Intent(this@StudentActivity, GroupActivity::class.java)
            startActivity(intent1)
            return true
        }
        return true
    }


    //Takes You Back To Login
    override fun onBackPressed() {

        val intent = Intent(this@StudentActivity, SelectionActivity::class.java)
        startActivity(intent)
        finish()

    }
}
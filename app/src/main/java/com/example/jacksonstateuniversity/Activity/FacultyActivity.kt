package com.example.jacksonstateuniversity.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jacksonstateuniversity.FacultyAdaptor
import com.example.jacksonstateuniversity.R
import com.example.jacksonstateuniversity.Student.Faculty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
//import com.google.firebase.database.R

class FacultyActivity : AppCompatActivity() {


    private lateinit var facultyRecyclerView: RecyclerView
    private lateinit var facultyList: ArrayList<Faculty>
    private lateinit var facultyAdapter : FacultyAdaptor
    private lateinit var mAthu : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_main)

        supportActionBar?.title = "Faculty"

        mAthu = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        facultyList = ArrayList()
        facultyAdapter = FacultyAdaptor(this,facultyList)

        facultyRecyclerView = findViewById(R.id.facultyRecyclerView)

        facultyRecyclerView.layoutManager = LinearLayoutManager(this)
        facultyRecyclerView.adapter = facultyAdapter


        mDbRef.child("teacher").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                facultyList.clear()

                for (postSnapshot in snapshot.children ){

                    val currentFaculty = postSnapshot.getValue(Faculty::class.java)

                    println("mAthu = ${mAthu.currentUser?.uid.toString()}  Current User = ${currentFaculty?.uid}")

                    if(mAthu.currentUser?.uid != currentFaculty?.uid){
                        facultyList.add(currentFaculty!!)
                    }

                }
                facultyAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    //Creates Logout Button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu3,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Logout Button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.groupchat){
            val intent1 = Intent(this@FacultyActivity, GroupActivity::class.java)
            startActivity(intent1)
            return true
        }

        if(item.itemId == R.id.student){
            val intent1 = Intent(this@FacultyActivity, StudentActivity::class.java)
            startActivity(intent1)
            return true
        }

        else if(item.itemId == R.id.logout){

            mAthu.signOut()
            val intent = Intent(this@FacultyActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()

            return true
        }


        return true
    }

    //Takes You Back To The Selection Screen
    override fun onBackPressed() {

        val intent = Intent(this@FacultyActivity, SelectionActivity::class.java)
        startActivity(intent)
        finish()
    }

}
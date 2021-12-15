package com.example.jacksonstateuniversity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
//import com.google.firebase.database.R

class FacultyMain : AppCompatActivity() {


    private lateinit var facultyRecyclerView: RecyclerView
    private lateinit var facultyList: ArrayList<Faculty>
    private lateinit var facultyAdapter : FacultyAdaptor
    private lateinit var mAthu : FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faculty_main)

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
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Logout Button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout){

            mAthu.signOut()
            val intent = Intent(this@FacultyMain,LoginActivity::class.java)
            startActivity(intent)
            finish()

            return true
        }
        return true
    }
}
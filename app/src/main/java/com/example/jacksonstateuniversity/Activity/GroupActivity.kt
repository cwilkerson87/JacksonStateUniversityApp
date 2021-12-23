package com.example.jacksonstateuniversity.Activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.example.jacksonstateuniversity.GroupChat
import com.example.jacksonstateuniversity.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet


class GroupActivity : AppCompatActivity() {
    private var add_room: Button? = null
    private var room_name: EditText? = null
    private var listView: ListView? = null
    private var arrayAdapter: ArrayAdapter<String?>? = null
    private val list_of_rooms = ArrayList<String?>()
    private var name: String? = null

    private lateinit var mAthu : FirebaseAuth

    private val root = FirebaseDatabase.getInstance().reference.root.child("groupchat")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)


        supportActionBar?.title = "Group Chat Main Room"

        mAthu = FirebaseAuth.getInstance()

        add_room = findViewById<Button>(R.id.btn_add_room)
        room_name = findViewById<EditText>(R.id.room_name_edittext)
        listView = findViewById<ListView>(R.id.listView)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list_of_rooms)
        listView?.adapter = arrayAdapter
        requestUserName()
        add_room?.setOnClickListener {
            val map: MutableMap<String, Any> =
                HashMap()
            map[room_name?.text.toString()] = ""
            root.updateChildren(map)
            room_name?.setText("")
        }
        root.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val set: MutableSet<String?> = HashSet()
                val i: Iterator<*> = dataSnapshot.children.iterator()
                while (i.hasNext()) {
                    set.add((i.next() as DataSnapshot).key)
                }
                list_of_rooms.clear()
                list_of_rooms.addAll(set)
                arrayAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        listView?.onItemClickListener =
            OnItemClickListener { adapterView, view, i, l ->
                val intent = Intent(applicationContext, GroupChat::class.java)
                intent.putExtra("room_name", (view as TextView).text.toString())
                intent.putExtra("user_name", name)
                startActivity(intent)
            }
    }

    private fun requestUserName() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Your Name:")
        val inputField = EditText(this)
        builder.setView(inputField)
        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialogInterface, i ->
                name = inputField.text.toString()
            })
        builder.setNegativeButton("Cancel"
        ) { dialogInterface, i ->
            dialogInterface.cancel()
            val intent = Intent(this@GroupActivity, SelectionActivity::class.java)
            startActivity(intent)
 //            requestUserName()
        }
        builder.show()
    }

    //Creates Logout Button
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu4,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //Logout Button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.student){
            val intent1 = Intent(this@GroupActivity, StudentActivity::class.java)
            startActivity(intent1)
            return true
        }

        if(item.itemId == R.id.faculty){
            val intent1 = Intent(this@GroupActivity, FacultyActivity::class.java)
            startActivity(intent1)
            return true
        }

        else if(item.itemId == R.id.logout){

            mAthu.signOut()
            val intent = Intent(this@GroupActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()

            return true
        }


        return true
    }

    //Takes You Back To Login
    override fun onBackPressed() {

        val intent = Intent(this@GroupActivity, SelectionActivity::class.java)
        startActivity(intent)
        finish()

    }
}
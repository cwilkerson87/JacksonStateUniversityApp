package com.example.jacksonstateuniversity.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.jacksonstateuniversity.GroupChat
import com.example.jacksonstateuniversity.R
import com.example.jacksonstateuniversity.Student.Participants
import com.example.jacksonstateuniversity.Student.Permission
import com.example.jacksonstateuniversity.Student.RoomName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class GroupActivity : AppCompatActivity() {

    private lateinit var room_name: EditText
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var lastFour: EditText
    private lateinit var searchIcon: ImageView
    private lateinit var searchResults: TextView
    private lateinit var clearIcon: ImageView
    private lateinit var add: ImageView
    private lateinit var list: ListView
    private lateinit var all: RadioButton
    private lateinit var delete: ImageView



    private lateinit var listAdapter: ArrayAdapter<String>
    private lateinit var groupList: ArrayList<String>

    private var add_room: Button? = null
    private var listView: ListView? = null
    private var arrayAdapter: ArrayAdapter<String?>? = null
    private val list_of_rooms = ArrayList<String?>()
    private var name: String? = null
    private var id: String? = null
    private lateinit var roomName: String

    private var permissionNeeded: Boolean = false

    private lateinit var dialog: AlertDialog

    private lateinit var mAthu: FirebaseAuth

    private val root = FirebaseDatabase.getInstance().reference.root.child("Group Chat")

    private var  myRef = FirebaseDatabase.getInstance().getReference().root.child("student")
    private var  idCheck = FirebaseDatabase.getInstance().getReference()
    private lateinit var inflater: View

   private var finalInfo: String? = null

    @SuppressLint("InflateParams")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)


        supportActionBar?.title = "Group Chat Main Room"

        mAthu = FirebaseAuth.getInstance()

        add_room = findViewById<Button>(R.id.btn_add_room)


        listView = findViewById<ListView>(R.id.listView)


        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list_of_rooms)
        listView?.adapter = arrayAdapter


        requestUserInfo()


        add_room?.setOnClickListener {


            val inflate: LayoutInflater = LayoutInflater.from(this)
            val view = inflate.inflate(R.layout.custom_group_chat_dialogbox, null)
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            val map: MutableMap<String, Any> = HashMap()

            var compareArray = emptyArray<String>()

            room_name = view.findViewById(R.id.room_name_edt_txt)
            firstName = view.findViewById(R.id.search_first_name)
            lastName = view.findViewById(R.id.search_last_name)
            lastFour = view.findViewById(R.id.search_id)
            searchIcon = view.findViewById(R.id.search_icon)
            searchResults = view.findViewById(R.id.search_name_results)
            clearIcon = view.findViewById(R.id.clear_icon)
            add = view.findViewById(R.id.add_participant)
            delete = view.findViewById(R.id.clear_participant)
            list = view.findViewById(R.id.participants_list)
            all = view.findViewById(R.id.all_btn)
            groupList = ArrayList()

            clear()

            var fName = ""
            var lName = ""
            var lFour = ""

            var clicks = 0

//            var info: String? = null

            listAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,groupList)
            list.adapter = listAdapter


            builder.setTitle("Group Room SetUp")
            builder.setView(view)


            builder.setPositiveButton("Add")
            { dialogInterface, i ->

                val mName = roomNameModifier()

                map[mName] = ""
                root.updateChildren(map)



                    root.child(mName).child("Permission").push()
                        .setValue(Permission(permissionNeeded.toString()))

                    var index = groupList.size - 1
                    while (index >= 0) {

                        val splitString = groupList[index].split(" ").toTypedArray()

                        val firstName = splitString[0]
                        val lastName = splitString[1]
                        val id = splitString[2]
                        val name = "$firstName $lastName"

                        root.child(mName.toString()).child("Participants").child(name)
                            .push()
                            .setValue(Participants(firstName, lastName, id))

                        root.child(mName).child("Group Name").push().setValue(RoomName(mName))
                        --index

                }


            Toast.makeText(this,"Working room name $mName",Toast.LENGTH_SHORT).show()

            }

            builder.setNegativeButton("Back")
            {
                    dialogInterface, i ->
                dialogInterface.dismiss()

            }


            searchIcon.setOnClickListener{

                 fName = firstName.text.toString()
                 lName = lastName.text.toString()
                 lFour = lastFour.text.toString()

                var tempArray = emptyArray<String>()


                if(firstName.text.isNotEmpty() && lastName.text.isNotEmpty() && lastFour.text.isNotEmpty()){

                    val formatFirst = fName.substring(0,1).uppercase() + fName.substring(1).lowercase().trim()
                    val formatLast = lName.substring(0,1).uppercase() + lName.substring(1).lowercase().trim()


                    Log.i("Not Working", "This is the info: ${firstName.text} ${lastName.text} ${lastFour.text}")
                    search(formatFirst,formatLast,lFour)

                    tempArray = arrayOf("$formatFirst $formatLast")

                }

                if(!compareArray.contentEquals(tempArray)){
                    compareArray = tempArray
                    clicks = 0
                }

            }

            add.setOnClickListener {

                var formatFirst: String? = null
                var formatLast: String? = null

                if(!firstName.text.isNullOrEmpty() && !lastName.text.isNullOrEmpty() || !lastFour.text.isNullOrEmpty()
                    && clicks <= 0){
                    clicks++
                    Log.i("Message", "Counts: $clicks")

//                     formatFirst = fName.substring(0,1).uppercase() + fName.substring(1).lowercase().trim()
//                     formatLast = lName.substring(0,1).uppercase() + lName.substring(1).lowercase().trim()

                    Log.i("Check Messages", "Info 2: $finalInfo")
                }

                if(firstName.text.isNullOrEmpty() && lastName.text.isNullOrEmpty()){
                    Toast.makeText(this@GroupActivity, "Fields Are Empty",Toast.LENGTH_SHORT).show()
                }

               else if(!groupList.contains(finalInfo)){
//                    info = "$formatFirst $formatLast $lFour"
                    groupList.add(finalInfo!!)
                    listAdapter.notifyDataSetChanged()

                    Toast.makeText(this@GroupActivity, "User Added!!",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this@GroupActivity,  "User Has Already Been Added!!",Toast.LENGTH_SHORT).show()

                }

            }

            delete.setOnClickListener{

                val pos = (groupList.indexOf(finalInfo))

                Log.i("Check Message", "Index check: ${groupList.indexOf(finalInfo)}")
                if(!firstName.text.isNullOrEmpty() || !lastName.text.isNullOrEmpty()){
                    if(groupList.size >= 0 && pos - 1 >= -1){
                        groupList.removeAt(pos)
                        Log.i("Check Message", "Index: ${groupList.indexOf(finalInfo)}")
                        Log.i("Check Message", "Index Size: ${groupList.size}")
                    }

                }
                listAdapter.notifyDataSetChanged()

                clicks = 0
            }


            clearIcon.setOnClickListener{

                firstName.setText("")
                lastName.setText("")
                lastFour.setText("")
                searchResults.text = "First And Last Name"

                clicks = 0

            }

            dialog = builder.create()
            dialog.show()
    }

    root.addValueEventListener( object : ValueEventListener {
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

    listView?.onItemClickListener = OnItemClickListener()
    {
        adapterView, view, i, l ->

        root.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var dataList: ArrayList<String> = ArrayList()
                var count = 0
                for (schlInfo in snapshot.children) {
                    dataList.add(schlInfo.toString())

                }

                for(info in dataList){


                    Log.i("Id Check1:", "ID shows -> $info ")
                    Log.i("Id Check2:", "ID shows -> ${id.toString()}")
                    Log.i("Id Check3:", "ID contained shows -> ${info.contains(id.toString())}")

                    val rName = adapterView.getItemAtPosition(i).toString()
                    Log.i("Permission:", "ID contained permission -> ${info.contains("false")}")
                    Log.i("rNamE:", "ID contained rName -> ${info.contains(rName)}")
                    Log.i("rName Output:", "This is rName ->$rName")

                    val dataCheck = dataList[count].contains(rName)
                    count++


                        if ((id.toString() != "" && name != "") &&
                            (info.contains(id.toString()) && info.contains(rName)) ||
                            (info.contains(rName) && info.contains("false"))) {
                            Toast.makeText(
                                this@GroupActivity,
                                "This Part Is Working $name",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(applicationContext, GroupChat::class.java)
                            intent.putExtra("room_name", (view as TextView).text.toString())
                            intent.putExtra("user_name", name)
                            //                      intent.putExtra("user_id",id)
                            startActivity(intent)

                            if (dataCheck) {
                                break
                            }
                        } else {

                            Log.i(
                                "Count Bfore :",
                                "The count is -> $count and the size is ${dataList.size}"
                            )

                            if (dataCheck) {
                                Toast.makeText(
                                    this@GroupActivity,
                                    "No Permissions",
                                    Toast.LENGTH_SHORT
                                ).show()


                                Log.i(
                                    "Name equals Name:",
                                    "The results of data check is -> $dataCheck"
                                )

                                Log.i(
                                    "Count After :",
                                    "The count is -> $count and the size is ${dataList.size}"
                                )
                                break
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

    //Search Database for User Info
    private fun search(first:String,last:String,id:String){
        var info = ""
        // Read from the database
        myRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val searchArray = ArrayList<String>()

                for(postSnapshot in snapshot.children){

                    searchArray.add(postSnapshot.toString())
                }
                var i = 0
                var count: Int
                for(databaseInfo in searchArray){
                    Log.i("Data List","Pulled information $databaseInfo")

                    count = databaseInfo.length-1


                    if(!all.isChecked) {


                        if(databaseInfo.contains(id) && id.length == 5) {

                            Log.i("last Name Index","Index ${databaseInfo.indexOf(last)}")

                            val dbInfo = databaseInfo.replace(",", "").replace("}","").trim()


                            var firstNameIndex = dbInfo.indexOf(first)
                            var lastNameIndex = dbInfo.indexOf(last)
                            var lastCount = 0
                            var firstCount = 0

                            i = 0


                            //Get The Length Of The First Name In Database
                            while (firstNameIndex <= dbInfo.indexOf(first).plus(i)) {

                                firstNameIndex++
                                firstCount++

                                if(dbInfo[firstNameIndex] == ' '){
                                    break
                                }
                                i++
                            }


                            //Get The Length Of The Last Name In Database
                            while ( dbInfo[lastNameIndex] != ' ') {


                                lastNameIndex++
                                lastCount++

                                Log.i("Count Spots", "dbInfo: $dbInfo")

                            }


                            Log.i("Index", "The Index Is $lastNameIndex")
                            Log.i("Count", "The Count Is $lastCount")

                            Log.i("Count", "The First Name Count Is ${first.length == firstCount}")
                            Log.i("Count", "The Last Name Count Is ${last.length == lastCount}")


                            Log.i("Checking", "Checking results ${last.length} = $lastCount")

                            if ((dbInfo.contains(first) && first.length == firstCount)
                                && (dbInfo.contains(last) && last.length == lastCount)) {

                                 info = "$first $last"

                                searchResults.text = info

                                finalInfo = "$info  $id"

                                Toast.makeText(this@GroupActivity, " $info", Toast.LENGTH_SHORT)
                                    .show()

                                break

                            } else {


                                Toast.makeText(
                                    this@GroupActivity,
                                    "User Not Found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else{

                            count--

                            //Breaks When The Database Cycles Through Completely
                            if( count == 0) {
                                Log.i("Break", "Working break")
                                Toast.makeText(
                                    this@GroupActivity,
                                    "User Not Found",
                                    Toast.LENGTH_SHORT
                                ).show()
                                break
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun requestUserInfo() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Information")
        builder.setMessage("Enter Your Name and Id To Enter The Group Chat")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL

        val inputField = EditText(this)
        inputField.hint = "Enter Your Full Name"

        val inputField2 = EditText(this)
        inputField2.hint = "Enter Your Id"

        layout.addView(inputField)
        layout.addView(inputField2)

        builder.setView(layout)

        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialogInterface, i ->
                name = inputField.text.toString()
                id = inputField2.text.toString()
            })
        builder.setNegativeButton("Cancel"
        ) { dialogInterface, i ->
            dialogInterface.cancel()
            val intent = Intent(this@GroupActivity, SelectionActivity::class.java)
            startActivity(intent)
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

    private fun clear(){
        firstName.setText("")
        lastName.setText("")
        lastFour.setText("")
        searchResults.setText("First And Last Name")


        firstName.isEnabled = false
        lastName.isEnabled = false
        lastFour.isEnabled = false

        searchIcon.isEnabled = false
        clearIcon.isEnabled = false
        add.isEnabled = false
        delete.isEnabled = false
    }

    private fun roomNameModifier():String{

        var formattedName = ""
        val room = room_name.text.toString()

        if(room.contains(" ")){
            val nameArray = room.split(" ")
            for(name in nameArray){
               formattedName += "${name.substring(0,1).uppercase()}${name.substring(1).lowercase()} "
            }

            formattedName.trim()
        }
        else {
            return "${room.substring(0, 1).uppercase()}${room.substring(1).lowercase()}"
        }
        return formattedName
    }


    @SuppressLint("InflateParams", "NewApi")
    fun onClick(view: android.view.View) {

        if (view is RadioButton) {

            val checked = view.isChecked

            when (view.getId()) {

                R.id.all_btn ->

                    if (checked) {
                        clear()

                        permissionNeeded = false

                        groupList.clear()
                        listAdapter.notifyDataSetChanged()

                }

                R.id.by_name_btn ->

                    if (checked) {

                        permissionNeeded = true

                        firstName.isEnabled = true
                        lastName.isEnabled = true
                        lastFour.isEnabled = true

                        add.isEnabled = true
                        delete.isEnabled = true
                        searchIcon.isEnabled = true
                        clearIcon.isEnabled = true

                    }
            }

        }
    }

}
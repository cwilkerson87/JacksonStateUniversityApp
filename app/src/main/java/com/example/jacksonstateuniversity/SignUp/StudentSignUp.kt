package com.example.jacksonstateuniversity.SignUp


import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import com.example.jacksonstateuniversity.Activity.LoginActivity
import com.example.jacksonstateuniversity.R
import com.example.jacksonstateuniversity.R.layout.activity_sign_up
import com.example.jacksonstateuniversity.Student.Student
import com.example.jacksonstateuniversity.Adaptor.UserAdaptor
import com.example.jacksonstateuniversity.WelcomeIntro
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class StudentSignUp : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    private lateinit var edtfirstname: EditText
    private lateinit var edtLastname: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtId: EditText
    private lateinit var edtRoll: EditText
 

    private lateinit var btnSignup: Button
    private lateinit var stdRadioBtns: RadioGroup
    private lateinit var radioBtnStudent: RadioButton
    private lateinit var radioBtnFaculty: RadioButton
    private lateinit var student: StudentSignUp

    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private var id: String? = null
    private var roll: String? = null
    private lateinit var stringArray: ArrayList<String?>
    private lateinit var adaptor: UserAdaptor
    private lateinit var userList: ArrayList<Student>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_sign_up)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
//        mDbRef = FirebaseDatabase.getInstance().getReference()
        edtfirstname = findViewById(R.id.edt_frstname)
        edtLastname = findViewById(R.id.edt_lstname)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        edtId = findViewById(R.id.std_id)
        edtRoll = findViewById(R.id.roll_number)
        btnSignup = findViewById(R.id.btnSignUp)
        stdRadioBtns = findViewById(R.id.radioGroup)
        radioBtnStudent = findViewById(R.id.std_Btn)
        radioBtnFaculty =  findViewById(R.id.faculty_btn)



        val spinner: Spinner = findViewById(R.id.edt_year)
        spinner.onItemSelectedListener = this

        btnSignup.setOnClickListener {


            var firstname = edtfirstname.text.toString()
            var lastname = edtLastname.text.toString()
            var email = edtEmail.text.toString()
            var password = edtPassword.text.toString()
            id = edtId.text.toString()
            roll = edtRoll.text.toString()

            stringArray = arrayListOf(firstname,lastname,spinner.selectedItem.toString(),email,password,id,roll)


            for(string in stringArray){

                if(string == null  || string.isEmpty()){

                    when(string){
                       firstname -> Toast.makeText(this, "Fill In First The Name Field",Toast.LENGTH_LONG).show()
                        lastname ->   Toast.makeText(this, "Fill In Last The Name Field",Toast.LENGTH_LONG).show()
                        email  ->   Toast.makeText(this, "Fill In The Email Field",Toast.LENGTH_LONG).show()
                        password -> Toast.makeText(this, "Fill In The Password Field",Toast.LENGTH_LONG).show()
                        spinner.onItemClickListener.toString() ->   Toast.makeText(this, "Select An Option From DropDown Menu",Toast.LENGTH_LONG).show()
                    }
                }
            }


            if(email.equals("@jsu.edu",ignoreCase = true) && !email.endsWith("@jsu.edu")){

                Toast.makeText(this,"You Must Use @jsu.edu As The Email", Toast.LENGTH_LONG).show()
            }
            else  if(spinner.selectedItem.toString() == "Select"){
                Toast.makeText(this,"Select A Year", Toast.LENGTH_LONG).show()
            }
            else{

                val firstNameStringChange = firstname.substring(0,1).uppercase() + firstname.substring(1).lowercase()
                firstname = firstNameStringChange

                val lastnameStringChange = lastname.substring(0,1).uppercase() + lastname.substring(1).lowercase()
                lastname = lastnameStringChange

                val emailStringChange = email.lowercase()
                email = emailStringChange


                signUp(firstname,lastname, id!!, roll!!,spinner.selectedItem.toString(),email,password)
            }
        }

    }


    private fun signUp(firstname:String, lastname: String, id: String, roll: String,
                       year: String, email:String, password: String) {

//
//        mDbRef.child("student").addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                userList.clear()
//
//                for (postSnapshot in snapshot.children ){
//
//                    val currentUser = postSnapshot.getValue(Student::class.java)
//
//                    println("mAthu = ${mAuth.currentUser?.uid.toString()}  Current User = ${currentUser?.uid}")
//
//                    if(mAuth.currentUser?.uid != currentUser?.uid){
//                        userList.add(currentUser!!)
//                    }
//                }
//
//                adaptor.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//
//        })


        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    addUserToDatabase(firstname,lastname,id,roll,year,email.lowercase(),mAuth.currentUser?.uid!!)
                    val intent = Intent(this@StudentSignUp, WelcomeIntro::class.java)
                    intent.putExtra("studentEmail", true)
                    finish()
                    startActivity(intent)
                } else {
                    intent.putExtra("studentEmail", false)
                    Toast.makeText(this@StudentSignUp,"Some error happen", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun addUserToDatabase(firstname: String, lastname: String, id: String,
                                  roll: String, year: String, email:String, uid:String) {

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("student").child(uid).setValue(Student(firstname,lastname,id,roll,year,email,uid))
    }

    fun onClick(view: android.view.View) {


        if(view is RadioButton){
            val checked = view.isChecked


            when(view.getId()){

                R.id.std_Btn ->

                    if(checked){


                        Toast.makeText(this@StudentSignUp,"You Already On The Student Page", Toast.LENGTH_SHORT).show()

                    }

                R.id.faculty_btn ->


                    if(checked){
                        val intent = Intent(this@StudentSignUp, FacultySignUp::class.java)
                        startActivity(intent)
                        finish()
                    }
            }
        }


    }



    private fun idGenerator(): Int {

        val range = 10000..50000

        return range.random()

    }

    @SuppressLint("SetTextI18n")
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if(parent?.getItemAtPosition(position) == "Select"){
            edtRoll.setText("")
            edtId.setText("")
        }
        else{
            edtRoll.setText("${parent?.getItemAtPosition(position)}".substring(0,1))
            edtRoll.inputType = InputType.TYPE_NULL

            edtId.setText("S" + idGenerator().toString())
            edtId.inputType = InputType.TYPE_NULL

        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


    //Takes You Back To Login
    override fun onBackPressed() {

        val intent = Intent(this@StudentSignUp, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }
}






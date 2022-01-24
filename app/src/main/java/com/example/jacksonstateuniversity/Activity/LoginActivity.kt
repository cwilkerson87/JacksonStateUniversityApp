package com.example.jacksonstateuniversity.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.jacksonstateuniversity.R
import com.example.jacksonstateuniversity.ResetPassword
import com.example.jacksonstateuniversity.SignUp.StudentSignUp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.system.exitProcess


class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button
    private lateinit var txtReset: TextView
    private lateinit var stringArray: ArrayList<String?>

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var stdId: DatabaseReference
    private lateinit var facultyId: DatabaseReference

    private lateinit var list: ArrayList<String>

    private lateinit var emailCheck: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        mDbRef = Firebase.database.reference
        stdId = FirebaseDatabase.getInstance().getReference().child("student")
        facultyId = FirebaseDatabase.getInstance().getReference().child("teacher")
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignup = findViewById(R.id.btnSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        txtReset = findViewById(R.id.forgot_password)

        list = ArrayList()


        //Print Toast Fill In Message
        stringArray = arrayListOf(edtEmail.toString(),edtPassword.toString())


        for(string in stringArray){

            if(string == null  || string.isEmpty()){

                when(string){
                    edtEmail.toString() -> Toast.makeText(this, "Fill In The Email Field",Toast.LENGTH_LONG).show()
                    edtPassword.toString() ->   Toast.makeText(this, "Fill In The Password Field",Toast.LENGTH_LONG).show()

                }
            }
        }

        btnSignup.setOnClickListener{
            val intent = Intent(this, StudentSignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()


            val emailStringChange = email.lowercase()
            val newEmail = emailStringChange


            //Toast For Fill In Error Messages
            if(newEmail == ""){

                Toast.makeText(this,"Fill In Email Field", Toast.LENGTH_LONG).show()
            }
            else if(password == ""){
                Toast.makeText(this,"Fill In Password Field", Toast.LENGTH_LONG).show()
            }

            if(newEmail.equals("@jsu.edu",ignoreCase = true) && !email.endsWith("@jsu.edu")){

                Toast.makeText(this,"You Must Use @jsu.edu As The Email", Toast.LENGTH_LONG).show()
            }

            else{
                login(newEmail, password)
            }

        }

        txtReset.setOnClickListener( View.OnClickListener {

            val intent= Intent(this@LoginActivity, ResetPassword::class.java)
            startActivity(intent)
        })

    }


    private fun login(email: String?, password: String) {
        Log.i("Checking:", "This is what is printing for email $email")

        mAuth.signInWithEmailAndPassword(email!!, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    stdId.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for (userEmail in snapshot.children) {


                                val studentEmail = userEmail.value.toString()

                                if (studentEmail.contains(email)) {
                                    val intent =
                                        Intent(this@LoginActivity, SelectionActivity::class.java)
                                    intent.putExtra("studentEmail", true)
                                    finish()
                                    startActivity(intent)
                                }
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }


                    })

                }
                if (task.isSuccessful) {

                    facultyId.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for (userEmail in snapshot.children) {

                                val facultyEmail = userEmail.value.toString()

                                if (facultyEmail.contains(email)) {

                                    val intent =
                                        Intent(this@LoginActivity, SelectionActivity::class.java)
                                    intent.putExtra("facultyEmail", true)
                                    finish()
                                    startActivity(intent)
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

                } else {
                    Toast.makeText(this@LoginActivity, "User does not exist", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
    var count = 0
    //Exits After Hitting Back Button Twice
    override fun onBackPressed() {

        count++

        if (count == 2){

            count = 0
            moveTaskToBack(true)
            finish()
            exitProcess(0)

        }
        Toast.makeText(this,"Hit Back Button Again To Exit", Toast.LENGTH_SHORT).show()
    }


    }


//}
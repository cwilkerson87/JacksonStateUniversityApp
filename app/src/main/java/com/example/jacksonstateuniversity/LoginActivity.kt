package com.example.jacksonstateuniversity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button

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

        list = ArrayList()


        btnSignup.setOnClickListener{
            val intent = Intent(this, StudentSignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            login(email, password)
        }

    }


    private fun login(email: String, password: String) {


        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    stdId.addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.i("CHECK", "This check is working")

                            for(userEmail in snapshot.children){

                                var studentEmail = userEmail.value.toString()

                               if(studentEmail.contains(email)){
                                   emailCheck = email
//
                                   val intent = Intent(this@LoginActivity, MainActivity::class.java)

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
                 if(task.isSuccessful){

                    facultyId.addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for(userEmail in snapshot.children){

                                var facultyEmail = userEmail.value.toString()

                                if(facultyEmail.contains(email)){

                                    val intent = Intent(this@LoginActivity, FacultyMain::class.java)
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

                else {

                    Toast.makeText(this@LoginActivity, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }

    }


}
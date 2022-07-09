package com.rishabhkumar.bookapp.Activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rishabhkumar.bookapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    //view Binding
    private lateinit var binding: ActivityLoginBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog, will show while loging in
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click , not have account, goto register screen
        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        //login btn click handle
        binding.btnLogin.setOnClickListener {
            /*
            1.Input Data
            2.Validate Data
            3.Login- Firebase Auth
            4.Check user type
                if-user-move to user user dashboard
                if admin-move to admin dashboard
         */
            validateData()
        }


    }

    private var email = ""
    private var password = ""

    private fun validateData() {
        //1.Input Data
        email = binding.edtLoginEmail.text.toString().trim()
        password = binding.edtLoginPassword.text.toString().trim()

        //2.Validate Data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //invalid email address
            Toast.makeText(this, "Please enter correct email address", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            //empty password
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show()
        } else {
            loginUser()
        }

    }

    private fun loginUser() {
        //3.Login- Firebase Auth

        //show progress
        progressDialog.setMessage("Logging in.")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //successfully logged in
                checkUser()
            }.addOnFailureListener {
                //failed to login
                progressDialog.dismiss()
                Toast.makeText(this, "Login Failed.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {
        /*
        4.Check user type
                if-user-move to user user dashboard
                if admin-move to admin dashboard
         */

        progressDialog.setMessage("Checking user..")
        val firebaseUser = firebaseAuth.currentUser

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()

                    //get user type
                    val userType = snapshot.child("userType").value
                    if (userType == "user") {
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()
                    } else if (userType == "admin") {
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                DashboardAdminActivity::class.java
                            )
                        )
                        finish()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}
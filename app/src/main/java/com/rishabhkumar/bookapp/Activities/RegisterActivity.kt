package com.rishabhkumar.bookapp.Activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rishabhkumar.bookapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityRegisterBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog, will show while creating account | Register User
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)


        //handle back btn
        binding.btnBack.setOnClickListener {
            onBackPressed() //goto previous screen
        }

        //btn register click handle
        binding.btnRegister.setOnClickListener {

            /*
            1) Input Data
            2) Validate Data
            3) Create Account -Firebase Auth
            4) Save user Info - Firebase runtime database
             */
            validateData()
        }

    }

    private var name = ""
    private var email = ""
    private var password = ""
    private var cnfrmPassword = ""


    private fun validateData() {
        // 1)---Input Data
        name = binding.edtRegisterName.text.toString().trim()
        email = binding.edtRegisterEmail.text.toString().trim()
        password = binding.edtRegisterPassword.text.toString().trim()
        cnfrmPassword = binding.edtRegisterCnfrmPassword.text.toString().trim()

        //2)--- Validate Data
        if (name.isEmpty()) {
            //empty name
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //invalid email address
            Toast.makeText(this, "Please enter correct email address", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            //empty password
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show()
        } else if (cnfrmPassword.isEmpty()) {
            //empty confirm password
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show()
        } else if (cnfrmPassword != password) {
            Toast.makeText(this, "Passwords mismatch", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }

    }

    private fun createUserAccount() {
        //3)--Create account- Firebase Auth
        //show progress
        progressDialog.setMessage("Creating Account..")
        progressDialog.show()


        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //account created
                updateUserInfo()
            }.addOnFailureListener {
                //failed creating account
                progressDialog.dismiss()
                Toast.makeText(this, "Failed creating user due to ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        //4) Save user Info - Firebase runtime database
        progressDialog.setMessage("Saving user Info")


        //timestamp
        val timestamp = System.currentTimeMillis()


        //get current user UID
        val uid = firebaseAuth.uid

        //setup data to add in database
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!).setValue(hashMap)
            .addOnSuccessListener {
                //user info saved
                progressDialog.dismiss()
                Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }.addOnFailureListener {
                //failed
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving user info", Toast.LENGTH_SHORT).show()
            }


    }
}
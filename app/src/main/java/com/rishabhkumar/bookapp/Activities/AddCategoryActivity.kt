package com.rishabhkumar.bookapp.Activities

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rishabhkumar.bookapp.databinding.ActivityAddCategoryBinding

class AddCategoryActivity : AppCompatActivity() {


    //view binding
    private lateinit var binding: ActivityAddCategoryBinding

    //firebaseAut
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //init firebase
        firebaseAuth = FirebaseAuth.getInstance()


        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)


        //handle back btn
        binding.btnBack.setOnClickListener {
            onBackPressed() //goto previous screen
        }

        //handle the click of done
        binding.btnDone.setOnClickListener {
            validateData()
        }

    }

    private var category = ""


    private fun validateData() {
        //validate data

        //get data
        category = binding.edtCategory.text.toString().trim()


        //validate Data
        if (category.isEmpty()) {
            Toast.makeText(this, "Enter category..", Toast.LENGTH_SHORT).show()
        } else {
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        //show progress
        progressDialog.show()

        //get timestamp
        val timestamp = System.currentTimeMillis()

        //setup data to add in firebase
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        //add to firebase database
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                binding.edtCategory.setText("")
                Toast.makeText(this, "Successfully added.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                //Failed in adding into database
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add due to ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
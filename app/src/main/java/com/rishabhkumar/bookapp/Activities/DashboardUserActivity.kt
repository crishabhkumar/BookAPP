package com.rishabhkumar.bookapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.rishabhkumar.bookapp.databinding.ActivityDashboardUserBinding

class DashboardUserActivity : AppCompatActivity() {

    //binding
    private lateinit var binding : ActivityDashboardUserBinding

    //firebase auth
    private lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle the click of logout button
        binding.btnLogout.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


    }
    private fun checkUser() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            //not logged in, user can stay in user dashboard without login
            binding.subTitleDashboard.text = "Not logged in."
        }else{
            //logged in, get and show user info
            val email = firebaseUser.email

            //set to textview of toolbar
            binding.subTitleDashboard.text = email

        }
    }
}
package com.rishabhkumar.bookapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rishabhkumar.bookapp.Categories.AdapterCategory
import com.rishabhkumar.bookapp.Categories.ModelCategory
import com.rishabhkumar.bookapp.databinding.ActivityDashboardAdminBinding

class DashboardAdminActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityDashboardAdminBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth


    //arraylist to hold categories
    private lateinit var categoryArrayList : ArrayList<ModelCategory>
    //adapter
    private lateinit var adapterCategory: AdapterCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //function to load categories in recyclerview
        loadCategories()


        //search
        binding.edtSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //called as and when user type anything
                try{
                    adapterCategory.filter.filter(s)
                }catch(e:Exception){

                }
            }
            override fun afterTextChanged(s: Editable?) {

            }
        })


        //handle the click of logout button
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }


        //handle the click of add category btn
        binding.btnAddCategory.setOnClickListener {
            startActivity(Intent(this@DashboardAdminActivity, AddCategoryActivity::class.java))
        }

        //handle the click of add pdf page
        binding.btnAddPdf.setOnClickListener {
            startActivity(Intent(this@DashboardAdminActivity, PdfAddActivity::class.java))
        }


    }

    private fun loadCategories() {
        //init arraylist
        categoryArrayList = ArrayList()

        //get all categories from firebase database
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
                categoryArrayList.clear()
                for(ds in snapshot.children){
                    //get data as model
                    val model = ds.getValue(ModelCategory::class.java)

                    //add to arraylist
                    categoryArrayList.add(model!!)
                }
                //setup adapter
                adapterCategory = AdapterCategory(this@DashboardAdminActivity,categoryArrayList)

                //set adapter to recyclerview
                binding.categoriesRecyclerView.adapter = adapterCategory
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun checkUser() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            //not logged in, goto main screen
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            //logged in, get and show user info
            val email = firebaseUser.email

            //set to textview of toolbar
            binding.subTitleDashboard.text = email

        }
    }
}
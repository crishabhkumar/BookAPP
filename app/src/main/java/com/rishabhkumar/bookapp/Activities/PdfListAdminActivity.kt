package com.rishabhkumar.bookapp.Activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.rishabhkumar.bookapp.AdapterPdfAdmin
import com.rishabhkumar.bookapp.ModelPdf
import com.rishabhkumar.bookapp.R
import com.rishabhkumar.bookapp.databinding.ActivityPdfListAdminBinding

class PdfListAdminActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding : ActivityPdfListAdminBinding


    private companion object{
        const val TAG = "PDF_LIST_ADMIN_TAG"
    }

    //category id,title
    private var categoryId = ""
    private var category = ""

    //arraylist to hold books
    private lateinit var pdfArrayList : ArrayList<ModelPdf>

    //adapter
    private lateinit var adapterPdfAdmin : AdapterPdfAdmin


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //get from intent,that we passed from adapter
        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!


        //set pdf category
        binding.txtSubTitle.text = category

        //load pdf/books
        loadPdfList()


        //search
        binding.edtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //filter data
                try{
                    adapterPdfAdmin.filter!!.filter(s)
                }
                catch (e: Exception){
                    Log.d(TAG,"onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }

    private fun loadPdfList() {
        //init arrayList
        pdfArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before start adding data into it
                    pdfArrayList.clear()
                    for(ds in snapshot.children){
                        //get data
                        val model = ds.getValue(ModelPdf::class.java)
                        //add to lisy
                        if (model != null) {
                            pdfArrayList.add(model)
                            Log.d(TAG,"onDataChange: ${model.title} ${model.categoryId}")
                        }
                    }
                    //setup adapter
                    adapterPdfAdmin = AdapterPdfAdmin(this@PdfListAdminActivity,pdfArrayList)
                    binding.pdfListRecyclerView.adapter = adapterPdfAdmin
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}
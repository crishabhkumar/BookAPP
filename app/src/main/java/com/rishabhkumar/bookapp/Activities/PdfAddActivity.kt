package com.rishabhkumar.bookapp.Activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.rishabhkumar.bookapp.Categories.ModelCategory
import com.rishabhkumar.bookapp.databinding.ActivityPdfAddBinding

class PdfAddActivity : AppCompatActivity() {

    //setup view binding
    private lateinit var binding: ActivityPdfAddBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    //arraylist to hold pdf categories
    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    //uri of picked pdf
    private var pdfUri: Uri? = null

    //TAG
    private val TAG = "PDF_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        loadPdfCategories()


        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)


        //handle click of back button
        binding.btnBack.setOnClickListener {
            onBackPressed() //goto previous screen
        }

        //handle click,show category pick dialog
        binding.txtPdfCategory.setOnClickListener {
            categoryPickDialog()
        }


        //handle click,pick pdf intent
        binding.btnAttach.setOnClickListener {
            pdfPickIntent()
        }

        //handle click ,start upload pdf/book
        binding.btnPdfSubmit.setOnClickListener {
            /*
            1.Validate Data
            2.Upload pdf to firebase storage
            3.Get url of uploaded pdf
            4.Upload pdf into to firebase db
             */
            validateData()
        }


    }

    private var title = ""
    private var description = ""
    private var category = ""

    private fun validateData() {
//        1.Validate Data
        Log.d(TAG, "validateData: validating data")

        //get data
        title = binding.edtTitle.text.toString().trim()
        description = binding.edtDescription.text.toString().trim()
        category = binding.txtPdfCategory.text.toString().trim()

        //validate data
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter title.", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Please enter book description.", Toast.LENGTH_SHORT).show()
        } else if (category.isEmpty()) {
            Toast.makeText(this, "Please choose category", Toast.LENGTH_SHORT).show()
        } else if (pdfUri == null) {
            Toast.makeText(this, "Pick PDF", Toast.LENGTH_SHORT).show()
        } else {
            //data validated,begin upload
            uploadPdfToStorage()
        }
    }

    private fun uploadPdfToStorage() {
        //2.Upload pdf to firebase storage
        Log.d(TAG, "uploadPdfToStorage: uploading to storage.")

        //show progress dialog
        progressDialog.setMessage("Uploading PDF")
        progressDialog.show()

        //timestamp
        val timestamp = System.currentTimeMillis()

        //path of pdf in firebase storage
        val filePathAndName = "Books/$timestamp"

        //storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "uploadPdfToStorage: PDF uploaded now getting url")

                //3.Get url of uploaded pdf
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedPdfUrl = "${uriTask.result}"

                //4.Upload pdf into to firebase db
                uploadPdfInfoToDb(uploadedPdfUrl, timestamp)

//                Toast.makeText(this, "Please choose category", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Log.d(TAG, "uploadPdfToStorage: failed to upload due to ${it.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Upload failed due to ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun uploadPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {
//        4.Upload pdf into to firebase db
        Log.d(TAG, "uploadPdfInfoToDb : uploading to db")
        progressDialog.setMessage("Uploading pdf info...")

        //uid of current user
        val uid = firebaseAuth.uid

        //setup data to upload
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$uid"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["url"] = "$uploadedPdfUrl"
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0

        //db reference DB-> Books -> BookId -> (Book Info)
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfInfoToDb: uploaded to db")
                progressDialog.dismiss()
                Toast.makeText(this, "Uploaded..", Toast.LENGTH_SHORT)
                    .show()
                pdfUri = null
                binding.edtTitle.setText("")
                binding.edtDescription.setText("")
                binding.txtPdfCategory.text = ""
            }
            .addOnFailureListener {
                Log.d(TAG, "uploadPdfToDb: failed to upload due to ${it.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Upload failed due to ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories:Loading pdf categories")

        //init arraylist
        categoryArrayList = ArrayList()

        //db reference to load categories DF -> Categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clean list before adding data
                categoryArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCategory::class.java)
                    //add into list
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""


    private fun categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog:Showing pdf category pick dialog")

        //get string array of categories from arraylist
        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices) {
            categoriesArray[i] = categoryArrayList[i].category
        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray) { dialog, which ->
                //handle item click
                //get clicked item
                selectedCategoryId = categoryArrayList[which].id
                selectedCategoryTitle = categoryArrayList[which].category

                //set category to textview
                binding.txtPdfCategory.text = selectedCategoryTitle

                Log.d(TAG, "categoryPickDialog: Selected category ID : $selectedCategoryId")
                Log.d(TAG, "categoryPickDialog: Selected category Title : $selectedCategoryTitle")
            }.show()
    }


    private fun pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)

    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "PDF Picked:")
                pdfUri = result.data!!.data
            } else {
                Log.d(TAG, "PDF Pick cancelled:")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )
}
package com.rishabhkumar.bookapp

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*

//application class containing functionalities which will be used in many classes
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        //a static method to convert timestamp to proper date format,so we can use it everywhere in project
        //no need to write again

        fun formatTimeStamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp

            //format dd/mm/yyyy
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        //function to get pdf size
        fun loadPdfSize(pdfUrl: String, pdfTitle: String, txtSize: TextView) {
            val TAG = "PDF_SIZE_TAG"
            //using url we can get file and its metadata from firebase storage
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener {
                    Log.d(TAG, "loadPdfSize: got metadata")
                    val bytes = it.sizeBytes.toDouble()
                    Log.d(TAG, "loadPdfSize: size bytes: $bytes")
                    //convert bytes to KB/MB
                    val kb = bytes / 1024
                    val mb = kb / 1024
                    if (mb > 1) {
                        txtSize.text = "${String.format("%.2f", mb)} MB"
                    } else if (kb >= 1) {
                        txtSize.text = "${String.format("%.2f", kb)} KB"
                    } else {
                        txtSize.text = "${String.format("%.2f", bytes)} bytes"
                    }
                }
                .addOnFailureListener {
                    //failed to get metadata
                    Log.d(TAG, "loadPdfSize: Failed to get metadata due to ${it.message}")
                }
        }


        /*
        Instead of making new function loadPdfPageCount() to just load pages count it would be more good to use
        some existing function to do that
        i.e. loadPdfFromUrlSingle Page
        Adding another parameter of type TextView e.g. txtPages
        Whenever we call that function
            1.If we require page  numbers we will pass txtPages(TextView)
            2.If we don't require page number we will pass null
            If txtPages in not null then we will se the page number count
         */


        fun loadPdfFromUrlSinglePage(
            pdfUrl: String,
            pdfTitle: String,
            pdfView: PDFView,
            progressBar: ProgressBar,
            txtPages: TextView?,
        ) {


            val TAG = "PDF_THUMBNAIL_TAG"
            //using url we can get files and its metadata from firebase storage
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener { bytes ->
                    Log.d(TAG, "loadPdfSize: size bytes: $bytes")

                    //SET to pdfVIew
                    pdfView.fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError {
                            progressBar.visibility = View.INVISIBLE
                            Log.d(TAG, "loadPdfFromUrlSinglePage: ${it.message}")
                        }
                        .onPageError { page, t ->
                            progressBar.visibility = View.INVISIBLE
                            Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                        }
                        .onLoad {
                            Log.d(TAG,"loadPDFFromUrlSinglePage: Pages:$it")
                            //pdf loaded, we can set page count,pdf thumbnail
                            progressBar.visibility = View.INVISIBLE

                            //if txtpages param is not null then set page numbers
                            if (txtPages != null) {
                                txtPages.text = "$it"
                            }
                        }
                        .load()

                }.addOnFailureListener {
                    //failed to get metadata
                    Log.d(TAG, "loadPdfSize: Failed to get metadata due to ${it.message}")
                }
        }

        fun loadCategory(categoryId: String, txtCategory: TextView) {
            //load category using category if from firebase
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //get category
                        val category: String = "${snapshot.child("category").value}"

                        //set category
                        txtCategory.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }

    }

}
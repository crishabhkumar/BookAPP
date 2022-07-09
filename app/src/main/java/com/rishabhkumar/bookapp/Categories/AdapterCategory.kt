package com.rishabhkumar.bookapp.Categories

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.rishabhkumar.bookapp.Activities.PdfListAdminActivity
import com.rishabhkumar.bookapp.databinding.SingleRowCategoryBinding

class AdapterCategory : RecyclerView.Adapter<AdapterCategory.HolderCategory>, Filterable {

    private val context: Context
    public var categoryArrayList: ArrayList<ModelCategory>
    private var filterList: ArrayList<ModelCategory>

    private var filter: FilterCategory? = null


    private lateinit var binding : SingleRowCategoryBinding

    //constructor
    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
//        inflate bind single_row.xml
        binding = SingleRowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderCategory(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        /*
        Get Data ,Set Data,Handle click etc
         */

        //Get Data
        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp = model.timestamp

        //set data
        holder.txtCategory.text = category

        //handle click ,delete category
        holder.btnDelete.setOnClickListener {
            //confirm before delete
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete it?")
                .setPositiveButton("Confirm") { a, d ->
                    Toast.makeText(context, "Deleting..", Toast.LENGTH_SHORT).show()
                    deleteCategory(model, holder)
                }
                .setNegativeButton("Cancel") { a, d ->
                    Toast.makeText(context, "Canceled.", Toast.LENGTH_SHORT).show()
                    a.dismiss()
                }.show()
        }

        //handle the click of start pdf list admin activity,
//        also pass pdf if,title

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfListAdminActivity::class.java)
            intent.putExtra("categoryId",id)
            intent.putExtra("category",category)
            context.startActivity(intent)
        }
    }

    private fun deleteCategory(model: ModelCategory, holder: HolderCategory) {
        //get id of category to delete
        val id = model.id

        //Firebase Db->Categories -> categoryID
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Unable to delete", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int {
        //number of items in list
        return categoryArrayList.size
    }


    //ViewHolder class to hold/init UI views for single_row.xml
    inner class HolderCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //init UI views
        var txtCategory: TextView = binding.txtCategory
        var btnDelete: ImageButton = binding.btnDelete

    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterCategory(filterList, this)
        }
        return filter as FilterCategory
    }


}
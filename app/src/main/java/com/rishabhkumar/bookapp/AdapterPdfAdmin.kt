package com.rishabhkumar.bookapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.rishabhkumar.bookapp.databinding.SingleRowPdfAdminBinding

class AdapterPdfAdmin : RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>, Filterable {

    //context
    private var context: Context

    //arraylist to hold pdfs
    public var pdfArrayList: ArrayList<ModelPdf>
    private val filterList: ArrayList<ModelPdf>


    //filter object
    var filter: FilterPdfAdmin? = null


    //constructor
    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }

    //view binding
    private lateinit var binding: SingleRowPdfAdminBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
        binding = SingleRowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderPdfAdmin(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
        /*
        Get data,set data,Handle click etc.
         */

        //Get Data
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp

        //convert timestamp to dd/mm/yyyy format
        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        //set data
        holder.txtTitle.text = title
        holder.txtDescription.text = description
        holder.txtDate.text = formattedDate

        //load further details like category, pdf from url,pdf size
        MyApplication.loadCategory(categoryId, holder.txtCategory)

        //we don't need page number,pass null for page number
        MyApplication.loadPdfFromUrlSinglePage(
            pdfUrl,
            title,
            holder.pdfView,
            holder.progressBar,
            null
        )

        //load pdf size
        MyApplication.loadPdfSize(pdfUrl, title, holder.txtSize)

    }

    override fun getItemCount(): Int {
        return pdfArrayList.size        //items count
    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterPdfAdmin(filterList,this)
        }

        return filter as FilterPdfAdmin
    }


    /*
    View Holder class for row_pdf_admin.xml
     */
    inner class HolderPdfAdmin(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //UI views of row_pdf_admin.xml
        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val txtTitle = binding.txtTitle
        val txtDescription = binding.txtDescription
        val txtCategory = binding.txtCategory
        val txtSize = binding.txtSize
        val txtDate = binding.txtDate
        val btnMore = binding.btnMore
    }
}
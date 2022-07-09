package com.rishabhkumar.bookapp

import android.widget.Filter

//Used to filter data from recyclerview | search pdf from pdf list in recyclerview
class FilterPdfAdmin : Filter {
    //arraylisr in which we want to search
    var filterList: ArrayList<ModelPdf>

    //adapter in which filter need to be implemented
    var adapterPdfAdmin: AdapterPdfAdmin

    //constructor
    constructor(filterList: ArrayList<ModelPdf>, adapterPdfAdmin: AdapterPdfAdmin) {
        this.filterList = filterList
        this.adapterPdfAdmin = adapterPdfAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint: CharSequence? = constraint
        val results = FilterResults()

        //value to be searched should not be null and not empty
        if (constraint != null && constraint.isNotEmpty()) {
            //change to uppercase , or lowercase to avoid case sensitivity
            constraint = constraint.toString().lowercase()
            var filteredModels = ArrayList<ModelPdf>()
            for (i in filterList.indices) {
//                validate if match
                if (filterList[i].title.lowercase().contains(constraint)) {
                    //searched value is similar to value in list, add to filtere list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        } else {
            //search value is either null or empty, return all data
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //apply filter changes
        adapterPdfAdmin.pdfArrayList = results.values as ArrayList<ModelPdf>

        //notify changes
        adapterPdfAdmin.notifyDataSetChanged()
    }
}
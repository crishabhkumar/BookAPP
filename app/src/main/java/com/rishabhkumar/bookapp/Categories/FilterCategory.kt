package com.rishabhkumar.bookapp.Categories

import android.widget.Filter

class FilterCategory : Filter {


    //arrayList in which we want to search
    private var filterList : ArrayList<ModelCategory>

    //adapter in which filter need to be implemented
    private var adapterCategory : AdapterCategory

    //constructor
    constructor(filterList: ArrayList<ModelCategory>, adapter: AdapterCategory) : super() {
        this.filterList = filterList
        this.adapterCategory = adapter
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        //value should not be null and not empty
        if(constraint != null && constraint.isNotEmpty()){
            //searched value is not null not empty


            //change to upper case , or lower case to avoid case sensitivity
            constraint = constraint.toString().uppercase()
            val filterModel:ArrayList<ModelCategory> = ArrayList()

            for(i in 0 until filterList.size){
                //validate
                if(filterList[i].category.uppercase().contains(constraint)){
                    //add the filtered list
                    filterModel.add(filterList[i])
                }
            }
            results.count = filterModel.size
            results.values = filterModel
        }else{
            //search value is either null or empty
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //apply filter changes
        adapterCategory.categoryArrayList = results.values as ArrayList<ModelCategory>

        //notify changes
        adapterCategory.notifyDataSetChanged()
    }


}
package com.eka.cacapp.ui.farmerConnect

import com.eka.cacapp.data.insight.AdvanceFltrData
import com.google.gson.JsonArray


interface FilterSelectListener {


    fun onBasicFilterSelected(value: JsonArray,
                              hashMapIsRowChecked : HashMap<String,Boolean>,
                              hashMapIsRowValuesChecked : HashMap<String,ArrayList<String>>,
                              advanceFltrData : AdvanceFltrData,
                              hMapAdvanceFltrData : HashMap<String,AdvanceFltrData>
    )
}
package com.eka.cacapp.ui.dashboard

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import com.eka.cacapp.R
import com.eka.cacapp.interfaces.SwtichCorporteCallBack
import org.json.JSONObject


class SwitchCorporate(mContext: Context,mViewModel: DashboardViewModel,
                      currentCorporRes : JSONObject,listOfCorporates :String) :Dialog(mContext) , SwtichCorporteCallBack {

    private lateinit var cancelTxt : TextView
    private lateinit var subHeaderText : TextView
    private lateinit var option : Spinner
    private var cntx : Context = mContext
    private lateinit var spnrArrow : ImageView
    private var viewModel : DashboardViewModel = mViewModel
    private var currCorpRes : JSONObject = currentCorporRes
    private var listOfCorRes :String = listOfCorporates
    private lateinit var switchBtn : TextView
    private lateinit var checkBox: CheckBox
    private var selectedCorpId = ""

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.switch_crp_scrn)

        cancelTxt =  findViewById(R.id.cancel_tv)
        option = findViewById(R.id.oper_one)
        spnrArrow = findViewById(R.id.spnr_arrow)
        subHeaderText = findViewById(R.id.sub_hdr)
        switchBtn = findViewById(R.id.apply_tv)
        checkBox = findViewById(R.id.checkbox)

        subHeaderText.setText("You're logged into corporate "+currCorpRes.optString("CorporateName"))

        cancelTxt.setOnClickListener {
            this.dismiss()
        }

        switchBtn.setOnClickListener {

            if(!checkBox.isChecked){
                Toast.makeText(cntx,"Please agree for switch corporate.",Toast.LENGTH_SHORT).show()
            }
            else if(selectedCorpId.isEmpty()){
                Toast.makeText(cntx,"Please select an option",Toast.LENGTH_SHORT).show()
            }else{
                viewModel.switchCorporate(selectedCorpId)
            }


        }


        var options = ArrayList<String>()
        var optionsKey = ArrayList<String>()
        val dataArr = JSONObject(listOfCorRes).optJSONArray("data")
        options.add(0,"Select")
        optionsKey.add(0,"")
        for(i in 0 until dataArr.length()){
            val jObj = dataArr.optJSONObject(i)
            options.add(i+1,jObj.optString("value"))
            optionsKey.add(i+1,jObj.optString("key"))
        }




        option.adapter = ArrayAdapter<String>(cntx, android.R.layout.simple_list_item_1, options)



        option.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            @SuppressLint("SetTextI18n")
            override fun onNothingSelected(parent: AdapterView<*>?)
            {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                selectedCorpId = optionsKey.get(position)

            }
        }


    }

    override fun onSuccess(jsonObj: String) {
           this.dismiss()
    }

    override fun onFailure() {
        Toast.makeText(cntx,"Something went wrong. Please try again later.",Toast.LENGTH_SHORT).show()
    }


}

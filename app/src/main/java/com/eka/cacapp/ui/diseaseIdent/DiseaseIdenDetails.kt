package com.eka.cacapp.ui.diseaseIdent

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.adapter.ColorPickerAdapter
import com.eka.cacapp.adapter.DiseaseIdenListAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.diseaseIden.DisIdenListModel
import com.eka.cacapp.databinding.DiseaDetailsFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DiseaseIdentificationRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import ja.burhanrashid52.photoeditor.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class DiseaseIdenDetails : Fragment() {

    private lateinit var mBinding : DiseaDetailsFragBinding

    private lateinit var mViewModel: DiseaseIdenViewModel

    private var currentFeedbackStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.disea_details_frag, container, false)

        (activity as DashboardActivity).clearSelectedViews()
        (activity as DashboardActivity).showBackButton()

        (activity as DashboardActivity).clearListView()

        val title = AppPreferences.getKeyValue(Constants.PrefCode.SELECTED_APP_NAME, "").toString()

        (activity as DashboardActivity).setTitle(title)



        val factory  = ViewModelFactory(DiseaseIdentificationRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(DiseaseIdenViewModel::class.java)


        mBinding.backImg.setOnClickListener {
            requireActivity().onBackPressed()
        }

        val selId =   AppPreferences.getKeyValue(Constants.PrefCode.DIS_IDEN_SEL_ID,"").toString()
        mViewModel.getdiseaseIdenDetails(selId)


        mViewModel.diseaseIdenDetailsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {

                    ProgressDialogUtil.hideProgressDialog()

                    try {

                        val res = it.value.string()
                        val json = JSONObject(res)
                        val responseArr = json.optJSONArray("response")
                        val responseObj = responseArr.optJSONObject(0)
                        val imageUrl = responseObj.optString("image_url")

                        Picasso.get().load(imageUrl).into(mBinding.imageView)

                        var title = ""
                        var disDtls = ""
                        var ttlColor = "#999999"

                        if(responseObj.optJSONArray("responseInfo").length()!==0){
                            val respInfoObj = responseObj.optJSONArray("responseInfo").optJSONObject(0)
                            if(respInfoObj.optString("status").equals("Failed")){
                                title = "INVALID IMAGE"
                                disDtls = "Invalid image"
                                ttlColor = "#999999"
                                mBinding.delItem.visibility = View.VISIBLE
                            }else if (respInfoObj.optString("status").equals("In Progress",true)){
                                title = "INVALID IMAGE"
                                disDtls = "Invalid image"
                                ttlColor = "#999999"
                                mBinding.delItem.visibility = View.GONE
                            }
                            else {
                                mBinding.delItem.visibility = View.GONE
                                if (respInfoObj.optString("category").equals("Infected")){
                                    title = "THREAT FOUND"
                                    disDtls = "The plant is infected with "+respInfoObj.optString("processName")
                                    ttlColor = "#D0021B"
                                }else {
                                    title = "NO THREAT FOUND"
                                    disDtls = "The plant is healthy"
                                    ttlColor = "#009688"
                                }

                            }
                        }

                        mBinding.title.setTextColor(Color.parseColor(ttlColor));
                        mBinding.title.text = title
                        mBinding.desc.text = disDtls
                        mBinding.dateTimeTv.text = disIdenDateFormat(responseObj.optLong("createdDate"))

                        currentFeedbackStatus = responseObj.optString("feedback")

                        updateFeebackOptions(currentFeedbackStatus)

                    }catch (e :Exception){

                    }



                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }

        })


        mBinding.delItem.setOnClickListener {
            DialogUtil.confirmationPopUp(requireContext(),
                    "Confirmation", "Are you Sure you want to delete ?","Ok",
                    "Cancel",
                    { mViewModel.deleteDisIdenRecord(selId)}
            )
        }


        mViewModel.diseaseIdenDeleteResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {

                    ProgressDialogUtil.hideProgressDialog()

                    try {

                        requireActivity().onBackPressed()
                    }catch (e :Exception){

                    }



                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }

        })


        mBinding.thumsUpBtn.setOnClickListener {
            val feebackJson = JsonObject()
            feebackJson.addProperty("feedback","Right Prediction")
            mViewModel.disIdenFeedback(selId,feebackJson )
        }

        mBinding.thumsDownBtn.setOnClickListener {
            val feebackJson = JsonObject()
            feebackJson.addProperty("feedback","Wrong Prediction")
            mViewModel.disIdenFeedback(selId,feebackJson )

        }

        mViewModel.diseaseIdenFeedbackResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {

                    ProgressDialogUtil.hideProgressDialog()

                    DialogUtil.infoPopUp(requireContext(),
                            "", "Thanks for the feedback.",
                            null)


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }

        })

        mBinding.shareImg.setOnClickListener {
            val bitmap=   screenShot(mBinding.imageView)
            bitmap?.let { showShareView(it) }
        }


        return mBinding.root

    }

    private fun updateFeebackOptions(currentFeedbackStatus: String) {

        if(currentFeedbackStatus.equals("",true)){
            mBinding.thumsDownBtn.isClickable = true
            mBinding.thumsUpBtn.isClickable = true

        }
        else if(currentFeedbackStatus.equals("Right Prediction",true)){
            mBinding.thumsDownBtn.isClickable = false
            mBinding.thumsUpBtn.isClickable = false

            mBinding.thumsUpBtn.background = requireContext().getDrawable(R.drawable.comment_btns_slctd_bg)
            mBinding.thumsDownBtn.alpha = 0.4f
        }else {
            mBinding.thumsDownBtn.background = requireContext().getDrawable(R.drawable.comment_btns_slctd_bg)
            mBinding.thumsUpBtn.alpha = 0.4f

            mBinding.thumsDownBtn.isClickable = false
            mBinding.thumsUpBtn.isClickable = false
        }

    }

    fun disIdenDateFormat(inputMilliSec: Long): String? {
        try {
            val date = Date(inputMilliSec)
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH)
            return dateFormat.format(date)
        }catch (e : Exception){
            return ""
        }

    }

    fun screenShot(view: View): Bitmap? {
        val bitmap: Bitmap = Bitmap.createBitmap(
                view.width,
                view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    private fun showShareView(bitmap: Bitmap) {

        val dialog = Dialog(requireActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.share_view_lay)
        val shareImage = dialog.findViewById<TextView>(R.id.share_img)
        val closeIcon = dialog.findViewById<ImageView>(R.id.close_icon)
        val addTextTv = dialog.findViewById<ImageView>(R.id.add_text)
        val addBrush = dialog.findViewById<ImageView>(R.id.add_brush)
        val clearAll = dialog.findViewById<TextView>(R.id.clear_all)
        val mPhotoEditorView: PhotoEditorView = dialog.findViewById(R.id.chart_shar_img)

        val addTextColorPickerRecyclerView: RecyclerView =
                dialog.findViewById(R.id.add_text_color_picker_recycler_view)

        var brushColorCode = 0
        var textColorCode = 0



        mPhotoEditorView.getSource().setImageBitmap(bitmap)
        var mPhotoEditor = PhotoEditor.Builder(requireContext(), mPhotoEditorView)
                .build()

        dialog.show()

        closeIcon.setOnClickListener {
            dialog.dismiss()
        }



        addTextTv.setOnClickListener {

            addTextColorPickerRecyclerView.visibility = View.GONE
            clearAll.visibility = View.VISIBLE

            val textEditorDialogFragment =
                    TextEditorDialogFragment.show(requireActivity())
            textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextColor(colorCode)
                styleBuilder.withTextSize(22f)
                mPhotoEditor.addText(inputText, styleBuilder)
                textColorCode = colorCode
                updateImageViewColor(addTextTv,textColorCode)

            }

        }

        addBrush.setOnClickListener {

            mPhotoEditor.setBrushDrawingMode(true)
            addTextColorPickerRecyclerView.visibility= View.VISIBLE
            clearAll.visibility = View.VISIBLE
        }



        val layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        addTextColorPickerRecyclerView.layoutManager = layoutManager
        addTextColorPickerRecyclerView.setHasFixedSize(true)

        val colorPickerAdapter = ColorPickerAdapter(requireActivity())
        colorPickerAdapter.setOnColorPickerClickListener { colorCode ->
            brushColorCode = colorCode
            mPhotoEditor.brushColor = brushColorCode

            updateImageViewColor(addBrush,brushColorCode)

        }
        addTextColorPickerRecyclerView.adapter = colorPickerAdapter


        shareImage.setOnClickListener {

            onClickShare(screenShot(mPhotoEditorView)!!)
        }


        clearAll.setOnClickListener {

            mPhotoEditor.clearAllViews()
            clearAll.visibility = View.GONE
            addTextColorPickerRecyclerView.visibility = View.GONE

            updateImageViewColor(addBrush,Color.BLACK)
            updateImageViewColor(addTextTv,Color.BLACK)


        }


        mPhotoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener {

            override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

            }

            override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
                val textEditorDialogFragment: TextEditorDialogFragment =
                        TextEditorDialogFragment.show(requireActivity(), text!!, colorCode)
                textEditorDialogFragment.setOnTextEditorListener({ inputText, newColorCode ->
                    val styleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(newColorCode)
                    styleBuilder.withTextSize(22f)
                    mPhotoEditor.editText(rootView!!, inputText, styleBuilder)

                    textColorCode = newColorCode
                    updateImageViewColor(addTextTv,textColorCode)

                })
            }

            override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

            }

            override fun onStartViewChangeListener(viewType: ViewType?) {

            }

            override fun onStopViewChangeListener(viewType: ViewType?) {

            }


        })

    }


    fun onClickShare( bitmap: Bitmap) {
        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(
                    requireContext().contentResolver,
                    bitmap,
                    "Eka",
                    null
            )
            val imageUri = Uri.parse(path)

            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = "image/*"

            waIntent.putExtra(Intent.EXTRA_STREAM, imageUri)


            val chooser = Intent.createChooser(waIntent, "Share File")

            val resInfoList: List<ResolveInfo> = requireContext().getPackageManager()
                    .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

            for (resolveInfo in resInfoList) {
                val packageName: String = resolveInfo.activityInfo.packageName
                requireContext().grantUriPermission(
                        packageName,
                        imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            startActivity(chooser)


        } catch (e: java.lang.Exception) {

        }
    }


    fun updateImageViewColor(imageView: ImageView, color : Int){
        val porterDuffColorFilter = PorterDuffColorFilter(
                color,
                PorterDuff.Mode.SRC_ATOP
        )
        imageView.setColorFilter(porterDuffColorFilter)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

}
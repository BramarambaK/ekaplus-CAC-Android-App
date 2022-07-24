package com.eka.cacapp.ui.diseaseIdent

import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eka.cacapp.R
import com.eka.cacapp.adapter.DiseaseIdenListAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.diseaseIden.DisIdenListModel
import com.eka.cacapp.databinding.DiseaHomeFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.network.UploadImgService
import com.eka.cacapp.repositories.DiseaseIdentificationRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.HttpClient.getUnsafeOkHttpClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class DiseaseIdenHome : Fragment() {

    private lateinit var mBinding : DiseaHomeFragBinding
    private var mUri: Uri? = null
    private val OPERATION_CAPTURE_PHOTO = 1
    private val OPERATION_CHOOSE_PHOTO = 2
    var service: UploadImgService? = null
    private lateinit var fileToUpload : File
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var mViewModel: DiseaseIdenViewModel
    var imageNameToSend = ""
    private lateinit var imagPreDlg : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.disea_home_frag, container, false)

        (activity as DashboardActivity).clearSelectedViews()
        (activity as DashboardActivity).showBackButton()

        (activity as DashboardActivity).clearListView()

        val title = AppPreferences.getKeyValue(Constants.PrefCode.SELECTED_APP_NAME, "").toString()

        (activity as DashboardActivity).setTitle(title)


        mBinding.addImg.setOnClickListener {
            showImageSelectionOption()
        }

        val factory  = ViewModelFactory(DiseaseIdentificationRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(DiseaseIdenViewModel::class.java)


        mViewModel.getdiseaseIdenCount()

        mViewModel.diseaseIdenCountResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED) {
                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()
                        val res = it.value.string()
                        val json = JSONObject(res)

                        val total = json.optInt("total")
                        val used = json.optInt("used")
                        val balance = total - used

                        mBinding.balanceTv.setText("Balance : " + balance)

                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        handleApiError(it) { null }
                    }
                }
            }
        })





        mViewModel.validateImageResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED) {
                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()
                        val res = it.value.string()
                        val json = JSONObject(res)
                        val isDuplicate = json.optBoolean("isDuplicateName")
                        if (isDuplicate) {
                            Toast.makeText(
                                    requireContext(),
                                    getString(R.string.image_name_exists),
                                    Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            imagPreDlg.dismiss()
                            uploadImage(fileToUpload, imageNameToSend)
                        }


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        handleApiError(it) { null }
                    }
                }
            }

        })




        mViewModel.getdiseaseIdenPredList()


        mViewModel.diseaseIdenPreListResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED) {
                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        try {

                            val res = it.value.string()
                            val json = JSONObject(res)

                            val disRes: DisIdenListModel = Gson().fromJson(
                                    res,
                                    DisIdenListModel::class.java
                            )

                            val myRecyclerViewAdapter =
                                    DiseaseIdenListAdapter(disRes, requireContext())
                            val llm = LinearLayoutManager(requireActivity())
                            llm.orientation = LinearLayoutManager.VERTICAL
                            mBinding.disList.layoutManager = llm
                            mBinding.disList.adapter = myRecyclerViewAdapter

                            myRecyclerViewAdapter.onItemClick = { selectedItem ->
                                AppPreferences.saveValue(
                                        Constants.PrefCode.DIS_IDEN_SEL_ID,
                                        selectedItem.requestId
                                )
                                findNavController().navigate(R.id.action_diseaseIdenHome_to_diseaseIdenDetails)
                            }


                            myRecyclerViewAdapter.onDeleteClick = { selectedItem ->
                                DialogUtil.confirmationPopUp(requireContext(),
                                        "Confirmation", "Are you Sure you want to delete ?", "Ok",
                                        "Cancel",
                                        { mViewModel.deleteDisIdenRecord(selectedItem.requestId) }
                                )
                            }

                        } catch (e: Exception) {

                        }


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        handleApiError(it) { null }
                    }
                }
            }

        })


        mViewModel.diseaseIdenDeleteResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED) {
                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        try {

                            mViewModel.getdiseaseIdenPredList()
                        } catch (e: Exception) {

                        }


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        handleApiError(it) { null }
                    }
                }
            }

        })


        pullToRefreshListener()


        return mBinding.root

    }

    private fun pullToRefreshListener() {
        mBinding.pullToRefresh.setOnRefreshListener {
            mBinding.pullToRefresh.isRefreshing = false
            mViewModel.getdiseaseIdenPredList()
        }
    }

    private fun openGalleryClicked(){
        val checkSelfPermission = ContextCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        else{
            openGallery()
        }
    }

    private fun capturePhoto(){
        val capturedImage = File(requireContext().externalCacheDir, "eka_disea_idnt.jpg")
        if(capturedImage.exists()) {
            capturedImage.delete()
        }
        capturedImage.createNewFile()


        mUri = if(Build.VERSION.SDK_INT >= 24){
            FileProvider.getUriForFile(requireContext(), "com.eka.CACApp.fileprovider",
                    capturedImage)
        } else {
            Uri.fromFile(capturedImage)
        }

        fileToUpload = capturedImage
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri)
        startActivityForResult(intent, OPERATION_CAPTURE_PHOTO)
    }
    private fun openGallery(){
        val intent = Intent("android.intent.action.GET_CONTENT")
        // intent.type = "image/*"
        val mimeTypes = arrayOf("image/png", "image/jpg", "image/jpeg")
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, OPERATION_CHOOSE_PHOTO)
    }
    private fun renderImage(imagePath: String?){
        if (imagePath != null) {
            val bitmap = BitmapFactory.decodeFile(imagePath)
            showImagePreview(bitmap)
        }
    }
    fun uploadImage(fileToUpld : File, fileName:String){

        ProgressDialogUtil.showProgressDialog(requireContext())

        val file = fileToUpld
        val mimeType = file.extension

        var reqJson = JsonObject()
        val userName = AppPreferences.getKeyValue(Constants.PrefCode.FIRST_NAME,"").toString();
        reqJson.addProperty("user",userName)
        val imageNameArr = JsonArray()
        imageNameArr.add(fileName)
        reqJson.add("imageName",imageNameArr)
        val jarray = JsonArray()
        jarray.add("coffee_wsb_disease")
        reqJson.add("processTypes",jarray)


        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();
        val token = AppPreferences.getKeyValue(Constants.PrefCode.USER_TOKEN,"").toString()
        service = Retrofit.Builder().baseUrl(baseUrl+"/spring/analytics/")
                .addConverterFactory(GsonConverterFactory.create()).
                client(getUnsafeOkHttpClient().build()).build().create(UploadImgService::class.java)

        val headers = HashMap<String, String>()


        headers[Constants.RequestParamCode.DEVICE_ID] = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
        headers[Constants.RequestParamCode.TENANT_DOMAIN] = baseUrl
        headers[Constants.RequestParamCode.AUTHORIZATION] = token
        headers[Constants.RequestParamCode.USER_ID] = AppPreferences.getKeyValue(Constants.PrefCode.USER_ID,"").toString()

        headers[Constants.RequestParamCode.REQUEST_ID] = UUID.randomUUID().toString().replace("-", "")
        headers[Constants.RequestParamCode.CONTENT_TYPE] = "multipart/form-data; boundary=Boundary-C6A364E1-1E44-4BCC-907C-B7A294C716B0"
        headers[Constants.RequestParamCode.SOURCE_DEVICE_ID] = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()





        val client: OkHttpClient = OkHttpClient().newBuilder()
                .build()

        val body1: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("requestJSON", reqJson.toString())
                .addFormDataPart("files",file.name,
                        RequestBody.create(
                            "image/jpeg".toMediaTypeOrNull(),
                                file))

                .build()

        val deviceId = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
        val boundary: String = "Boundary-" + System.currentTimeMillis()
        val request: Request = Request.Builder()
                .url(baseUrl+"/spring/analytics/upload?access_token="+token)
                .method("POST", body1)
                .addHeader("Device-Id", deviceId)
                .addHeader("Content-Type", "multipart/form-data; boundary="+boundary)
                .addHeader("Authorization", token)
                .addHeader("Tenant-Domain", baseUrl)
                .build()



        client.newCall(request).enqueue(object : Callback{
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                ProgressDialogUtil.hideProgressDialog()

                mViewModel.getdiseaseIdenPredList()
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                ProgressDialogUtil.hideProgressDialog()

            }
        })




    }



    private fun getImagePath(uri: Uri, selection: String?): String {
        var path: String? = null
        val cursor = requireActivity().contentResolver.query(uri, null, selection, null, null )
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path!!
    }


    private fun handleImage(data: Intent?) {
        var imagePath: String? = null
        val uri = data!!.data
        if (DocumentsContract.isDocumentUri(requireContext(), uri)){
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri!!.authority){
                val id = docId.split(":")[1]
                val selsetion = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selsetion)
            }
            else if ("com.android.providers.downloads.documents" == uri!!.authority){
                val contentUri = ContentUris.withAppendedId(Uri.parse(
                        "content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                imagePath = getImagePath(contentUri, null)
            }
        }
        else if ("content".equals(uri!!.scheme, ignoreCase = true)){
            imagePath = getImagePath(uri, null)
        }
        else if ("file".equals(uri.scheme, ignoreCase = true)){
            imagePath = uri.path
        }
        fileToUpload = File(imagePath)
        renderImage(imagePath)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>
                                            , grantedResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantedResults)
        when(requestCode){
            1 ->
                if (grantedResults.isNotEmpty() && grantedResults.get(0) ==
                        PackageManager.PERMISSION_GRANTED){
                    openGallery()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            OPERATION_CAPTURE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap = BitmapFactory.decodeStream(
                            requireActivity().getContentResolver().openInputStream(mUri!!))
                    showImagePreview(bitmap)

                }
            OPERATION_CHOOSE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImage(data)
                    }
                }
        }
    }


    private fun showImageSelectionOption() {
        val view: View = layoutInflater.inflate(R.layout.image_sel_lay, null)
        bottomSheetDialog = BottomSheetDialog(requireActivity())
        val takePhoto = view.findViewById<TextView>(R.id.take_photo)
        val openGallery = view.findViewById<TextView>(R.id.open_gall)
        val cancel = view.findViewById<TextView>(R.id.cancel)
        bottomSheetDialog.setContentView(view)

        takePhoto.setOnClickListener {
            bottomSheetDialog.dismiss()
            capturePhoto()
        }

        openGallery.setOnClickListener {
            bottomSheetDialog.dismiss()
            openGalleryClicked()
        }

        cancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()



    }


    private fun showImagePreview(bitmp : Bitmap) {

        imagPreDlg = Dialog(requireActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        imagPreDlg.setContentView(R.layout.dis_iden_img_prev)
        val closeImg = imagPreDlg.findViewById<ImageView>(R.id.close_icon)
        val prevImg = imagPreDlg.findViewById<ImageView>(R.id.img_prev)

        val imageNameEdtTxt = imagPreDlg.findViewById<EditText>(R.id.image_name_edttxt)
        val sendAnalysisBtn = imagPreDlg.findViewById<Button>(R.id.send_anal_btn)

        prevImg.setImageBitmap(bitmp)


        closeImg.setOnClickListener {
            imagPreDlg.dismiss()
        }

        sendAnalysisBtn.setOnClickListener {
            val imagName = imageNameEdtTxt.text.toString().trim()
            if(imagName.isNotEmpty()){
                imageNameToSend = imagName
                mViewModel.validateImage(imagName)

            }else{
                Toast.makeText(requireContext(),"Please enter image name",Toast.LENGTH_SHORT).show()
            }
        }


        imagPreDlg.show()



    }




}
package com.eka.cacapp.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface UploadImgService {

    @Multipart
    @POST("upload")
    fun postImage(@Part image: MultipartBody.Part?,
                  @Part("requestJSON") name: RequestBody,
            @HeaderMap headers: Map<String, String>,
            @Query("access_token") access_token :String
           ): Call<ResponseBody?>?


}
package com.vishaldairy.resource.service

import com.vishaldairy.resource.client.RetrofitClient
import com.vishaldairy.resource.responseModel.MCategoryResponse
import com.vishaldairy.resource.responseModel.MLoginResponse
import com.vishaldairy.utils.RouteConstants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

class ServiceLogin(
   map:HashMap<String,Any>
) {
    interface Api {
        @POST(RouteConstants.URL_LOGIN)
        fun getData(
            @Body data: HashMap<String,Any>
        ): Call<MLoginResponse>
    }
    val call: Call<MLoginResponse> = RetrofitClient.getClient().create(Api::class.java).getData(map)
}
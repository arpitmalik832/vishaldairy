package com.vishaldairy.resource.service

import com.vishaldairy.resource.client.RetrofitClient
import com.vishaldairy.resource.responseModel.MLoginResponse
import com.vishaldairy.utils.RouteConstants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

class ServiceSubscribeProduct(
    requestBody: HashMap<String, Any>
) {
    interface Api {
        @POST(RouteConstants.URL_SUBSCRIBE_PRODUCT)
        fun addBalance(
            @Body requestBody: HashMap<String, Any>
        ): Call<MLoginResponse>
    }

    val call: Call<MLoginResponse> =
        RetrofitClient.getClient().create(Api::class.java).addBalance(requestBody)
}
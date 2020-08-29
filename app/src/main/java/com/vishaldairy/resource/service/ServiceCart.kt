package com.vishaldairy.resource.service

import com.vishaldairy.resource.client.RetrofitClient
import com.vishaldairy.resource.responseModel.MCartResponse
import com.vishaldairy.utils.RouteConstants
import retrofit2.Call
import retrofit2.http.GET

class ServiceCart {
    interface Api {
        @GET(RouteConstants.URL_STORE_CART)
        fun getCart(): Call<MCartResponse>
    }

    val call: Call<MCartResponse> = RetrofitClient.getClient().create(Api::class.java).getCart()
}
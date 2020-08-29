package com.vishaldairy.resource.service

import com.vishaldairy.resource.client.RetrofitClient
import com.vishaldairy.resource.responseModel.MPaymentMethodResponse
import com.vishaldairy.utils.RouteConstants
import retrofit2.Call
import retrofit2.http.GET

class ServiceFetchPaymentMethods {
    interface Api {
        @GET(RouteConstants.URL_FETCH_PAYMENT_METHODS)
        fun getPaymentMethods(): Call<ArrayList<MPaymentMethodResponse>>
    }

    val call: Call<ArrayList<MPaymentMethodResponse>> = RetrofitClient.getClient().create(Api::class.java).getPaymentMethods()
}
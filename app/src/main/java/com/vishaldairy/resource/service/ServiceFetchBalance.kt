package com.vishaldairy.resource.service

import com.vishaldairy.resource.client.RetrofitClient
import com.vishaldairy.resource.responseModel.MWalletResponse
import com.vishaldairy.utils.RouteConstants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

class ServiceFetchBalance(
    requestBody: HashMap<String, Any>
) {
    interface Api {
        @POST(RouteConstants.URL_FETCH_WALLET_BALANCE)
        fun getBalance(
            @Body requestBody: HashMap<String, Any>
        ): Call<MWalletResponse>
    }

    val call: Call<MWalletResponse> = RetrofitClient.getClient().create(Api::class.java).getBalance(requestBody)
}
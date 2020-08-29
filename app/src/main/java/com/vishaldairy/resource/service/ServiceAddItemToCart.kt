package com.vishaldairy.resource.service

import com.vishaldairy.resource.client.RetrofitClient
import com.vishaldairy.resource.responseModel.MCartItemResponse
import com.vishaldairy.utils.RouteConstants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

class ServiceAddItemToCart(
    requestBody: HashMap<String, Any>
) {
    interface Api {
        @POST(RouteConstants.URL_ADD_ITEM_TO_CART)
        fun getCart(
            @Body requestBody: HashMap<String, Any>
        ): Call<MCartItemResponse>
    }

    val call: Call<MCartItemResponse> = RetrofitClient.getClient().create(Api::class.java).getCart(requestBody)
}
package com.vishaldairy.resource.service

import com.vishaldairy.resource.client.RetrofitClient
import com.vishaldairy.resource.responseModel.MCartItemResponse
import com.vishaldairy.utils.RouteConstants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

class ServiceUpdateQuantityOfItemInCart(
    key: String,
    requestBody: HashMap<String, Any>
) {
    interface Api {
        @POST(RouteConstants.URL_UPDATE_QUANTITY_OF_ITEM_IN_CART)
        fun getCart(
            @Path("key") key: String,
            @Body requestBody: HashMap<String, Any>
        ): Call<MCartItemResponse>
    }

    val call: Call<MCartItemResponse> = RetrofitClient.getClient().create(Api::class.java).getCart(key,requestBody)
}
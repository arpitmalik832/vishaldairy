package com.vishaldairy.resource.service

import com.vishaldairy.resource.client.RetrofitClient
import com.vishaldairy.resource.responseModel.MCartItemResponse
import com.vishaldairy.utils.RouteConstants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

class ServiceRemoveItemFromCart(
    key: String
) {
    interface Api {
        @DELETE(RouteConstants.URL_DELETE_ITEM_FROM_CART)
        fun getCart(
            @Path("key") key: String
        ): Call<MCartItemResponse>
    }

    val call: Call<MCartItemResponse> = RetrofitClient.getClient().create(Api::class.java).getCart(key)
}
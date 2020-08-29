package com.vishaldairy.resource.service

import com.vishaldairy.resource.client.RetrofitClient
import com.vishaldairy.resource.responseModel.MProductResponse
import com.vishaldairy.utils.RouteConstants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

class ServiceAllProducts(
    perPage: Int,
    page: Int
) {
    interface Api {
        @GET(RouteConstants.URL_V3_PRODUCTS)
        fun getProducts(
            @Query("per_page") perPage: Int,
            @Query("page") page: Int
        ): Call<ArrayList<MProductResponse>>
    }

    val call: Call<ArrayList<MProductResponse>> = RetrofitClient.getClient().create(Api::class.java).getProducts(perPage, page)
}
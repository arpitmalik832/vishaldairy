package com.vishaldairy.resource.service

import com.vishaldairy.resource.client.RetrofitClient
import com.vishaldairy.resource.responseModel.MCategoryResponse
import com.vishaldairy.utils.RouteConstants
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

class ServiceCategories(
    perPage: Int,
    pageNo: Int
) {
    interface Api {
        @GET(RouteConstants.URL_V3_CATEGORIES)
        fun getCategories(
            @Query("per_page") perPage: Int,
            @Query("page") pageNo: Int
        ): Call<ArrayList<MCategoryResponse>>
    }

    val call: Call<ArrayList<MCategoryResponse>> = RetrofitClient.getClient().create(Api::class.java).getCategories(perPage, pageNo)
}
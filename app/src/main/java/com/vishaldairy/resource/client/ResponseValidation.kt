package com.vishaldairy.resource.client

import retrofit2.Response

class ResponseValidation<T> {
    fun ifResponseIsNotSuccessful(response: Response<T>): Boolean {
        val apiResponse = response.body()
        return if(!response.isSuccessful) {
            true
        } else apiResponse == null
    }
}

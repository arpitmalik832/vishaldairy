package com.vishaldairy.resource.client

import com.vishaldairy.utils.AppConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitClient {

    companion object {

        private val auth = "Basic " + android.util.Base64.encodeToString(
            ("${AppConstants.CONSUMER_KEY}:${AppConstants.CONSUMER_SECRET}").toByteArray(),
            android.util.Base64.NO_WRAP
        )

        private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .addInterceptor(object : Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    return chain.proceed(chain.request().newBuilder().addHeader("Authorization", auth)
                        .method(chain.request().method, chain.request().body).build())
                }

            })
            .addInterceptor(interceptor)
            .build()
//
//            .addInterceptor {
//                object :Interceptor{}
//                val original = it.request()
//                val req: Request.Builder = original.newBuilder()
//                    .addHeader("Authorization", auth)
//                    .method(original.method, original.body)
//                val request: Request = req.build()
//                it.proceed(request)
//            }


        private val retrofit = Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        fun getClient(): Retrofit {
            return retrofit
        }

        private val interceptor: HttpLoggingInterceptor
            get() {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                return interceptor
            }
    }

}
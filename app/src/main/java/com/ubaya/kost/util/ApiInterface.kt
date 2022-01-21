package com.ubaya.kost.util

import com.ubaya.kost.data.Global
import com.ubaya.kost.data.models.Room
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {
    @Multipart
    @POST("/tenants")
    fun addTenantToRoom(
        @Part ktp: MultipartBody.Part
    ): Call<Room>

    companion object {
        val BASE_URL = "http://192.168.1.4/kost/public/api"

        fun getInstance(): ApiInterface {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request: Request =
                        chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer ${Global.authToken}")
                            .build()
                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()

            return retrofit.create(ApiInterface::class.java)
        }
    }
}
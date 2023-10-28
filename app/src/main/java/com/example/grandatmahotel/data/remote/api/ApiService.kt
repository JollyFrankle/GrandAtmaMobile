package com.example.grandatmahotel.data.remote.api

import com.example.grandatmahotel.data.remote.model.ApiResponse
import com.example.grandatmahotel.data.remote.model.LoginInnerResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): LoginResponse
}

data class LoginResponse(
    val message: String,
    val data: LoginInnerResponse
)
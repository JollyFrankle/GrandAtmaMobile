package com.example.grandatmahotel.data.remote.api

import com.example.grandatmahotel.data.remote.model.ApiResponse
import com.example.grandatmahotel.data.remote.model.JenisKamar
import com.example.grandatmahotel.data.remote.model.LoginCustomerInnerResponse
import com.example.grandatmahotel.data.remote.model.LoginPegawaiInnerResponse
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.data.remote.model.UserCustomer
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("public/jenis-kamar")
    suspend fun getJenisKamar(): ApiResponse<List<JenisKamar>>

    @POST("login-customer")
    @FormUrlEncoded
    suspend fun loginCustomer(
        @Field("username") email: String,
        @Field("password") password: String
    ): ApiResponse<LoginCustomerInnerResponse>

    @POST("login-pegawai")
    @FormUrlEncoded
    suspend fun loginPegawai(
        @Field("username") email: String,
        @Field("password") password: String
    ): ApiResponse<LoginPegawaiInnerResponse>

    @POST("register")
    suspend fun registerCustomer(
        @Body userCustomer: UserCustomer
    ): ApiResponse<UserCustomer>

    @POST("customer/logout")
    suspend fun logoutCustomer(
        @Header("Authorization") token: String
    ): ApiResponse<Nothing>

    @POST("pegawai/logout")
    suspend fun logoutPegawai(
        @Header("Authorization") token: String
    ): ApiResponse<Nothing>

    @POST("reset-password")
    @FormUrlEncoded
    suspend fun resetPassword(
        @Field("email") email: String,
        @Field("type") type: String
    ): ApiResponse<Nothing>

    @GET("customer/reservasi")
    suspend fun getReservasiCustomer(
        @Header("Authorization") token: String,
    ): ApiResponse<List<Reservasi>>

    @GET("customer/reservasi/{id}")
    suspend fun getDetailReservasiCustomer(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): ApiResponse<Reservasi>

    @GET("customer/user")
    suspend fun getDetailCustomer(
        @Header("Authorization") token: String,
    ): ApiResponse<UserCustomer>

    @PUT("customer/user")
    suspend fun updateCustomer(
        @Header("Authorization") token: String,
        @Body userCustomer: UserCustomer
    ): ApiResponse<UserCustomer>
}
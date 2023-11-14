package com.example.grandatmahotel.data.remote.api

import com.example.grandatmahotel.data.remote.model.ApiResponse
import com.example.grandatmahotel.data.remote.model.BookingResponse
import com.example.grandatmahotel.data.remote.model.BookingS1Input
import com.example.grandatmahotel.data.remote.model.DeadlineAndStage
import com.example.grandatmahotel.data.remote.model.FasilitasLayananTambahan
import com.example.grandatmahotel.data.remote.model.JenisKamar
import com.example.grandatmahotel.data.remote.model.LoginCustomerInnerResponse
import com.example.grandatmahotel.data.remote.model.LoginPegawaiInnerResponse
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.data.remote.model.ReservasiInput
import com.example.grandatmahotel.data.remote.model.TarifKamar
import com.example.grandatmahotel.data.remote.model.UserCustomer
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("public/jenis-kamar")
    suspend fun getJenisKamar(): ApiResponse<List<JenisKamar>>

    @GET("public/layanan-tambahan")
    suspend fun getFLT(): ApiResponse<List<FasilitasLayananTambahan>>

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
        @Query("status") status: String? = null
    ): ApiResponse<List<Reservasi>>

    @GET("customer/reservasi/{id}")
    suspend fun getDetailReservasiCustomer(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Reservasi>

    @DELETE("customer/reservasi/{id}")
    suspend fun cancelReservasiCustomer(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Nothing>

    @GET("customer/user")
    suspend fun getDetailCustomer(
        @Header("Authorization") token: String,
    ): ApiResponse<UserCustomer>

    @PUT("customer/user")
    suspend fun updateCustomer(
        @Header("Authorization") token: String,
        @Body userCustomer: UserCustomer
    ): ApiResponse<UserCustomer>

    @POST("customer/booking")
    suspend fun booking(
        @Header("Authorization") token: String,
        @Body reservasiInput: ReservasiInput
    ): ApiResponse<BookingResponse>

    @GET("customer/booking/{idR}/deadline")
    suspend fun getBookingDeadline(
        @Header("Authorization") token: String,
        @Path("idR") idR: Long
    ): ApiResponse<DeadlineAndStage>

    @POST("customer/booking/{id}/step-1")
    suspend fun submitBookingStep1(
        @Header("Authorization") token: String,
        @Body input: BookingS1Input,
        @Path("id") id: Long
    ): ApiResponse<Reservasi>

    @POST("customer/booking/{id}/step-2")
    suspend fun submitBookingStep2(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Nothing>

    @POST("customer/booking/{id}/step-3")
    @Multipart
    suspend fun submitBookingStep3(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): ApiResponse<Nothing>

    @POST("public/booking/search")
    @FormUrlEncoded
    suspend fun searchForRooms(
        @Field("check_in") checkIn: String,
        @Field("check_out") checkOut: String,
        @Field("jumlah_dewasa") jumlahDewasa: Int,
        @Field("jumlah_anak") jumlahAnak: Int,
        @Field("jumlah_kamar") jumlahKamar: Int
    ): ApiResponse<List<TarifKamar>>
}
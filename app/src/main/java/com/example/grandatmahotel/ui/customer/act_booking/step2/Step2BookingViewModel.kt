package com.example.grandatmahotel.ui.customer.act_booking.step2

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.ApiErrorResponse
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class Step2BookingViewModel(private val application: Application): ViewModel() {

    private val _reservasi = MutableLiveData<Result<Reservasi>>()
    val reservasi: LiveData<Result<Reservasi>> = _reservasi

    private val _submitResult = MutableLiveData<Result<Nothing?>>()
    val submitResult: LiveData<Result<Nothing?>> = _submitResult

    fun getReservasi(id: Long) = viewModelScope.launch {
        try {
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getDetailReservasiCustomer(
                token = "Bearer $token",
                id = id
            ).data

            _reservasi.value = Result.Success(data)
        } catch (e: IOException) {
            // No Internet Connection
            _reservasi.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _reservasi.value = Result.Error(errorResponse.message)
        }
    }

    fun submit(id: Long) = viewModelScope.launch {
        try {
            _submitResult.value = Result.Loading
            val token = Utils.getToken(application)
            ApiConfig.getApiService().submitBookingStep2(
                token = "Bearer $token",
                id = id
            )
            _submitResult.value = Result.Success(null)
        } catch (e: IOException) {
            // No Internet Connection
            _submitResult.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _submitResult.value = Result.Error(errorResponse.message)
        }
    }

}
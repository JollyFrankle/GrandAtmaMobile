package com.example.grandatmahotel.ui.customer.riwayat

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

class CustomerRiwayatViewModel(private val application: Application): ViewModel() {

    private val _list = MutableLiveData<Result<List<Reservasi>>>()
    val list: LiveData<Result<List<Reservasi>>> = _list

    private val _cancelResult = MutableLiveData<Result<Nothing?>>()
    val cancelResult: LiveData<Result<Nothing?>> = _cancelResult

    private val status = listOf(
        "upcoming",
        "completed",
        "cancelled"
    )

    var selectedTab = 0

    fun refreshReservasi() = getReservasi(selectedTab)

    fun getReservasi(tabIndex: Int) = viewModelScope.launch {
        try {
            this@CustomerRiwayatViewModel.selectedTab = tabIndex
            _list.value = Result.Loading
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getReservasiCustomer(
                token = "Bearer $token",
                status = status[this@CustomerRiwayatViewModel.selectedTab]
            ).data
            _list.value = Result.Success(data)
        } catch (e: IOException) {
            // No Internet Connection
            _list.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _list.value = Result.Error(errorResponse.message)
        }
    }

    fun cancelReservasi(id: Long) = viewModelScope.launch {
        try {
            _cancelResult.value = Result.Loading
            val token = Utils.getToken(application)
            ApiConfig.getApiService().cancelReservasiCustomer(
                token = "Bearer $token",
                id = id
            )

            _cancelResult.value = Result.Success(null)
        } catch (e: IOException) {
            // No Internet Connection
            _cancelResult.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _cancelResult.value = Result.Error(errorResponse.message)
        }
    }
}
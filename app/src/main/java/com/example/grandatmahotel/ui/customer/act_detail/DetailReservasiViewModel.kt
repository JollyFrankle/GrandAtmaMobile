package com.example.grandatmahotel.ui.customer.act_detail

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

class DetailReservasiViewModel(private val application: Application): ViewModel() {

    private val _data = MutableLiveData<Result<Reservasi>>()
    val data: LiveData<Result<Reservasi>> = _data

    fun getDetailReservasi(id: Int) = viewModelScope.launch {
        try {
            _data.value = Result.Loading
            val token = Utils.getToken(application)
            val response = ApiConfig.getApiService().getDetailReservasiCustomer(
                token = "Bearer $token",
                id = id
            )

            val data = response.data
            _data.value = Result.Success(data)
        } catch (e: IOException) {
            // No Internet Connection
            _data.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _data.value = Result.Error(errorResponse.message)
        }
    }
}
package com.example.grandatmahotel.ui.customer.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.ApiErrorResponse
import com.example.grandatmahotel.data.remote.model.UserCustomer
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.utils.Event
import com.example.grandatmahotel.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ProfileViewModel(private val application: Application): ViewModel() {

    private val _detail = MutableLiveData<Result<UserCustomer>>()
    val detail: LiveData<Result<UserCustomer>> = _detail

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _errors = MutableLiveData<ApiErrorResponse>()
    val errors: LiveData<ApiErrorResponse> = _errors

    init {
        getDetail()
    }

    private fun getDetail() = viewModelScope.launch {
        try {
            _detail.value = Result.Loading
            val token = Utils.getToken(application)
            val response = ApiConfig.getApiService().getDetailCustomer(
                token = "Bearer $token"
            )
            val data = response.data
            _detail.value = Result.Success(data)
        } catch (e: IOException) {
            // No Internet Connection
            _detail.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _detail.value = Result.Error(errorResponse.message)
        }
    }

    fun updateProfile(newProfile: UserCustomer) = viewModelScope.launch {
        try {
            _detail.value = Result.Loading
            val token = Utils.getToken(application)
            val response = ApiConfig.getApiService().updateCustomer(
                token = "Bearer $token",
                userCustomer = newProfile
            )
            val data = response.data
            _detail.value = Result.Success(data)
            _message.value = Event(response.message)
        } catch (e: IOException) {
            // No Internet Connection
            _detail.value = Result.Error(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _detail.value = Result.Error(errorResponse.message)
            _errors.value = errorResponse
        }
    }

    fun logout() = viewModelScope.launch {
        try {
            val token = Utils.getToken(application)
            val response = ApiConfig.getApiService().logoutCustomer(
                token = "Bearer $token"
            )
            _message.value = Event(response.message)
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _message.value = Event(errorResponse.message)
        } finally {
            Utils.setUserCustomer(application, null)
            Utils.setToken(application, "")
        }
    }
}
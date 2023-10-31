package com.example.grandatmahotel.ui.auth.register

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.data.remote.model.ApiErrorResponse
import com.example.grandatmahotel.data.remote.model.LoginCustomerInnerResponse
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.utils.Event
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RegisterViewModel(private val application: Application): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _registerResult = MutableLiveData<LoginCustomerInnerResponse>()
    val registerResult: LiveData<LoginCustomerInnerResponse> = _registerResult

    private val _registerError = MutableLiveData<ApiErrorResponse>()
    val registerError: LiveData<ApiErrorResponse> = _registerError

    fun clientRegister(email: String, password: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val data = ApiConfig.getApiService().loginCustomer(
                email = email,
                password = password
            ).data

            _registerResult.value = data
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _registerError.value = errorResponse
            _message.value = Event(errorResponse.message)
        } finally {
            _isLoading.value = false
        }
    }
}
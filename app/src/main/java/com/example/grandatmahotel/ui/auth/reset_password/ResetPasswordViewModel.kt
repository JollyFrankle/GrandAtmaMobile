package com.example.grandatmahotel.ui.auth.reset_password

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.data.remote.model.ApiErrorResponse
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.utils.Event
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ResetPasswordViewModel(private val application: Application): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _isSuccess = MutableLiveData<Boolean?>()
    val isSuccess: LiveData<Boolean?> = _isSuccess

    private val _errors = MutableLiveData<ApiErrorResponse>()
    val errors: LiveData<ApiErrorResponse> = _errors

    fun requestResetPasword(email: String, type: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val response = ApiConfig.getApiService().resetPassword(email, type)
            _message.value = Event(response.message)

            _isSuccess.value = true
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
            _isSuccess.value = false
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _errors.value = errorResponse
            _message.value = Event(errorResponse.message)
            _isSuccess.value = false
        } finally {
            _isLoading.value = false
        }
    }
}
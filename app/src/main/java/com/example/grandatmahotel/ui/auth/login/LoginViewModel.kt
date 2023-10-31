package com.example.grandatmahotel.ui.auth.login

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.data.remote.model.ApiErrorResponse
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.utils.Event
import com.example.grandatmahotel.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(private val application: Application): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _loginResult = MutableLiveData<Char?>(null)
    val loginResult: LiveData<Char?> = _loginResult

    private val _loginError = MutableLiveData<ApiErrorResponse>()
    val loginError: LiveData<ApiErrorResponse> = _loginError

    fun loginAsCustomer(email: String, password: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val response = ApiConfig.getApiService().loginCustomer(
                email = email,
                password = password
            )

            // Set preferences
            val data = response.data
            Utils.setToken(application, data.token)
            Utils.setUserCustomer(application, data.user)
            Toast.makeText(application, response.message, Toast.LENGTH_SHORT).show()

            _loginResult.value = 'c'
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _loginError.value = errorResponse
            _message.value = Event(errorResponse.message)
        } finally {
            _isLoading.value = false
        }
    }

    fun loginAsPegawai(email: String, password: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val response = ApiConfig.getApiService().loginPegawai(
                email = email,
                password = password
            )

            // Set preferences
            val data = response.data
            if (data.user.role != "owner" || data.user.role != "gm") {
                _message.value = Event("Anda tidak memiliki akses")
            } else {
                Utils.setToken(application, data.token)
                Utils.setUserPegawai(application, data.user)
                Toast.makeText(application, response.message, Toast.LENGTH_SHORT).show()
                _loginResult.value = 'p'
            }
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _loginError.value = errorResponse
            _message.value = Event(errorResponse.message)
        } finally {
            _isLoading.value = false
        }
    }
}
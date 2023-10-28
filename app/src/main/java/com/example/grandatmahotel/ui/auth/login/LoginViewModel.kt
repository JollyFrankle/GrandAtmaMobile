package com.example.grandatmahotel.ui.auth.login

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.data.remote.model.ApiErrorResponse
import com.example.grandatmahotel.data.remote.model.LoginInnerResponse
import com.example.grandatmahotel.data.remote.model.UserCustomer
import com.example.grandatmahotel.data.remote.model.UserPegawai
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

    private val _loginResult = MutableLiveData<LoginInnerResponse>()
    val loginResult: LiveData<LoginInnerResponse> = _loginResult

    private val _loginError = MutableLiveData<ApiErrorResponse>()
    val loginError: LiveData<ApiErrorResponse> = _loginError

    fun clientLogin(email: String, password: String) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val data = ApiConfig.getApiService().login(
                email = email,
                password = password
            )

            // Set preferences
            val userC = if (data.message == "Berhasil login sebagai customer") {
                data.data.user as UserCustomer
            } else {
                null
            }

            val userP = if (data.message == "Berhasil login sebagai pegawai") {
                data.data.user as UserPegawai
            } else {
                null
            }

            if (userC != null) {
                Utils.setToken(application, data.data.token)
                Utils.setUserCustomer(application, userC)
                Toast.makeText(application, "Berhasil login sebagai customer", Toast.LENGTH_SHORT).show()
            } else if (userP != null) {
                Utils.setToken(application, data.data.token)
                Utils.setUserPegawai(application, userP)
                Toast.makeText(application, "Berhasil login sebagai pegawai", Toast.LENGTH_SHORT).show()
            }

            _loginResult.value = data.data
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
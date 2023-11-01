package com.example.grandatmahotel.ui.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.ApiErrorResponse
import com.example.grandatmahotel.data.remote.model.JenisKamar
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeViewModel(private val application: Application): ViewModel() {

    private val _list = MutableLiveData<Result<List<JenisKamar>>>()
    val list: LiveData<Result<List<JenisKamar>>> = _list

    private val _userType = MutableLiveData<Char?>()
    val userType: LiveData<Char?> = _userType

    init {
        getList()
    }

    fun getList() = viewModelScope.launch {
        try {
            _list.value = Result.Loading
            val data = ApiConfig.getApiService().getJenisKamar().data
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

    fun getUser() = viewModelScope.launch {
        if (Utils.getUserCustomer(application) != null) {
            _userType.value = 'c'
        } else if (Utils.getUserPegawai(application) != null) {
            _userType.value = 'p'
        } else {
            _userType.value = null
        }
    }
}
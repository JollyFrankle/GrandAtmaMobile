package com.example.grandatmahotel.ui.customer.act_booking

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
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

class BookingViewModel(private val application: Application): ViewModel() {

    private val _deadlineSeconds = MutableLiveData<Long>(0)
    val deadlineSeconds: LiveData<Long> = _deadlineSeconds

    private val _currentPage = MutableLiveData<Int>()
    val currentPage: LiveData<Int> = _currentPage

    private val _cdFinished = MutableLiveData<Boolean>(false)
    val cdFinished: LiveData<Boolean> = _cdFinished

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    fun getDeadlineAndStage(idReservasi: Long) = viewModelScope.launch {
        try {
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().getBookingDeadline(
                token = "Bearer $token",
                idR = idReservasi
            ).data

            _currentPage.value = data.stage-1

            val deadlineTime = Utils.parseDate(data.deadline, Utils.DF_TIMESTAMP).time
            val currentTime = System.currentTimeMillis()

            Log.e("TAG789", "getDeadlineAndStage: $deadlineTime, $currentTime")

            val timer = object: CountDownTimer(deadlineTime - currentTime + 1000, 1000) {
                override fun onTick(p0: Long) {
                    _deadlineSeconds.value = p0 / 1000
                }

                override fun onFinish() {
                    _cdFinished.value = true
                }
            }
            timer.start()

            Log.e("TAG456", "getDeadlineAndStage: $data")
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _message.value = Event(errorResponse.message)
        }
    }
}
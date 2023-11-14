package com.example.grandatmahotel.ui.customer.act_room_search

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.ApiErrorResponse
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.data.remote.model.ReservasiInput
import com.example.grandatmahotel.data.remote.model.TarifKamar
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.utils.Event
import com.example.grandatmahotel.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.Date

class RoomSearchViewModel(private val application: Application): ViewModel() {

    private val _list = MutableLiveData<Result<List<TarifKamar>>>()
    val list: LiveData<Result<List<TarifKamar>>> = _list

    private val _reservasi = MutableLiveData<Reservasi?>(null)
    val reservasi: LiveData<Reservasi?> = _reservasi

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    var tglCheckIn: Long = 0
    var tglCheckOut: Long = 0
    var jumlahDewasa = 2
    var jumlahAnak = 0
    var jumlahKamar = 1

    fun getList(
        tglCheckIn: Long,
        tglCheckOut: Long,
        jumlahDewasa: Int,
        jumlahAnak: Int,
        jumlahKamar: Int
    ) = viewModelScope.launch {
        this@RoomSearchViewModel.tglCheckIn = tglCheckIn
        this@RoomSearchViewModel.tglCheckOut = tglCheckOut
        this@RoomSearchViewModel.jumlahDewasa = jumlahDewasa
        this@RoomSearchViewModel.jumlahAnak = jumlahAnak
        this@RoomSearchViewModel.jumlahKamar = jumlahKamar

        try {
            _list.value = Result.Loading
            val data = ApiConfig.getApiService().searchForRooms(
                checkIn = Utils.formatDate(Date(tglCheckIn), Utils.DF_YMD),
                checkOut = Utils.formatDate(Date(tglCheckOut), Utils.DF_YMD),
                jumlahDewasa = jumlahDewasa,
                jumlahAnak = jumlahAnak,
                jumlahKamar = jumlahKamar
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

    fun booking(bookBody: ReservasiInput) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val token = Utils.getToken(application)
            val data = ApiConfig.getApiService().booking(
                token = "Bearer $token",
                reservasiInput = bookBody
            ).data
            _reservasi.value = data.reservasi
        } catch (e: IOException) {
            // No Internet Connection
            _message.value = Event(e.message.toString())
            _reservasi.value = null
        } catch (e: HttpException) {
            // Error Response (4xx, 5xx)
            val errorResponse = Gson().fromJson(e.response()?.errorBody()?.string(), ApiErrorResponse::class.java)
            _message.value = Event(errorResponse.message)
            _reservasi.value = null
        } finally {
            _isLoading.value = false
        }
    }

    fun refreshList() = getList(tglCheckIn, tglCheckOut, jumlahDewasa, jumlahAnak, jumlahKamar)

}
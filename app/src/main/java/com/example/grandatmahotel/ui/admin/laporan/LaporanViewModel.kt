package com.example.grandatmahotel.ui.admin.laporan

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.utils.Utils
import kotlinx.coroutines.launch
import java.util.Calendar

class LaporanViewModel(private val application: Application): ViewModel() {

    val tahun = MutableLiveData(Calendar.getInstance().get(Calendar.YEAR).toString())
    private var index = 0

    private val _url = MutableLiveData<String>()
    val url: LiveData<String> = _url

    private val _urlExport = MutableLiveData<String>()
    val urlExport: LiveData<String> = _urlExport

    private val _judulLaporan = MutableLiveData<String>()
    val judulLaporan: LiveData<String> = _judulLaporan

    init {
        tahun.observeForever {
            getURL()
        }
    }

    fun getURL(noLaporan: Int = index) = viewModelScope.launch {
        val token = Utils.getToken(application)
        this@LaporanViewModel.index = noLaporan

        when (noLaporan) {
            1 -> _judulLaporan.value = "Laporan Customer Baru per Bulan"
            2 -> _judulLaporan.value = "Laporan pendapatan & grafik per jenis tamu per bulan"
            3 -> _judulLaporan.value = "Laporan & grafik jumlah tamu menginap per jenis kamar pada bulan tertentu"
            4 -> _judulLaporan.value = "Laporan 5 Customer dengan Pemesanan Terbanyak"
        }

        val queryParams = mutableMapOf(
            "token" to token,
            "tahun" to tahun.value,
            "readonly" to "true",
            "ts" to System.currentTimeMillis().toString()
        )

        val queryParamString = queryParams.map { "${it.key}=${it.value}" }.joinToString("&")
        _url.value = "${ApiConfig.BASE_URL}/public/pdf/laporan/${noLaporan}?$queryParamString"

        val queryParamsExport = mutableMapOf(
            "token" to token,
            "tahun" to tahun.value,
            "ts" to System.currentTimeMillis().toString()
        )

        val queryParamStringExport = queryParamsExport.map { "${it.key}=${it.value}" }.joinToString("&")
        _urlExport.value = "${ApiConfig.BASE_URL}/public/pdf/laporan/${noLaporan}?$queryParamStringExport"
    }

}
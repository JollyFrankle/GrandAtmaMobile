package com.example.grandatmahotel.ui.customer

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.utils.Utils
import kotlinx.coroutines.launch

class CustomerDashboardViewModel(private val application: Application): ViewModel() {

    fun logout() = viewModelScope.launch {
        Utils.setUserCustomer(application, null)
        Utils.setToken(application, "")
    }

}
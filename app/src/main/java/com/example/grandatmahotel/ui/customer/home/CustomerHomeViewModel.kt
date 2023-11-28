package com.example.grandatmahotel.ui.customer.home

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grandatmahotel.data.remote.model.UserCustomer
import com.example.grandatmahotel.utils.Utils
import kotlinx.coroutines.launch

class CustomerHomeViewModel(private val application: Application): ViewModel() {

    private val _customer = MutableLiveData<UserCustomer?>()
    val customer: LiveData<UserCustomer?> = _customer

    init {
        getCustomer()
    }

    private fun getCustomer() = viewModelScope.launch {
        _customer.value = Utils.getUserCustomer(application)
    }
}
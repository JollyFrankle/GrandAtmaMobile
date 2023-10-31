package com.example.grandatmahotel.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.ui.auth.MainViewModel
import com.example.grandatmahotel.ui.auth.login.LoginViewModel
import com.example.grandatmahotel.ui.customer.CustomerDashboardViewModel
import com.example.grandatmahotel.ui.customer.home.CustomerHomeViewModel
import com.example.grandatmahotel.ui.home.HomeViewModel

class ViewModelFactory private constructor(private val application: Application): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(application) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(application) as T
            modelClass.isAssignableFrom(CustomerDashboardViewModel::class.java) -> CustomerDashboardViewModel(application) as T
            modelClass.isAssignableFrom(CustomerHomeViewModel::class.java) -> CustomerHomeViewModel(application) as T
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(application: Application): ViewModelFactory =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(application)
            }
    }
}
package com.example.grandatmahotel.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.ui.admin.AdminDashboardViewModel
import com.example.grandatmahotel.ui.admin.laporan.LaporanViewModel
import com.example.grandatmahotel.ui.auth.MainViewModel
import com.example.grandatmahotel.ui.auth.login.LoginViewModel
import com.example.grandatmahotel.ui.auth.register.RegisterViewModel
import com.example.grandatmahotel.ui.auth.reset_password.ResetPasswordViewModel
import com.example.grandatmahotel.ui.customer.CustomerDashboardViewModel
import com.example.grandatmahotel.ui.customer.act_booking.BookingViewModel
import com.example.grandatmahotel.ui.customer.act_booking.step1.Step1BookingViewModel
import com.example.grandatmahotel.ui.customer.act_booking.step2.Step2BookingViewModel
import com.example.grandatmahotel.ui.customer.act_booking.step3.Step3BookingViewModel
import com.example.grandatmahotel.ui.customer.act_detail.DetailReservasiViewModel
import com.example.grandatmahotel.ui.customer.act_room_search.RoomSearchViewModel
import com.example.grandatmahotel.ui.customer.home.CustomerHomeViewModel
import com.example.grandatmahotel.ui.customer.profile.ProfileViewModel
import com.example.grandatmahotel.ui.customer.riwayat.CustomerRiwayatViewModel
import com.example.grandatmahotel.ui.home.HomeViewModel

class ViewModelFactory private constructor(private val application: Application): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // GENERAL & AUTH
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(application) as T
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(application) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(application) as T
            modelClass.isAssignableFrom(ResetPasswordViewModel::class.java) -> ResetPasswordViewModel(application) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(application) as T

            // CUSTOMER
            modelClass.isAssignableFrom(CustomerDashboardViewModel::class.java) -> CustomerDashboardViewModel(application) as T
            modelClass.isAssignableFrom(CustomerRiwayatViewModel::class.java) -> CustomerRiwayatViewModel(application) as T
            modelClass.isAssignableFrom(DetailReservasiViewModel::class.java) -> DetailReservasiViewModel(application) as T
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> ProfileViewModel(application) as T
            modelClass.isAssignableFrom(CustomerHomeViewModel::class.java) -> CustomerHomeViewModel(application) as T
            modelClass.isAssignableFrom(RoomSearchViewModel::class.java) -> RoomSearchViewModel(application) as T
            modelClass.isAssignableFrom(BookingViewModel::class.java) -> BookingViewModel(application) as T
            modelClass.isAssignableFrom(Step1BookingViewModel::class.java) -> Step1BookingViewModel(application) as T
            modelClass.isAssignableFrom(Step2BookingViewModel::class.java) -> Step2BookingViewModel(application) as T
            modelClass.isAssignableFrom(Step3BookingViewModel::class.java) -> Step3BookingViewModel(application) as T

            // PEGAWAI (ADMIN)
            modelClass.isAssignableFrom(AdminDashboardViewModel::class.java) -> AdminDashboardViewModel(application) as T
            modelClass.isAssignableFrom(LaporanViewModel::class.java) -> LaporanViewModel(application) as T
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
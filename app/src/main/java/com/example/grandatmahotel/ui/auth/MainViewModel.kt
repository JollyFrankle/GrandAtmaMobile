package com.example.grandatmahotel.ui.auth

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.grandatmahotel.utils.Utils

class MainViewModel(private val application: Application): ViewModel() {

    suspend fun getUserType(): Char? {
        val token = Utils.getToken(application)
        if (token.isEmpty()) {
            return null
        }

        val userC = Utils.getUserCustomer(application)
        if (userC != null) {
            return 'c'
        }

        val userP = Utils.getUserPegawai(application)
        if (userP != null) {
            return 'p'
        }

        return null
    }

}
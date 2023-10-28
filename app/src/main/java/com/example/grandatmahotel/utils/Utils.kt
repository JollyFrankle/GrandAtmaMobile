package com.example.grandatmahotel.utils

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.model.UserCustomer
import com.example.grandatmahotel.data.remote.model.UserPegawai
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
object Utils {
    suspend fun getToken(context: Context): String {
        val pref = AppPreferences.getInstance(context.dataStore)
        return pref.getToken().first()
    }

    suspend fun setToken(context: Context, value: String) {
        val pref = AppPreferences.getInstance(context.dataStore)
        pref.setToken(value)
    }

    suspend fun getUserCustomer(context: Context): UserCustomer? {
        val pref = AppPreferences.getInstance(context.dataStore)
        return pref.getUserCustomer().first()
    }

    suspend fun setUserCustomer(context: Context, value: UserCustomer?) {
        val pref = AppPreferences.getInstance(context.dataStore)
        pref.setUserCustomer(value)
    }

    suspend fun getUserPegawai(context: Context): UserPegawai? {
        val pref = AppPreferences.getInstance(context.dataStore)
        return pref.getUserPegawai().first()
    }

    suspend fun setUserPegawai(context: Context, value: UserPegawai?) {
        val pref = AppPreferences.getInstance(context.dataStore)
        pref.setUserPegawai(value)
    }

    fun generateLoader(context: Context): AlertDialog {
        val view = View.inflate(context, R.layout.layout_loader, null)
        return AlertDialog.Builder(context, R.style.GAH_LoadingDialog)
            .setView(view)
            .setCancelable(false)
            .create()
    }

    fun getCurrentDate(pattern: String = "yyyy-MM-dd"): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }

    fun formatDate(time: Date, pattern: String = "yyyy-MM-dd"): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(time)
    }

    fun parseDate(dateString: String, pattern: String = "yyyy-MM-dd"): Date {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.parse(dateString)!!
    }
}
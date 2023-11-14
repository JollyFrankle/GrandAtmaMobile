package com.example.grandatmahotel.utils

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.data.remote.model.UserCustomer
import com.example.grandatmahotel.data.remote.model.UserPegawai
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
object Utils {
    const val DF_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DF_DATE_READABLE = "d MMMM yyyy"
    const val DF_DATE_SHORT = "d MMM yyyy"
    const val DF_YMD = "yyyy-MM-dd"

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

    fun getCurrentDate(pattern: String = DF_TIMESTAMP, dateDiff: Int = 0): String {
        val date = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, dateDiff)
        return formatDate(calendar.time, pattern)
    }

    fun getCurrentDateAsDate(dateDiff: Int = 0): Date {
        val date = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, dateDiff)
        return calendar.time
    }

    fun formatDate(time: Date, pattern: String = DF_TIMESTAMP): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(time)
    }

    fun parseDate(dateString: String, pattern: String = DF_TIMESTAMP): Date {
        return if (pattern == DF_TIMESTAMP) {
            // this is timestamp: UTC based
            // we need to convert it to local time
            val instant = Instant.parse(dateString)

            // Get the device's local time zone
            val deviceTimeZone = ZoneId.systemDefault()

            // Convert the UTC based instant to the device's local time zone
            val localDateTime = instant.atZone(deviceTimeZone).toLocalDateTime()

            // Convert the local date time to Date object
            Date.from(localDateTime.atZone(deviceTimeZone).toInstant())
        } else {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            val date = formatter.parse(dateString)!!
            date
        }
    }

    fun getTimeDiff(date1: Date, date2: Date): Int {
        val diff = date1.time - date2.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getRiwayatStatus(status: String, tanggalDL: String?): Pair<Int, String> {
        return when(status) {
            "pending-1", "pending-2", "pending-3" -> {
                val dateDL = parseDate(tanggalDL!!)
                if (dateDL.before(Date())) {
                    Pair(R.color.red_500, "Kedaluwarsa")
                } else {
                    Pair(R.color.teal_500, "Belum Dibayar")
                }
            }
            "expired" -> Pair(R.color.red_500, "Kedaluwarsa")
//            "dp" -> Pair(R.color.teal_500, "DP")
            "lunas" -> Pair(R.color.teal_500, "Lunas")
            "batal" -> Pair(R.color.red_500, "Batal")
            "checkin" -> Pair(R.color.primary, "Check In")
            "selesai" -> Pair(R.color.green_500, "Selesai")
            "test" -> Pair(R.color.yellow_500, "Test")
            else -> Pair(R.color.red_500, "Tidak diketahui")
        }
    }

    fun isReservasiCancelable(reservasi: Reservasi): CancelableStatus {
        val tanggalCheckIn = parseDate(reservasi.arrivalDate)
        val tanggalDlBooking = if (reservasi.tanggalDlBooking != null) parseDate(reservasi.tanggalDlBooking) else null

        // if > 7 days, show llYesRefund
        val diffDays = getTimeDiff(tanggalCheckIn, getCurrentDateAsDate())
        Log.e("isReservasiCancelable", "reservasiiD: ${reservasi.id} - diffDays: $diffDays")
        val isOverCheckOut = getCurrentDateAsDate() > parseDate(reservasi.departureDate).apply {
            val calendar = Calendar.getInstance()
            calendar.time = this
            calendar.set(Calendar.HOUR_OF_DAY, 12)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            this.time = calendar.time.time
        }
        val isOverDl = if (tanggalDlBooking != null) getCurrentDateAsDate() > tanggalDlBooking else false
        Log.e("isReservasiCancelable", "reservasiiD: ${reservasi.id} - getCurrentDateAsDate: ${getCurrentDateAsDate()} - tanggalDlBooking: $tanggalDlBooking")
        val isReservasiUncancelable = reservasi.status == "checkin" || reservasi.status == "batal" || reservasi.status == "expired"
        if (reservasi.status.startsWith("pending-") && !isOverDl) {
            return CancelableStatus.NO_CONSEQUENCE
        } else if (diffDays > 7) {
            return CancelableStatus.YES_REFUND
        } else if (isOverCheckOut || isReservasiUncancelable) {
            return CancelableStatus.NOT_CANCELABLE
        } else {
            return CancelableStatus.NO_REFUND
        }
    }

    enum class CancelableStatus {
        YES_REFUND,
        NO_REFUND,
        NO_CONSEQUENCE,
        NOT_CANCELABLE
    }
}
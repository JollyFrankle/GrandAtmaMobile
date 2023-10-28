package com.example.grandatmahotel.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.grandatmahotel.data.remote.model.UserCustomer
import com.example.grandatmahotel.data.remote.model.UserPegawai
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val TOKEN_KEY = stringPreferencesKey(token_key)
    private val USER_CUSTOMER_KEY = stringPreferencesKey(user_c_key)
    private val USER_PEGAWAI_KEY = stringPreferencesKey(user_p_key)

    /**
     * Info:
     * To save a parcelable object, use Gson to convert it to a string and save it as a string.
     * There is currently no way to save a parcelable object directly.
     */

    fun getToken(): Flow<String> {
        return dataStore.data.map { p ->
            p[TOKEN_KEY] ?: ""
        }
    }

    suspend fun setToken(value: String) {
        dataStore.edit { p ->
            p[TOKEN_KEY] = value
        }
    }

    fun getUserCustomer(): Flow<UserCustomer?> {
        return dataStore.data.map { p ->
            Gson().fromJson(p[USER_CUSTOMER_KEY] ?: "null", UserCustomer::class.java)
        }
    }

    suspend fun setUserCustomer(value: UserCustomer?) {
        dataStore.edit { p ->
            p[USER_CUSTOMER_KEY] = Gson().toJson(value)
        }
    }

    fun getUserPegawai(): Flow<UserPegawai?> {
        return dataStore.data.map { p ->
            Gson().fromJson(p[USER_PEGAWAI_KEY] ?: "null", UserPegawai::class.java)
        }
    }

    suspend fun setUserPegawai(value: UserPegawai?) {
        dataStore.edit { p ->
            p[USER_PEGAWAI_KEY] = Gson().toJson(value)
        }
    }

    companion object {
        const val token_key = "token"
        const val user_c_key = "user"
        const val user_p_key = "user_data"
        private var INSTANCE: AppPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AppPreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppPreferences(dataStore)
            }
    }
}
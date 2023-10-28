package com.example.grandatmahotel.data.remote.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: T
)

data class ApiErrorResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("errors")
    val errors: HashMap<String, String>? // eg: "email": ["The email has already been taken."]
) {
    override fun toString(): String {
        var errors = ""
        this.errors?.forEach { (key, value) ->
            errors += "$key: ${value}\n"
        }
        return errors
    }
}

// RESPONSES
data class LoginInnerResponse (
    @SerializedName("user")
    val user: Any, // override UserCustomer or UserPegawai
    @SerializedName("token")
    val token: String
)

data class UserCustomer(
    val id: Int,
    val type: String,
    val nama: String,
    val nama_institusi: String?,
    val no_identitas: String,
    val jenis_identitas: String,
    val no_telp: String,
    val email: String,
    val alamat: String,
    val verified_at: String?,
    val password_last_changed: String?,
    val created_at: String,
    val updated_at: String
)

data class UserPegawai (
    val id: Int,
    val role: String,
    val nama: String,
    val email: String,
    val alamat: String,
    val created_at: String,
    val updated_at: String
)
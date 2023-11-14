package com.example.grandatmahotel.data.remote.model

import android.util.Log
import com.google.gson.annotations.SerializedName
import org.json.JSONArray

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
    val errors: HashMap<String, String>? // eg: "email": "The email has already been taken."
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
data class LoginCustomerInnerResponse (
    @SerializedName("user")
    val user: UserCustomer, // override UserCustomer or UserPegawai
    @SerializedName("token")
    val token: String
)

data class LoginPegawaiInnerResponse (
    @SerializedName("user")
    val user: UserPegawai, // override UserCustomer or UserPegawai
    @SerializedName("token")
    val token: String
)

data class UserCustomer(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("type") val type: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("nama_institusi") val namaInstitusi: String? = null,
    @SerializedName("no_identitas") val noIdentitas: String,
    @SerializedName("jenis_identitas") val jenisIdentitas: String,
    @SerializedName("no_telp") val noTelp: String,
    @SerializedName("email") val email: String,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("verified_at") val verifiedAt: String? = null,
    @SerializedName("password_last_changed") val passwordLastChanged: String? = null,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("password") val password: String? = null,
    @SerializedName("old_password") val oldPassword: String? = null
)

data class UserPegawai (
    @SerializedName("id") val id: Int,
    @SerializedName("role") val role: String,
    @SerializedName("nama") val nama: String,
    @SerializedName("email") val email: String,
    @SerializedName("alamat") val alamat: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class Reservasi(
    @SerializedName("id") val id: Int,
    @SerializedName("id_customer") val idCustomer: Int,
    @SerializedName("id_sm") val idSm: Int?,
    @SerializedName("id_booking") val idBooking: String?,
    @SerializedName("arrival_date") val arrivalDate: String,
    @SerializedName("departure_date") val departureDate: String,
    @SerializedName("checked_in") val checkedIn: String?,
    @SerializedName("checked_out") val checkedOut: String?,
    @SerializedName("jumlah_malam") val jumlahMalam: Int,
    @SerializedName("jumlah_dewasa") val jumlahDewasa: Int,
    @SerializedName("jumlah_anak") val jumlahAnak: Int,
    @SerializedName("tanggal_dl_booking") val tanggalDlBooking: String?,
    @SerializedName("tanggal_dp") val tanggalDp: String?,
    @SerializedName("jumlah_dp") val jumlahDp: Int?,
    @SerializedName("status") val status: String,
    @SerializedName("total") val total: Int,
    @SerializedName("deposit") val deposit: Int?,
    @SerializedName("permintaan_tambahan") val permintaanTambahan: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("bukti_transfer") val buktiTransfer: String?,
    @SerializedName("reservasi_layanan") val reservasiLayanan: List<ReservasiLayanan>?,
    @SerializedName("reservasi_rooms") val reservasiRooms: List<ReservasiRoom>?,
    @SerializedName("invoice") val invoice: Invoice?,
    @SerializedName("user_customer") val userCustomer: UserCustomer?,
    @SerializedName("user_pegawai") val userPegawai: UserPegawai?
)

data class ReservasiLayanan(
    @SerializedName("id") val id: Int,
    @SerializedName("id_reservasi") val idReservasi: Int,
    @SerializedName("id_layanan") val idLayanan: Int,
    @SerializedName("tanggal_pakai") val tanggalPakai: String,
    @SerializedName("qty") val qty: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("layanan_tambahan") val layananTambahan: FasilitasLayananTambahan?
)

data class ReservasiRoom(
    @SerializedName("id") val id: Int,
    @SerializedName("id_reservasi") val idReservasi: Int,
    @SerializedName("no_kamar") val noKamar: String?,
    @SerializedName("id_jenis_kamar") val idJenisKamar: Int,
    @SerializedName("harga_per_malam") val hargaPerMalam: Int,
    @SerializedName("jenis_kamar") val jenisKamar: JenisKamar?
)

data class Invoice(
    @SerializedName("id_reservasi") val idReservasi: Int,
    @SerializedName("id_fo") val idFo: Int,
    @SerializedName("no_invoice") val noInvoice: String,
    @SerializedName("tanggal_lunas") val tanggalLunas: String,
    @SerializedName("total_kamar") val totalKamar: Int,
    @SerializedName("total_layanan") val totalLayanan: Int,
    @SerializedName("pajak_layanan") val pajakLayanan: Int,
    @SerializedName("grand_total") val grandTotal: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("reservasi") val reservasi: Reservasi?
)

data class Kamar(
    @SerializedName("no_kamar") val noKamar: String,
    @SerializedName("id_jenis_kamar") val idJenisKamar: Int,
    @SerializedName("jenis_bed") val jenisBed: String,
    @SerializedName("no_lantai") val noLantai: Int,
    @SerializedName("is_smoking") val isSmoking: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("jenis_kamar") val jenisKamar: JenisKamar?
)

data class JenisKamar(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("gambar") val gambar: String,
    @SerializedName("short_desc") val shortDesc: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("fasilitas_unggulan") val fasilitasUnggulan: Any,
    @SerializedName("fasilitas") val fasilitas: String, // ternyata auto convert ke JSONObject
    @SerializedName("rincian") val rincian: String, // ternyata auto convert ke JSONArray
    @SerializedName("ukuran") val ukuran: Float,
    @SerializedName("tipe_bed") val tipeBed: String, // ternyata auto convert ke JSONArray
    @SerializedName("kapasitas") val kapasitas: Int,
    @SerializedName("harga_dasar") val hargaDasar: Int
) {
    fun tipeBedAsReadable(): String {
        val tipeBed = JSONArray(tipeBed)
        val list = mutableListOf<String>()
        for (i in 0 until tipeBed.length()) {
            list.add(tipeBed.getString(i))
        }

        return list.joinToString(" atau ")
    }

    fun rincianAsList(): List<String> {
        Log.e("AAA ", this.toString())
        val rincian = JSONArray(rincian)
        val list = mutableListOf<String>()
        for (i in 0 until rincian.length()) {
            list.add(rincian.getString(i))
        }

        return list
    }
}

data class FasilitasLayananTambahan(
    @SerializedName("id") val id: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("gambar") val gambar: String?,
    @SerializedName("short_desc") val shortDesc: String,
    @SerializedName("satuan") val satuan: String,
    @SerializedName("tarif") val tarif: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class RincianTarif (
    @SerializedName("jumlah_kamar") val jumlahKamar: Int,
    @SerializedName("harga_diskon") val hargaDiskon: Int,
    @SerializedName("harga") val harga: Int,
    @SerializedName("catatan") val catatan: List<CatatanRincianTarif>
)

data class CatatanRincianTarif (
    @SerializedName("type") val type: String,
    @SerializedName("message") val message: String
)

data class TarifKamar (
    @SerializedName("jenis_kamar") val jenisKamar: JenisKamar,
    @SerializedName("rincian_tarif") val rincianTarif: RincianTarif
)

data class DeadlineAndStage(
    @SerializedName("deadline") val deadline: String,
    @SerializedName("stage") val stage: Int
)

data class BookingResponse(
    @SerializedName("reservasi") val reservasi: Reservasi,
    @SerializedName("kamar") val kamar: List<ReservasiRoom>,
)

data class ReservasiS1Response (
    @SerializedName("reservation") val reservasi: Reservasi,
    @SerializedName("layanan_tambahan") val layananTambahan: List<ReservasiLayanan>
)
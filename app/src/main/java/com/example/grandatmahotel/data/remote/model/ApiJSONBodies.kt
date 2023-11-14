package com.example.grandatmahotel.data.remote.model

data class ReservasiInput (
    val jenis_kamar: List<JenisKamarReservasiInput>,
    val detail: DetailReservasiInput
)

data class JenisKamarReservasiInput (
    val id_jk: Int,
    val jumlah: Int,
    val harga: Int
)

data class DetailReservasiInput (
    val arrival_date: String,
    val departure_date: String,
    val jumlah_dewasa: Int,
    val jumlah_anak: Int
)

data class BookingS1Input (
    val layanan_tambahan: List<LayananTambahanInput>,
    val permintaan_khusus: PermintaanKhususInput
)

data class LayananTambahanInput (
    val id: Int,
    val amount: Int
)

data class PermintaanKhususInput (
    val expected_check_in: String = "",
    val expected_check_out: String = "",
    val permintaan_tambahan_lain: String
)
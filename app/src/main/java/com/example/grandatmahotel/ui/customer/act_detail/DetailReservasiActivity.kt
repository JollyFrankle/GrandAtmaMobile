package com.example.grandatmahotel.ui.customer.act_detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.databinding.ActivityDetailReservasiBinding
import com.example.grandatmahotel.utils.CustomTableRows
import com.example.grandatmahotel.utils.Utils
import com.example.grandatmahotel.utils.ViewModelFactory

class DetailReservasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailReservasiBinding
    private lateinit var viewModel: DetailReservasiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReservasiBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[DetailReservasiViewModel::class.java]
        setContentView(binding.root)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val id = intent.getIntExtra(EXTRA_ID, 0)
        viewModel.getDetailReservasi(id)

        setupViewModelBinding()

        binding.root.setOnRefreshListener {
            viewModel.getDetailReservasi(id)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupViewModelBinding() {
        viewModel.data.observe(this) {
            when (it) {
                is Result.Loading -> {
                    binding.root.isRefreshing = true
                }

                is Result.Success -> {
                    binding.root.isRefreshing = false
                    showDetailReservasi(it.data)
                }

                is Result.Error -> {
                    // Show error message
                    binding.root.isRefreshing = false
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDetailReservasi(reservasi: Reservasi) {
        // APP BAR
        supportActionBar?.apply {
            title = "Detail Reservasi ${reservasi.idBooking}"
        }

        // TOP CARDS
        val status = Utils.getRiwayatStatus(reservasi.status)
        binding.tvIdBooking.text = reservasi.idBooking ?: "(Belum dibuatkan)"
        binding.chipStatus.setChipBackgroundColorResource(status.first)
        binding.chipStatus.text = status.second
        binding.chipStatus.setTextColor(getColor(R.color.white))

        // PEMESAN
        binding.tvNamaPemesan.text = reservasi.userCustomer?.nama
        binding.tvEmailPemesan.text = reservasi.userCustomer?.email
        binding.tvNoTelpPemesan.text = reservasi.userCustomer?.noTelp

        // RESERVASI
        binding.tvTanggalMenginap.text = Utils.formatDate(Utils.parseDate(reservasi.arrivalDate), Utils.DF_DATE_READABLE)
        binding.tvJumlahTamu.text = getString(R.string.rvir_jumlah_tamu_format, reservasi.jumlahDewasa, reservasi.jumlahAnak, reservasi.jumlahMalam)
        binding.tvTanggalDP.text = if(reservasi.tanggalDp != null) Utils.formatDate(Utils.parseDate(reservasi.tanggalDp), Utils.DF_DATE_READABLE) else "-"

        // KAMAR
        CustomTableRows(
            context = this,
            data = reservasi.reservasiRooms!!,
            tableLayout = binding.tableKamar,
            rowData = { cell, index ->
                listOf(
                    (index+1).toString(),
                    cell.jenisKamar!!.nama,
                    cell.noKamar ?: "-",
                    cell.jenisKamar.kapasitas.toString(),
                    cell.hargaPerMalam.toString()
                )
            }
        ).render()

        // LAYANAN BERBAYAR
        CustomTableRows(
            context = this,
            data = reservasi.reservasiLayanan!!,
            tableLayout = binding.tableLayananBerbayar,
            rowData = { cell, index ->
                listOf(
                    (index+1).toString(),
                    cell.layananTambahan!!.nama,
                    Utils.formatDate(Utils.parseDate(cell.tanggalPakai), Utils.DF_DATE_READABLE),
                    getString(R.string.format_currency, cell.total)
                )
            }
        ).render()

        // INVOICE
        if (reservasi.invoice != null) {
            binding.llInvoice.visibility = View.VISIBLE

            val tglCheckIn = Utils.parseDate(reservasi.arrivalDate)
            binding.tvTanggalCheckIn.text = Utils.formatDate(tglCheckIn, Utils.DF_DATE_READABLE)
            binding.tvTanggalCheckOut.text = Utils.formatDate(Utils.getTanggalCheckOut(tglCheckIn, reservasi.jumlahMalam), Utils.DF_DATE_READABLE)
            binding.tvTotalHargaKamar.text = getString(R.string.format_currency, reservasi.invoice.totalKamar)
            binding.tvTotalHargaLayanan.text = getString(R.string.format_currency, reservasi.invoice.totalLayanan)
            binding.tvPajakLayanan.text = getString(R.string.format_currency, reservasi.invoice.pajakLayanan)
            binding.tvGrandTotal.text = getString(R.string.format_currency, reservasi.invoice.grandTotal)
        } else {
            binding.llInvoice.visibility = View.GONE
        }
    }

    companion object {
        const val EXTRA_ID = "id"
    }
}
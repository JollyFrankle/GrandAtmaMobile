package com.example.grandatmahotel.ui.customer.act_room_search

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.DetailReservasiInput
import com.example.grandatmahotel.data.remote.model.JenisKamarReservasiInput
import com.example.grandatmahotel.data.remote.model.ReservasiInput
import com.example.grandatmahotel.databinding.ActivityRoomSearchBinding
import com.example.grandatmahotel.databinding.DialogSearchBinding
import com.example.grandatmahotel.databinding.DialogSearchConfirmBinding
import com.example.grandatmahotel.ui.customer.act_booking.BookingActivity
import com.example.grandatmahotel.utils.Utils
import com.example.grandatmahotel.utils.ViewModelFactory
import com.example.grandatmahotel.utils.rv.RoomSearchRVAdapter
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date

class RoomSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoomSearchBinding
    private lateinit var viewModel: RoomSearchViewModel

    private lateinit var rvAdapter: RoomSearchRVAdapter

    private lateinit var dialogSearchBinding: DialogSearchBinding
    private lateinit var dialogSearch: AlertDialog
    private lateinit var dialogConfirmBinding: DialogSearchConfirmBinding
    private lateinit var dialogConfirm: AlertDialog
    private lateinit var dialogLoading: AlertDialog

    private var listKD = mutableListOf<RoomSearchRVAdapter.KamarDipesan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoomSearchBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[RoomSearchViewModel::class.java]
        dialogLoading = Utils.generateLoader(this)
        setContentView(binding.root)

        binding.srlRoomSearch.setOnRefreshListener {
            viewModel.refreshList()
        }

        binding.btnPesan.setOnClickListener {
            setDialogConfirmContent()
            dialogConfirm.show()
        }

        setupRV()
        setupViewModelBinding()
        setupDialogSearch()
        setupDialogConfirm()
    }

    private fun setupViewModelBinding() {
        viewModel.list.observe(this) {
            when (it) {
                is Result.Loading -> {
                    binding.srlRoomSearch.isRefreshing = true
                }

                is Result.Success -> {
                    rvAdapter.setList(it.data)
                    rvAdapter.setMaxKamar(viewModel.jumlahKamar)
                    dialogSearch.dismiss()
                    binding.srlRoomSearch.isRefreshing = false

                    // Set detail
                    binding.tvDetailTglPenc.text = "${Utils.formatDate(Date(viewModel.tglCheckIn), Utils.DF_DATE_SHORT)} - ${Utils.formatDate(Date(viewModel.tglCheckOut), Utils.DF_DATE_SHORT)}"
                    binding.tvDetailPencLain.text = "${viewModel.jumlahDewasa} Dewasa, ${viewModel.jumlahAnak} Anak – ${viewModel.jumlahKamar} Kamar"
                }

                is Result.Error -> {
                    // Show error message
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                    binding.srlRoomSearch.isRefreshing = false
                }
            }
        }

        viewModel.message.observe(this) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.reservasi.observe(this) {
            if (it != null) {
                val intent = Intent(this, BookingActivity::class.java)
                intent.putExtra(BookingActivity.EXTRA_ID, it.id.toLong())
                startActivity(intent)
                finish()
            }
        }

        viewModel.isLoading.observe(this) {
            if (it) {
                dialogLoading.show()
            } else {
                dialogLoading.dismiss()
            }
        }
    }

    private fun setupRV() {
        rvAdapter = RoomSearchRVAdapter(object : RoomSearchRVAdapter.OnItemCallback {
            override fun onKamarChanged(jumlahKamar: Int, listKD: List<RoomSearchRVAdapter.KamarDipesan>) {
                this@RoomSearchActivity.listKD = listKD.toMutableList()
                updatePriceSummary(jumlahKamar)
            }
        })

        binding.rvRoomSearch.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@RoomSearchActivity)
        }
    }

    private fun setupDialogSearch() {
        dialogSearchBinding = DialogSearchBinding.inflate(layoutInflater)
        dialogSearch = AlertDialog.Builder(this)
            .setView(dialogSearchBinding.root)
            .create()

        viewModel.tglCheckIn = MaterialDatePicker.todayInUtcMilliseconds()
        viewModel.tglCheckOut = MaterialDatePicker.todayInUtcMilliseconds() + 86400000
        viewModel.refreshList()
        rvAdapter.setMaxKamar(viewModel.jumlahKamar)

        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Tanggal Check In - Check Out")
            .setSelection(
                androidx.core.util.Pair(viewModel.tglCheckIn, viewModel.tglCheckOut)
            )
            // set min date to today
            .setCalendarConstraints(
                CalendarConstraints.Builder()
                    .setStart(MaterialDatePicker.todayInUtcMilliseconds())
                    .setEnd(MaterialDatePicker.todayInUtcMilliseconds() + 31536000000)
                    .build()
            )
            .build()

        dialogSearchBinding.apply {
            actvTanggal.setOnClickListener {
                tilTanggal.isEnabled = false
                datePicker.show(supportFragmentManager, "DATE_PICKER")
            }

            tilTanggal.setEndIconOnClickListener {
                tilTanggal.isEnabled = false
                datePicker.show(supportFragmentManager, "DATE_PICKER")
            }

            actvTanggal.setText("${Utils.formatDate(Date(viewModel.tglCheckIn), Utils.DF_DATE_SHORT)} - ${Utils.formatDate(Date(viewModel.tglCheckOut), Utils.DF_DATE_SHORT)}")
            tilJumlahDewasa.editText?.setText(viewModel.jumlahDewasa.toString())
            tilJumlahAnak.editText?.setText(viewModel.jumlahAnak.toString())
            tilJumlahKamar.editText?.setText(viewModel.jumlahKamar.toString())

            btnCariKamar.setOnClickListener {
                val tglCheckIn = viewModel.tglCheckIn
                val tglCheckOut = viewModel.tglCheckOut

                val jumlahDewasa = try {
                    tilJumlahDewasa.editText?.text.toString().toInt()
                } catch (e: Exception) {
                    0
                }

                val jumlahAnak = try {
                    tilJumlahAnak.editText?.text.toString().toInt()
                } catch (e: Exception) {
                    0
                }

                val jumlahKamar = try {
                    tilJumlahKamar.editText?.text.toString().toInt()
                } catch (e: Exception) {
                    0
                }

                if (jumlahDewasa == 0) {
                    tilJumlahDewasa.error = "Jumlah dewasa tidak boleh kosong."
                    return@setOnClickListener
                } else if (jumlahKamar == 0) {
                    tilJumlahKamar.error = "Jumlah kamar tidak boleh kosong."
                    return@setOnClickListener
                } else if (jumlahDewasa > 10) {
                    tilJumlahDewasa.error = "Jumlah dewasa tidak boleh lebih dari 10."
                    return@setOnClickListener
                } else if (jumlahKamar > jumlahDewasa) {
                    tilJumlahKamar.error = "Jumlah kamar tidak boleh lebih dari jumlah dewasa."
                    return@setOnClickListener
                } else if (jumlahKamar > 5) {
                    tilJumlahKamar.error = "Jumlah kamar tidak boleh lebih dari 5."
                    return@setOnClickListener
                } else if (tglCheckIn >= tglCheckOut) {
                    tilTanggal.error = "Tanggal check in tidak boleh lebih dari tanggal check out."
                    return@setOnClickListener
                } else if (tglCheckIn < MaterialDatePicker.todayInUtcMilliseconds()) {
                    tilTanggal.error = "Tanggal check in tidak boleh kurang dari hari ini."
                    return@setOnClickListener
                } else if (tglCheckOut > MaterialDatePicker.todayInUtcMilliseconds() + 31536000000) {
                    tilTanggal.error = "Tanggal check out tidak boleh lebih dari 1 tahun dari hari ini."
                    return@setOnClickListener
                } else if (tglCheckOut - tglCheckIn > 7 * 24 * 60 * 60 * 1000) {
                    tilTanggal.error = "Jumlah malam tidak boleh lebih dari 7 malam."
                    return@setOnClickListener
                } else {
                    tilJumlahDewasa.apply {
                        editText?.setText(jumlahDewasa.toString())
                        error = null
                    }
                    tilJumlahAnak.apply {
                        editText?.setText(jumlahAnak.toString())
                        error = null
                    }
                    tilJumlahKamar.apply {
                        editText?.setText(jumlahKamar.toString())
                        error = null
                    }
                    tilTanggal.error = null
                }

                viewModel.getList(
                    tglCheckIn,
                    tglCheckOut,
                    jumlahDewasa,
                    jumlahAnak,
                    jumlahKamar
                )

                dialogSearch.dismiss()
            }
        }

        binding.fabOpenDialogSearch.setOnClickListener {
            dialogSearch.show()
        }

        datePicker.addOnPositiveButtonClickListener {
            val startDate = Date(it.first ?: 0)
            val endDate = Date(it.second ?: 0)
            val startDateString = Utils.formatDate(startDate, Utils.DF_DATE_SHORT)
            val endDateString = Utils.formatDate(endDate, Utils.DF_DATE_SHORT)
            dialogSearchBinding.actvTanggal.setText("$startDateString - $endDateString")
            viewModel.tglCheckIn = it.first ?: 0
            viewModel.tglCheckOut = it.second ?: 0
        }

        datePicker.addOnDismissListener {
            dialogSearchBinding.tilTanggal.isEnabled = true
        }
    }

    private fun setDialogConfirmContent() {
        val binding = dialogConfirmBinding

        // RINCIAN RESERVASI
        val tglCheckIn = Utils.formatDate(Date(viewModel.tglCheckIn), Utils.DF_DATE_READABLE)
        val tglCheckOut = Utils.formatDate(Date(viewModel.tglCheckOut), Utils.DF_DATE_READABLE)
        val jumlahTamu = "${viewModel.jumlahDewasa} Dewasa, ${viewModel.jumlahAnak} Anak"

        binding.tvCheckIn.text = tglCheckIn
        binding.tvCheckOut.text = "${tglCheckOut} (${Utils.getDateDiff(Date(viewModel.tglCheckOut), Date(viewModel.tglCheckIn))} malam)"
        binding.tvJumlahTamu.text = jumlahTamu

        // KAMAR YANG AKAN DIPESAN
        var txtKamarDipesan = ""
        listKD.forEach {
            if (it.count > 0) {
                txtKamarDipesan += "${it.count} ${it.tarifKamar.jenisKamar.nama}\n"
                txtKamarDipesan += "${getString(R.string.format_currency, it.tarifKamar.rincianTarif.hargaDiskon)}/kamar/malam\n"
                txtKamarDipesan += "${getString(R.string.format_currency, it.tarifKamar.rincianTarif.hargaDiskon * it.count)}/malam\n\n"
            }
        }

        binding.tvKamarDipesan.text = txtKamarDipesan.trim()

        val totalPerMalam = listKD.sumOf {
            it.tarifKamar.rincianTarif.hargaDiskon * it.count
        }
        binding.tvTotalPerMalam.text = getString(R.string.format_currency, totalPerMalam)
    }

    private fun setupDialogConfirm() {
        dialogConfirmBinding = DialogSearchConfirmBinding.inflate(layoutInflater)
        dialogConfirm = AlertDialog.Builder(this)
            .setView(dialogConfirmBinding.root)
            .create()

        dialogConfirmBinding.apply {
            btnBatal.setOnClickListener {
                dialogConfirm.dismiss()
            }

            btnKonfirmasi.setOnClickListener {
                dialogConfirm.dismiss()

                val jenisKamarGrouped = listKD.filter { it.count > 0 }.map {
                    JenisKamarReservasiInput(
                        id_jk = it.tarifKamar.jenisKamar.id,
                        jumlah = it.count,
                        harga = it.tarifKamar.rincianTarif.hargaDiskon
                    )
                }

                val detail = DetailReservasiInput(
                    arrival_date = Utils.formatDate(Date(viewModel.tglCheckIn), Utils.DF_YMD),
                    departure_date = Utils.formatDate(Date(viewModel.tglCheckOut), Utils.DF_YMD),
                    jumlah_dewasa = viewModel.jumlahDewasa,
                    jumlah_anak = viewModel.jumlahAnak
                )

                val reservasiInput = ReservasiInput(
                    jenis_kamar = jenisKamarGrouped,
                    detail = detail
                )

                viewModel.booking(reservasiInput)
            }
        }
    }

    private fun updatePriceSummary(jumlahKamarDipesan: Int) {
        if (jumlahKamarDipesan > 0) {
            showPriceSummary(true)

            val totalHargaNormal = listKD.sumOf {
                it.tarifKamar.rincianTarif.harga * it.count
            }
            val totalHargaDiskon = listKD.sumOf {
                it.tarifKamar.rincianTarif.hargaDiskon * it.count
            }

            binding.tvHargaNormal.apply {
                if (totalHargaDiskon == totalHargaNormal) {
                    visibility = View.GONE
                } else {
                    visibility = View.VISIBLE
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = context.getString((R.string.format_currency), totalHargaNormal)
                }
            }

            binding.tvHargaDiskon.apply {
                if (totalHargaDiskon == totalHargaNormal) {
                    setTextColor(context.getColor(R.color.black))
                } else {
                    setTextColor(context.getColor(R.color.green_500))
                }
                text = context.getString((R.string.format_currency), totalHargaDiskon)
            }

            // Set button enabled/disabled
            if (jumlahKamarDipesan < viewModel.jumlahKamar) {
                binding.btnPesan.apply {
                    isEnabled = false
                    alpha = 0.5f
                }
                binding.tvPesanWarning.apply {
                    visibility = View.VISIBLE
                    text = "${jumlahKamarDipesan}/${viewModel.jumlahKamar} kamar dipilih"
                }
            } else if (jumlahKamarDipesan > viewModel.jumlahKamar){
                binding.btnPesan.apply {
                    isEnabled = false
                    alpha = 0.5f
                }
                binding.tvPesanWarning.apply {
                    visibility = View.VISIBLE
                    text = "Kamar terlalu banyak"
                }
            } else {
                binding.btnPesan.apply {
                    isEnabled = true
                    alpha = 1f
                }
                binding.tvPesanWarning.visibility = View.GONE
            }
        } else {
            showPriceSummary(false)
        }
    }

    private fun showPriceSummary(state: Boolean) {
        if (state) {
            ObjectAnimator.ofFloat(binding.mcvPriceSummary, View.TRANSLATION_Y, 0f).apply {
                duration = 300
                start()
            }
        } else {
            ObjectAnimator.ofFloat(binding.mcvPriceSummary, View.TRANSLATION_Y, binding.mcvPriceSummary.height.toFloat()).apply {
                duration = 300
                start()
            }
        }
    }
}
package com.example.grandatmahotel.ui.customer.act_booking.step2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.data.remote.model.ReservasiRoom
import com.example.grandatmahotel.databinding.FragmentStep2BookingBinding
import com.example.grandatmahotel.ui.customer.act_booking.BookingActivity
import com.example.grandatmahotel.utils.ViewModelFactory


class Step2BookingFragment : Fragment() {

    private var _binding: FragmentStep2BookingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: Step2BookingViewModel

    private var id: Long = 0
    private lateinit var activity: BookingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStep2BookingBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[Step2BookingViewModel::class.java]
        activity = requireActivity() as BookingActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id = arguments?.getLong(BookingActivity.EXTRA_ID) ?: 0L
        viewModel.getReservasi(id)

        setupViewModelBinding()

        binding.btnLanjutkan.setOnClickListener {
            viewModel.submit(id)
        }
    }

    private fun setupViewModelBinding() {
        viewModel.reservasi.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    activity.dialogLoading.show()
                }

                is Result.Success -> {
                    val reservasi = it.data
                    setDataReservasi(reservasi)
                    activity.dialogLoading.dismiss()
                }

                is Result.Error -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                    Log.e("Step1BookingFragmentAAA", it.error)
                }
            }
        }

        viewModel.submitResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    activity.dialogLoading.show()
                }
                is Result.Success -> {
                    activity.toPage(2)
                }
                is Result.Error -> {
                    activity.dialogLoading.dismiss()
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setDataReservasi(reservasi: Reservasi) {
        // IDENTITAS
        if (reservasi.userCustomer != null) {
            binding.tvIdentitasNamaLengkap.text = reservasi.userCustomer.nama
            binding.tvIdentitasNomorIdentitas.text = "${reservasi.userCustomer.jenisIdentitas} - ${reservasi.userCustomer.noIdentitas}"
            binding.tvIdentitasEmail.text = reservasi.userCustomer.email
            binding.tvIdentitasNomorTelepon.text = reservasi.userCustomer.noTelp
            binding.tvIdentitasAlamat.text = reservasi.userCustomer.alamat
        }

        // LAYANAN TAMBAHAN
        if (reservasi.reservasiLayanan != null) {
            var layananTambahan = ""
            for (lay in reservasi.reservasiLayanan) {
                layananTambahan += "${lay.layananTambahan?.nama}\n"
                layananTambahan += "${lay.qty} ${lay.layananTambahan?.satuan} × ${getString(R.string.format_currency, lay.total / lay.qty)}\n"
                layananTambahan += "Total: ${getString(R.string.format_currency, lay.total)}\n\n"
            }
            if (layananTambahan.isBlank()) {
                layananTambahan = "Tidak ada layanan tambahan"
            }
            binding.tvLayananList.text = layananTambahan.trim()
        }

        // PERMINTAAN KHUSUS
        if (reservasi.permintaanTambahan != null) {
            binding.tvPermintaanKhusus.text = reservasi.permintaanTambahan
        } else {
            binding.tvPermintaanKhusus.text = "Tidak ada permintaan khusus"
        }

        // GROUP KAMAR UNTUK RESI
        val groupedKamar = mutableListOf<KamarGrouped>()
        for (kamar in reservasi.reservasiRooms ?: emptyList()) {
            val index = groupedKamar.indexOfFirst { it.kamar.id == kamar.id }
            if (index == -1) {
                groupedKamar.add(
                    KamarGrouped(kamar, 1)
                )
            } else {
                groupedKamar[index].jumlah++
            }
        }

        var txtKamarDipesan = ""
        groupedKamar.forEach {
            if (it.jumlah > 0) {
                txtKamarDipesan += "${it.jumlah} ${it.kamar.jenisKamar?.nama}\n"
                txtKamarDipesan += "${it.jumlah} kamar × ${reservasi.jumlahMalam} malam × ${getString(R.string.format_currency, it.kamar.hargaPerMalam)}\n"
                txtKamarDipesan += "${getString(R.string.format_currency, it.kamar.hargaPerMalam * it.jumlah * reservasi.jumlahMalam)}\n\n"
            }
        }
        binding.tvKamarDipesan.text = txtKamarDipesan.trim()

        binding.tvTotalHarga.text = getString(R.string.format_currency, reservasi.total)
    }

    data class KamarGrouped(
        val kamar: ReservasiRoom,
        var jumlah: Int
    )

}
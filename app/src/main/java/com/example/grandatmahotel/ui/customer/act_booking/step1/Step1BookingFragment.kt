package com.example.grandatmahotel.ui.customer.act_booking.step1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.BookingS1Input
import com.example.grandatmahotel.data.remote.model.LayananTambahanInput
import com.example.grandatmahotel.data.remote.model.PermintaanKhususInput
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.databinding.DialogStep1ConfirmBinding
import com.example.grandatmahotel.databinding.FragmentStep1BookingBinding
import com.example.grandatmahotel.ui.customer.act_booking.BookingActivity
import com.example.grandatmahotel.utils.ViewModelFactory
import com.example.grandatmahotel.utils.rv.FasilitasRVAdapter


class Step1BookingFragment : Fragment() {

    private var _binding: FragmentStep1BookingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: Step1BookingViewModel
    private var id: Long = 0

    private lateinit var rvAdapter: FasilitasRVAdapter

    private lateinit var dialogConfirmBinding: DialogStep1ConfirmBinding
    private lateinit var dialogConfirm: androidx.appcompat.app.AlertDialog

    private lateinit var activity: BookingActivity

    private var listFD = mutableListOf<FasilitasRVAdapter.FasilitasDipesan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStep1BookingBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[Step1BookingViewModel::class.java]
        activity = requireActivity() as BookingActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        id = arguments?.getLong(BookingActivity.EXTRA_ID) ?: 0L
        viewModel.getReservasi(id)

        setupViewModelBinding()
        setupDialogConfirm()

        binding.btnLanjutkan.setOnClickListener {
            setDialogConfirmContent()
            dialogConfirm.show()
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

        viewModel.fasilitas.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val fasilitas = it.data
                    setupRV()
                    rvAdapter.setList(fasilitas)
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.submitResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    activity.dialogLoading.show()
                }
                is Result.Success -> {
                    activity.toPage(1)
                }
                is Result.Error -> {
                    activity.dialogLoading.dismiss()
                    dialogConfirm.show() // show ulang dialog
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRV() {
        rvAdapter = FasilitasRVAdapter(object: FasilitasRVAdapter.OnItemCallback {
            override fun onAmountChanged(
                listFD: List<FasilitasRVAdapter.FasilitasDipesan>
            ) {
                this@Step1BookingFragment.listFD = listFD.toMutableList()
                Log.e("Step1BookingFragmentAAA", listFD.toString())
            }
        })

        val llm = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvLayananTambahan.apply {
            layoutManager = llm
            adapter = rvAdapter
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
    }

    private fun setupDialogConfirm() {
        dialogConfirmBinding = DialogStep1ConfirmBinding.inflate(layoutInflater)
        dialogConfirm = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogConfirmBinding.root)
            .create()

        dialogConfirmBinding.btnKonfirmasi.setOnClickListener {
            val listLT = listFD.map {
                LayananTambahanInput(
                    id = it.fasilitas.id,
                    amount = it.count
                )
            }
            val pki = PermintaanKhususInput(
                permintaan_tambahan_lain = binding.tilPermintaanKhusus.editText?.text.toString().trim()
            )
            val data = BookingS1Input(
                layanan_tambahan = listLT,
                permintaan_khusus = pki
            )
            viewModel.submit(id, data)

            dialogConfirm.dismiss()
        }

        dialogConfirmBinding.btnBatal.setOnClickListener {
            dialogConfirm.dismiss()
        }
    }

    private fun setDialogConfirmContent() {
        // LAYANAN TAMBAHAN
        var layananTambahan = ""
        for (item in listFD) {
            if (item.count > 0) {
                layananTambahan += "(${item.count}x) ${item.fasilitas.nama}\n"
            }
        }
        if (layananTambahan.isNotBlank()) {
            dialogConfirmBinding.tvLayananTambahan.text = layananTambahan.trim()
        } else {
            dialogConfirmBinding.tvLayananTambahan.text = "Tidak ada yang dipesan"
        }

        // PERMINTAAN TAMBAHAN
        val permintaanTambahan = binding.tilPermintaanKhusus.editText?.text.toString()
        if (permintaanTambahan.isNotBlank()) {
            dialogConfirmBinding.tvPermintaanKhusus.text = permintaanTambahan
        } else {
            dialogConfirmBinding.tvPermintaanKhusus.text = "Tidak ada permintaan khusus"
        }
    }
}
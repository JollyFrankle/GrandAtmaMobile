package com.example.grandatmahotel.ui.customer.act_booking.step3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.databinding.FragmentStep3BookingBinding
import com.example.grandatmahotel.ui.customer.act_booking.BookingActivity
import com.example.grandatmahotel.ui.customer.act_detail.DetailReservasiActivity
import com.example.grandatmahotel.utils.ViewModelFactory


class Step3BookingFragment : Fragment() {

    private var _binding: FragmentStep3BookingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: Step3BookingViewModel

    private var id: Long = 0
    private lateinit var activity: BookingActivity

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.fileGambar.value = it
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentStep3BookingBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[Step3BookingViewModel::class.java]
        activity = requireActivity() as BookingActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelBinding()

        id = arguments?.getLong(BookingActivity.EXTRA_ID) ?: 0L
        viewModel.getReservasi(id)

        binding.btnLanjutkan.setOnClickListener {
            viewModel.submit(id)
        }

        binding.btnPilihGambar.setOnClickListener {
            launcherGallery.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
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
                    val intent = Intent(requireContext(), DetailReservasiActivity::class.java)
                    intent.putExtra(DetailReservasiActivity.EXTRA_ID, id.toInt())
                    intent.putExtra(DetailReservasiActivity.EXTRA_SHOW_BTN_DASHBOARD, true)
                    startActivity(intent)
                    Toast.makeText(requireContext(), "Reservasi berhasil dibuat dan dikonfirmasi", Toast.LENGTH_SHORT).show()
                    activity.finish()
                }
                is Result.Error -> {
                    activity.dialogLoading.dismiss()
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.fileGambar.observe(viewLifecycleOwner) {
            binding.imgBuktiPembayaran.setImageURI(it)
            if (it != null) {
                binding.btnLanjutkan.apply {
                    isEnabled = true
                    alpha = 1f
                }
            } else {
                binding.btnLanjutkan.apply {
                    isEnabled = false
                    alpha = 0.5f
                }
            }
        }
    }

    private fun setDataReservasi(reservasi: Reservasi) {
        binding.tvTotalHarga.text = getString(R.string.format_currency, reservasi.total)
    }

}
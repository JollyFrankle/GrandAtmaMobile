package com.example.grandatmahotel.ui.customer.riwayat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.databinding.DialogDeleteConfirmBinding
import com.example.grandatmahotel.databinding.FragmentCustomerRiwayatBinding
import com.example.grandatmahotel.ui.customer.act_detail.DetailReservasiActivity
import com.example.grandatmahotel.utils.Utils
import com.example.grandatmahotel.utils.ViewModelFactory
import com.example.grandatmahotel.utils.rv.HistoryReservasiRVAdapter
import com.google.android.material.tabs.TabLayout

class CustomerRiwayatFragment : Fragment() {

    private var _binding: FragmentCustomerRiwayatBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CustomerRiwayatViewModel

    private lateinit var rvAdapter: HistoryReservasiRVAdapter

    private lateinit var dialogDeleteBinding: DialogDeleteConfirmBinding
    private lateinit var dialogDelete: AlertDialog

    private var idToCancel = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCustomerRiwayatBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[CustomerRiwayatViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRV()
        setupViewModelBinding()
        setupDialogDelete()

        binding.srlRiwayat.setOnRefreshListener {
            viewModel.refreshReservasi()
        }

        binding.tlRiwayat.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.getReservasi(tab?.position ?: 0)
                binding.tilSearch.editText?.setText("")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        binding.tilSearch.editText?.addTextChangedListener { editable ->
            val query = editable.toString()
            rvAdapter.searchList(query)
            Log.e("CustomerRiwayatFragment", "onViewCreated: $query")
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshReservasi()

        // set selected tab from viewModel
        binding.tlRiwayat.getTabAt(viewModel.selectedTab)?.select()
    }

    private fun setupViewModelBinding() {
        viewModel.list.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.srlRiwayat.isRefreshing = true
                }

                is Result.Success -> {
                    binding.srlRiwayat.isRefreshing = false
                    rvAdapter.setList(it.data)
                }

                is Result.Error -> {
                    // Show error message
                    binding.srlRiwayat.isRefreshing = false
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.cancelResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.srlRiwayat.isRefreshing = true
                }

                is Result.Success -> {
                    // tetap saja refresh
                    viewModel.refreshReservasi()
                    Toast.makeText(requireContext(), "Reservasi berhasil dibatalkan", Toast.LENGTH_SHORT).show()
                }

                is Result.Error -> {
                    // Show error message
                    binding.srlRiwayat.isRefreshing = false
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRV() {
        rvAdapter = HistoryReservasiRVAdapter(object: HistoryReservasiRVAdapter.OnItemCallback {
            override fun onItemClicked(data: Reservasi) {
                val intent = Intent(requireContext(), DetailReservasiActivity::class.java)
                intent.putExtra(DetailReservasiActivity.EXTRA_ID, data.id)
                startActivity(intent)
            }

            override fun onItemCancelled(data: Reservasi) {
                setDialogDeteleData(data)
                dialogDelete.show()
            }
        })

        binding.rvJenisKamar.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupDialogDelete() {
        dialogDeleteBinding = DialogDeleteConfirmBinding.inflate(layoutInflater)
        dialogDelete = AlertDialog.Builder(requireContext())
            .setView(dialogDeleteBinding.root)
            .setPositiveButton("Batalkan Reservasi") { _, _ ->
                viewModel.cancelReservasi(idToCancel.toLong())
            }
            .setNegativeButton("Tidak", null)
            .create()
    }

    private fun setDialogDeteleData(reservasi: Reservasi) {
        if (reservasi.idBooking != null) {
            dialogDeleteBinding.tvBookingId.text = reservasi.idBooking
        } else {
            dialogDeleteBinding.tvBookingId.text = "(Belum dibuatkan)"
        }

        idToCancel = reservasi.id
        when (Utils.isReservasiCancelable(reservasi)) {
            Utils.CancelableStatus.NO_CONSEQUENCE -> {
                dialogDeleteBinding.mcvYesRefund.visibility = View.GONE
                dialogDeleteBinding.mcvNoRefund.visibility = View.GONE
                dialogDeleteBinding.mcvNoConsequence.visibility = View.VISIBLE
            }
            Utils.CancelableStatus.YES_REFUND -> {
                dialogDeleteBinding.mcvYesRefund.visibility = View.VISIBLE
                dialogDeleteBinding.mcvNoRefund.visibility = View.GONE
                dialogDeleteBinding.mcvNoConsequence.visibility = View.GONE
            }
            Utils.CancelableStatus.NO_REFUND -> {
                dialogDeleteBinding.mcvYesRefund.visibility = View.GONE
                dialogDeleteBinding.mcvNoRefund.visibility = View.VISIBLE
                dialogDeleteBinding.mcvNoConsequence.visibility = View.GONE
            }
            Utils.CancelableStatus.NOT_CANCELABLE -> {
                dialogDeleteBinding.mcvYesRefund.visibility = View.GONE
                dialogDeleteBinding.mcvNoRefund.visibility = View.GONE
                dialogDeleteBinding.mcvNoConsequence.visibility = View.GONE
                dialogDelete.hide()
            }
        }
    }
}
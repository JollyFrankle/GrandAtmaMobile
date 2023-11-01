package com.example.grandatmahotel.ui.customer.riwayat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.databinding.FragmentCustomerRiwayatBinding
import com.example.grandatmahotel.ui.customer.act_detail.DetailReservasiActivity
import com.example.grandatmahotel.utils.ViewModelFactory
import com.example.grandatmahotel.utils.rv.HistoryReservasiRVAdapter

class CustomerRiwayatFragment : Fragment() {

    private var _binding: FragmentCustomerRiwayatBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CustomerRiwayatViewModel

    private lateinit var rvAdapter: HistoryReservasiRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding= FragmentCustomerRiwayatBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[CustomerRiwayatViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRV()
        setupViewModelBinding()

        binding.root.setOnRefreshListener {
            viewModel.getReservasi()
        }
    }

    private fun setupViewModelBinding() {
        viewModel.list.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.root.isRefreshing = true
                }

                is Result.Success -> {
                    binding.root.isRefreshing = false
                    rvAdapter.setList(it.data)
                }

                is Result.Error -> {
                    // Show error message
                    binding.root.isRefreshing = false
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
        })

        binding.rvJenisKamar.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
            setHasFixedSize(true)
        }
    }
}
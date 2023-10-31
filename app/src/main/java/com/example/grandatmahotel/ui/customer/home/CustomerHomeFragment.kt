package com.example.grandatmahotel.ui.customer.home

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
import com.example.grandatmahotel.databinding.FragmentCustomerHomeBinding
import com.example.grandatmahotel.utils.ViewModelFactory
import com.example.grandatmahotel.utils.rv.HistoryReservasiRVAdapter

class CustomerHomeFragment : Fragment() {

    private var _binding: FragmentCustomerHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CustomerHomeViewModel

    private lateinit var rvAdapter: HistoryReservasiRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentCustomerHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[CustomerHomeViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRV()
        setupViewModelBinding()
    }

    private fun setupViewModelBinding() {
        viewModel.list.observe(this) {
            when (it) {
                is Result.Loading -> {
                }

                is Result.Success -> {
                    rvAdapter.setList(it.data)
                }

                is Result.Error -> {
                    // Show error message
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRV() {
        rvAdapter = HistoryReservasiRVAdapter(object: HistoryReservasiRVAdapter.OnItemCallback {
            override fun onItemClicked(data: Reservasi) {
                Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
            }
        })

        binding.rvJenisKamar.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }
    }
}
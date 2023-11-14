package com.example.grandatmahotel.ui.customer.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.model.UserCustomer
import com.example.grandatmahotel.databinding.FragmentCustomerHomeBinding
import com.example.grandatmahotel.ui.customer.CustomerDashboardActivity
import com.example.grandatmahotel.ui.customer.act_room_search.RoomSearchActivity
import com.example.grandatmahotel.utils.ViewModelFactory

class CustomerHomeFragment : Fragment() {

    private var _binding: FragmentCustomerHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CustomerHomeViewModel

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

        setupVieModelBinding()

        binding.llNavProfile.setOnClickListener {
            (activity as CustomerDashboardActivity).bottomNavigation.selectedItemId = R.id.profileFragment
        }

        binding.llNavHistoryReservasi.setOnClickListener {
            (activity as CustomerDashboardActivity).bottomNavigation.selectedItemId = R.id.customerRiwayatFragment
        }

        binding.btnReservasi.setOnClickListener {
            val intent = Intent(requireContext(), RoomSearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupVieModelBinding() {
        viewModel.customer.observe(viewLifecycleOwner) {
            if(it != null) {
                displayDataCustomer(it)
            }
        }
    }

    private fun displayDataCustomer(customer: UserCustomer) {
        binding.tvNamaUser.text = customer.nama
    }

}
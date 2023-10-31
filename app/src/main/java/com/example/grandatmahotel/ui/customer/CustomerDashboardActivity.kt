package com.example.grandatmahotel.ui.customer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.grandatmahotel.R
import com.example.grandatmahotel.databinding.ActivityCustomerDashboardBinding
import com.example.grandatmahotel.utils.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCustomerDashboardBinding
    private lateinit var viewModel: CustomerDashboardViewModel

    lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDashboardBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[CustomerDashboardViewModel::class.java]
        setContentView(binding.root)

        supportActionBar?.title = "Grand Atma Hotel"

        val navController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController)

        bottomNavigation = binding.navView
    }
}
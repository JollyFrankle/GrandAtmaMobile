package com.example.grandatmahotel.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.databinding.ActivityAdminDashboardBinding
import com.example.grandatmahotel.utils.ViewModelFactory

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var viewModel: AdminDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[AdminDashboardViewModel::class.java]
        setContentView(binding.root)
    }
}
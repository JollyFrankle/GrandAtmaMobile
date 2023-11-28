package com.example.grandatmahotel.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.databinding.ActivityAdminDashboardBinding
import com.example.grandatmahotel.ui.admin.laporan.LaporanActivity
import com.example.grandatmahotel.utils.ViewModelFactory

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var viewModel: AdminDashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[AdminDashboardViewModel::class.java]
        setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            finish()
        }

        binding.llLaporan1.setOnClickListener {
            val intent = Intent(this, LaporanActivity::class.java)
            intent.putExtra(LaporanActivity.EXTRA_NO_LAP, 1)
            startActivity(intent)
        }

        binding.llLaporan4.setOnClickListener {
            val intent = Intent(this, LaporanActivity::class.java)
            intent.putExtra(LaporanActivity.EXTRA_NO_LAP, 4)
            startActivity(intent)
        }

        setupViewModelBinding()
    }

    private fun setupViewModelBinding() {
        viewModel.message.observe(this) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.user.observe(this) {
            binding.tvNamaUser.text = it.nama
        }
    }
}
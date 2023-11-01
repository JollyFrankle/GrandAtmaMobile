package com.example.grandatmahotel.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.JenisKamar
import com.example.grandatmahotel.data.remote.service.ApiConfig
import com.example.grandatmahotel.databinding.ActivityHomeBinding
import com.example.grandatmahotel.ui.auth.MainActivity
import com.example.grandatmahotel.utils.ViewModelFactory
import com.example.grandatmahotel.utils.rv.JenisKamarRVAdapter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    private lateinit var rvAdapter: JenisKamarRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[HomeViewModel::class.java]
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        setupRV()
        setupViewModelBinding()

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        // Check if user is logged in
        viewModel.getUser()
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
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.userType.observe(this) {
            when (it) {
                'c' -> binding.btnLogin.text = "Dashboard"
                'p' -> binding.btnLogin.text = "Dashboard Admin"
                else -> binding.btnLogin.text = "Masuk Akun"
            }
        }
    }

    private fun setupRV() {
        rvAdapter = JenisKamarRVAdapter(object: JenisKamarRVAdapter.OnItemCallback {
            override fun onItemClicked(data: JenisKamar) {
                // open browser
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("${ApiConfig.WEB_URL}/kamar/${data.id}")
                startActivity(intent)
            }
        })

        val llm = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvJenisKamar.apply {
            layoutManager = llm
            adapter = rvAdapter
        }
    }
}
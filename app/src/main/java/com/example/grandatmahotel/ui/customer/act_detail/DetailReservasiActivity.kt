package com.example.grandatmahotel.ui.customer.act_detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.Reservasi
import com.example.grandatmahotel.databinding.ActivityDetailReservasiBinding
import com.example.grandatmahotel.utils.ViewModelFactory

class DetailReservasiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailReservasiBinding
    private lateinit var viewModel: DetailReservasiViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailReservasiBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[DetailReservasiViewModel::class.java]
        setContentView(binding.root)

        val id = intent.getIntExtra("id", 0)
        viewModel.getDetailReservasi(id)

        setupViewModelBinding()
    }

    private fun setupViewModelBinding() {
        viewModel.data.observe(this) {
            when (it) {
                is Result.Loading -> {
                }

                is Result.Success -> {
                    showDetailReservasi(it.data)
                }

                is Result.Error -> {
                    // Show error message
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDetailReservasi(reservasi: Reservasi) {

    }
}
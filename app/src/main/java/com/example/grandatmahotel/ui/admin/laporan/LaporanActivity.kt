package com.example.grandatmahotel.ui.admin.laporan

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.databinding.ActivityLaporanBinding
import com.example.grandatmahotel.utils.ViewModelFactory
import java.util.Calendar

class LaporanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaporanBinding
    private lateinit var viewModel: LaporanViewModel

    private var noLap = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[LaporanViewModel::class.java]
        setContentView(binding.root)

        noLap = intent.getIntExtra(EXTRA_NO_LAP, 0)

        setupViewModelBinding()

        // Setup ACTV Tahun
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val yearsAvailable = (2023..currentYear).map { it.toString() } .toList()

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, yearsAvailable)
        val actv = binding.actvTahun
        actv.setAdapter(adapter)
        actv.setText(currentYear.toString(), false)

        actv.setOnItemClickListener { adapterView, view, i, l ->
            viewModel.tahun.value = adapterView.getItemAtPosition(i).toString()
        }
        binding.srlLaporan.isRefreshing = true

        // Setup WebView
        val webView = binding.wvLaporan
        webView.setInitialScale(150)
        webView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
        }

        // webView on loading
        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.wvLaporan.visibility = View.VISIBLE
                binding.srlLaporan.isRefreshing = false
            }

            // on start loading
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.wvLaporan.visibility = View.GONE
                binding.srlLaporan.isRefreshing = true
            }

            // on error loading
            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                binding.wvLaporan.visibility = View.GONE
                binding.srlLaporan.isRefreshing = false
                Toast.makeText(this@LaporanActivity, "Error loading page", Toast.LENGTH_SHORT).show()
            }
        }

        binding.srlLaporan.setOnRefreshListener {
            webView.reload()
        }

        binding.btnExportPDF.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(viewModel.urlExport.value)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.getURL(noLap)
    }

    private fun setupViewModelBinding() {
        viewModel.url.observe(this) {
            binding.wvLaporan.loadUrl(it)
            Log.e("AHHHH", it)
        }

        viewModel.judulLaporan.observe(this) {
            binding.tvJudulLaporan.text = it
        }
    }

    companion object {
        const val EXTRA_NO_LAP = "extra_no_lap"
    }
}
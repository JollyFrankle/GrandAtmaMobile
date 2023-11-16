package com.example.grandatmahotel.ui.customer.act_booking

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.R
import com.example.grandatmahotel.databinding.ActivityBookingBinding
import com.example.grandatmahotel.ui.customer.act_booking.step1.Step1BookingFragment
import com.example.grandatmahotel.ui.customer.act_booking.step2.Step2BookingFragment
import com.example.grandatmahotel.ui.customer.act_booking.step3.Step3BookingFragment
import com.example.grandatmahotel.utils.Utils
import com.example.grandatmahotel.utils.ViewModelFactory

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private lateinit var viewModel: BookingViewModel

    private lateinit var dialogExpired: AlertDialog
    lateinit var dialogLoading: AlertDialog

    private var currentPage = 0
    private var isFinishedBooking = false
    private var id: Long = 0

    private lateinit var pages: List<Fragment?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[BookingViewModel::class.java]
        dialogLoading = Utils.generateLoader(this)
        setContentView(binding.root)

        val s1BF = Step1BookingFragment().apply {
            arguments = intent.extras
        }
        val s2BF = Step2BookingFragment().apply {
            arguments = intent.extras
        }
        val s3BF = Step3BookingFragment().apply {
            arguments = intent.extras
        }
        pages = listOf(
            s1BF,
            s2BF,
            s3BF,
            null
        )

        binding.lpiOnboarding.max = pages.size-1
//        toNextPage(false)

//        binding.btnContinue.setOnClickListener {
//            toNextPage(false)
//        }

        id = intent.getLongExtra(EXTRA_ID, 0L)
        if (id == 0L) {
            Toast.makeText(this, "Ada kesalahan!", Toast.LENGTH_SHORT).show()
            finish()
        }

        viewModel.getDeadlineAndStage(id)

        setupDialogExpired()
        setupViewModelBinding()
    }

    private fun setupViewModelBinding() {
        viewModel.deadlineSeconds.observe(this) {
            binding.tvCountdown.text = getHMS(it)
            Log.e("TAG123", "setupViewModelBinding: $it")
        }

        viewModel.currentPage.observe(this) {
            toPage(it)
        }

        viewModel.cdFinished.observe(this) {
            if (it) {
                dialogExpired.show()
            }
        }
    }

    private fun setupDialogExpired() {
        dialogExpired = AlertDialog.Builder(this)
            .setTitle("Waktu pemesanan habis")
            .setMessage("Silakan ulangi pemesanan.")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .create()
    }

    private fun getHMS(seconds: Long): String {
        // pad 0 to hms
        val h = seconds / 3600
        val m = seconds % 3600 / 60
        val s = seconds % 60

        return "${h.toString().padStart(2, '0')} : ${m.toString().padStart(2, '0')} : ${s.toString().padStart(2, '0')}"
    }

    fun toNextPage(addToBackStack: Boolean = true) {
        binding.lpiOnboarding.setProgressCompat(currentPage, true)
//        if (currentPage+1 > pages.size-1) {
//            setToUploadFragment()
//            return
//        }

        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in, // enter
                R.anim.fade_out, // exit
                R.anim.fade_in, // popEnter
                R.anim.slide_out // popExit
            )
            replace(R.id.fragment_container, pages[currentPage]!!)
            if (addToBackStack) addToBackStack(null)
        }

        currentPage++
    }

    fun toPage(index: Int) {
        currentPage = index

        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in, // enter
                R.anim.fade_out, // exit
                R.anim.fade_in, // popEnter
                R.anim.slide_out // popExit
            )
            replace(R.id.fragment_container, pages[currentPage]!!)
        }

        binding.lpiOnboarding.setProgressCompat(currentPage, true)
        Log.e("HARUSGIMANALAGI", currentPage.toString())
    }

    companion object {
        const val EXTRA_ID = "id"
    }
}
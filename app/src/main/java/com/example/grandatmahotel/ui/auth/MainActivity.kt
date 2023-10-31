package com.example.grandatmahotel.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.databinding.ActivityMainBinding
import com.example.grandatmahotel.ui.auth.login.LoginFragment
import com.example.grandatmahotel.ui.auth.register.RegisterFragment
import com.example.grandatmahotel.ui.customer.CustomerDashboardActivity
import com.example.grandatmahotel.utils.ViewModelFactory
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?): Unit = runBlocking {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this@MainActivity, ViewModelFactory.getInstance(application))[MainViewModel::class.java]

        setContentView(binding.root)

        val userType = viewModel.getUserType()
        if (userType == 'c') {
            val intent = Intent(this@MainActivity, CustomerDashboardActivity::class.java)
            startActivity(intent)
            finish()
            return@runBlocking
        } else if (userType == 'p') {
            val intent = Intent(this@MainActivity, CustomerDashboardActivity::class.java)
            startActivity(intent)
            finish()
            return@runBlocking
        }

        setFragment("login", false)
    }

    fun setFragment(
        frg: String,
        addToBackStack: Boolean = true,
        listElements : ArrayList<Pair<View, String>>? = null,
    ) {

        val transaction = supportFragmentManager
            .beginTransaction()

            .apply {
                listElements?.forEach { (view, transitionName) ->
                    addSharedElement(view, transitionName)
                }
            }



        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        when (frg) {
            "login" -> {
                transaction.replace(binding.root.id, LoginFragment())
                    .commit()
            }
            "register" -> {
                transaction.replace(binding.root.id, RegisterFragment())
                    .commit()
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
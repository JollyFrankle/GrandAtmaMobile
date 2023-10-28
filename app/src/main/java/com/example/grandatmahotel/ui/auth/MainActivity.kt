package com.example.grandatmahotel.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.databinding.ActivityMainBinding
import com.example.grandatmahotel.ui.auth.login.LoginFragment
import com.example.grandatmahotel.ui.auth.register.RegisterFragment
import com.example.grandatmahotel.utils.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application))[MainViewModel::class.java]

        setContentView(binding.root)

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
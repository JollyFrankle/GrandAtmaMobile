package com.example.grandatmahotel.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grandatmahotel.databinding.FragmentLoginBinding
import com.example.grandatmahotel.ui.admin.AdminDashboardActivity
import com.example.grandatmahotel.ui.auth.MainActivity
import com.example.grandatmahotel.ui.customer.CustomerDashboardActivity
import com.example.grandatmahotel.utils.Utils
import com.example.grandatmahotel.utils.ViewModelFactory
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LoginViewModel
    private lateinit var loader: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[LoginViewModel::class.java]
        loader = Utils.generateLoader(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()
            viewModel.loginAsCustomer(email, password)
        }

        binding.btnLoginAsPegawai.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString()
            val password = binding.tilPassword.editText?.text.toString()
            viewModel.loginAsPegawai(email, password)
        }

        binding.btnGoToRegister.setOnClickListener {
            (activity as MainActivity).setFragment("register", true)
        }

        binding.btnGoToForgetPassword.setOnClickListener {
            (activity as MainActivity).setFragment("reset_password", true)
        }

        setupViewModelBinding()
    }

    private fun setupViewModelBinding() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                loader.show()
            } else {
                loader.dismiss()
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        // Handling login result
        viewModel.loginResult.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                if (it == 'c') {
                    val intent = Intent(requireContext(), CustomerDashboardActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else if (it == 'p') {
                    val intent = Intent(requireContext(), AdminDashboardActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }

        // Handling login error
        viewModel.loginError.observe(viewLifecycleOwner) {
            it.errors?.forEach { (key, value) ->
                when (key) {
                    "username" -> binding.tilEmail.error = value
                    "password" -> binding.tilPassword.error = value
                }
            }
        }
    }
}
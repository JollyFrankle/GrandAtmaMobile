package com.example.grandatmahotel.ui.auth.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.grandatmahotel.R
import com.example.grandatmahotel.databinding.FragmentLoginBinding
import com.example.grandatmahotel.ui.auth.MainViewModel
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

        // TODO 1: Set the click listener for the login button

        // TODO 2: Observe all the LiveData from the ViewModel
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
                if (it.token.isNotEmpty()) {
                    // Go to homepage
//                    val intent = Intent(requireContext(), MainScreenActivity::class.java)
//                    startActivity(intent)
//                    requireActivity().finish()
                }
            }
        }

        // Handling login error
//        viewModel.loginError.observe(viewLifecycleOwner) {
//            it.errors?.forEach { (key, value) ->
//                when (key) {
//                    "email" -> binding.inputEmail.error = value
//                    "password" -> binding.inputPassword.error = value
//                }
//            }
//        }
    }
}
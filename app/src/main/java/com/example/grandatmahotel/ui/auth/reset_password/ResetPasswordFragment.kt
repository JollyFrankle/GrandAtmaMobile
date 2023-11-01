package com.example.grandatmahotel.ui.auth.reset_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.R
import com.example.grandatmahotel.databinding.FragmentResetPasswordBinding
import com.example.grandatmahotel.ui.auth.MainActivity
import com.example.grandatmahotel.utils.Utils
import com.example.grandatmahotel.utils.ViewModelFactory

class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ResetPasswordViewModel
    private lateinit var loader: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[ResetPasswordViewModel::class.java]
        loader = Utils.generateLoader(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelBinding()

        binding.btnSubmit.setOnClickListener {
            val email = binding.tilEmail.editText?.text.toString()
            val type = when(binding.rgUserType.checkedRadioButtonId) {
                R.id.radioCustomer -> "c"
                R.id.radioPegawai -> "p"
                else -> "c"
            }

            viewModel.requestResetPasword(email, type)
        }

        binding.btnGoToLogin.setOnClickListener {
            (activity as MainActivity).setFragment("login", true)
        }

        binding.btnGoToRegister.setOnClickListener {
            (activity as MainActivity).setFragment("register", true)
        }
    }

    private fun setupViewModelBinding() {
        viewModel.message.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errors.observe(viewLifecycleOwner) {
            it.errors?.forEach { (key, value) ->
                when (key) {
                    "email" -> binding.tilEmail.error = value
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it) {
                loader.show()
            } else {
                loader.dismiss()
            }
        }

        viewModel.isSuccess.observe(viewLifecycleOwner) {
            if (it == true) {
                (activity as MainActivity).setFragment("login", true)
            }
        }
    }
}
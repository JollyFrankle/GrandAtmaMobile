package com.example.grandatmahotel.ui.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.model.UserCustomer
import com.example.grandatmahotel.databinding.FragmentRegisterBinding
import com.example.grandatmahotel.ui.auth.MainActivity
import com.example.grandatmahotel.utils.Utils
import com.example.grandatmahotel.utils.ViewModelFactory

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RegisterViewModel
    private lateinit var loader: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[RegisterViewModel::class.java]
        loader = Utils.generateLoader(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelBinding()

        binding.btnSubmit.setOnClickListener {
            register()
        }

        binding.btnGoToLogin.setOnClickListener {
            (activity as MainActivity).setFragment("login", true)
        }

        binding.btnGoToForgetPassword.setOnClickListener {
            (activity as MainActivity).setFragment("reset_password", true)
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
                    "nama" -> binding.tilNama.error = value
                    "jenis_identitas" -> setErrorJenisIdentitas(value)
                    "no_identitas" -> binding.tilNomorIdentitas.error = value
                    "no_telp" -> binding.tilNoTelp.error = value
                    "alamat" -> binding.tilAlamat.error = value
                    "password" -> binding.tilNewPassword.error = value
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

    private fun register() {
        resetErrors()

        val email = binding.tilEmail.editText?.text.toString()
        val nama = binding.tilNama.editText?.text.toString()
        val jenisIdentitas = getJenisIdentitas()
        val noIdentitas = binding.tilNomorIdentitas.editText?.text.toString()
        val noTelp = binding.tilNoTelp.editText?.text.toString()
        val alamat = binding.tilAlamat.editText?.text.toString()

        val newPassword = binding.tilNewPassword.editText?.text.toString()
        val newPassConf = binding.tilNewPassConf.editText?.text.toString()

        if (newPassword != newPassConf) {
            binding.tilNewPassConf.error = "Kata sandi tidak sama"
            return
        }

        val newUser = UserCustomer(
            type = "p",
            email = email,
            nama = nama,
            jenisIdentitas = jenisIdentitas,
            noIdentitas = noIdentitas,
            noTelp = noTelp,
            alamat = alamat,
            password = newPassword,
            createdAt = "",
            updatedAt = "",
        )

        viewModel.clientRegister(newUser)
    }

    private fun resetErrors() {
        binding.tilEmail.error = null
        binding.tilNama.error = null
        setErrorJenisIdentitas(null)
        binding.tilNomorIdentitas.error = null
        binding.tilNoTelp.error = null
        binding.tilAlamat.error = null
        binding.tilNewPassword.error = null
        binding.tilNewPassConf.error = null
    }

    private fun getJenisIdentitas(): String {
        return when(binding.rgJenisIdentitas.checkedRadioButtonId) {
            R.id.rbKTP -> "ktp"
            R.id.rbSIM -> "sim"
            R.id.rbPaspor -> "paspor"
            else -> ""
        }
    }

    private fun setErrorJenisIdentitas(error: String?) {
        binding.rbKTP.error = error
        binding.rbSIM.error = error
        binding.rbPaspor.error = error
    }
}
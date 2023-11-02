package com.example.grandatmahotel.ui.customer.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grandatmahotel.R
import com.example.grandatmahotel.data.remote.Result
import com.example.grandatmahotel.data.remote.model.UserCustomer
import com.example.grandatmahotel.databinding.FragmentProfileBinding
import com.example.grandatmahotel.utils.ViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    private var isEdit = false
    private var isChangePassword = false
    private var userClone: UserCustomer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding  = FragmentProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this, ViewModelFactory.getInstance(requireActivity().application))[ProfileViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModelBinding()
        setEdit(false)

        binding.btnCancel.setOnClickListener {
            setEdit(false)
            showUserDetail(userClone!!)
        }

        binding.btnUpdateProfile.setOnClickListener {
            setEdit(true)
        }

        binding.btnSubmit.setOnClickListener {
            modalConfirmUpdate()
        }

        binding.btnChangePassword.setOnClickListener {
            binding.llChangePassword.visibility = View.VISIBLE
            binding.tilOldPassword.editText?.setText("")
            binding.tilNewPassword.editText?.setText("")
            binding.tilNewPassConf.editText?.setText("")

            isChangePassword = !isChangePassword
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            requireActivity().finish()
        }
    }

    private fun setupViewModelBinding() {
        viewModel.detail.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    showUserDetail(it.data)
                    setEdit(false)
                }
                is Result.Error -> {
                    // Show error message
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

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
                    "old_password" -> binding.tilOldPassword.error = value
                    "password" -> binding.tilNewPassword.error = value
                }
            }
        }
    }

    private fun setEdit(newState: Boolean) {
        isEdit = newState
        if (newState) {
            binding.btnCancel.visibility = View.VISIBLE
            binding.btnSubmit.visibility = View.VISIBLE
            binding.btnUpdateProfile.visibility = View.GONE
            binding.btnChangePassword.visibility = View.VISIBLE

            setInputState(true)
        } else {
            binding.btnCancel.visibility = View.GONE
            binding.btnSubmit.visibility = View.GONE
            binding.btnUpdateProfile.visibility = View.VISIBLE
            binding.btnChangePassword.visibility = View.GONE

            setInputState(false)
            binding.llChangePassword.visibility = View.GONE
            isChangePassword = false
        }
    }

    private fun setInputState(newState: Boolean) {
        binding.tilEmail.isEnabled = newState
        binding.tilNama.isEnabled = newState
        setEnabledJenisIdentitas(newState)
        binding.tilNomorIdentitas.isEnabled = newState
        binding.tilNoTelp.isEnabled = newState
        binding.tilAlamat.isEnabled = newState
    }

    private fun showUserDetail(user: UserCustomer) {
        userClone = user.copy()

        binding.tilEmail.editText?.setText(user.email)
        binding.tilNama.editText?.setText(user.nama)
        setJenisIdentitas(user.jenisIdentitas)
        binding.tilNomorIdentitas.editText?.setText(user.noIdentitas)
        binding.tilNoTelp.editText?.setText(user.noTelp)
        binding.tilAlamat.editText?.setText(user.alamat)
    }

    private fun modalConfirmUpdate() {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage("Apakah anda yakin ingin mengubah data?")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                updateProfile()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateProfile() {
        resetErrors()

        val email = binding.tilEmail.editText?.text.toString()
        val nama = binding.tilNama.editText?.text.toString()
        val jenisIdentitas = getJenisIdentitas()
        val noIdentitas = binding.tilNomorIdentitas.editText?.text.toString()
        val noTelp = binding.tilNoTelp.editText?.text.toString()
        val alamat = binding.tilAlamat.editText?.text.toString()

        val oldPassword = if (isChangePassword) binding.tilOldPassword.editText?.text.toString() else null
        val newPassword = if (isChangePassword) binding.tilNewPassword.editText?.text.toString() else null

        if (isChangePassword) {
            // check confirmation
            val newPassConf = binding.tilNewPassConf.editText?.text.toString()
            if (newPassword != newPassConf) {
                binding.tilNewPassConf.error = "Password baru tidak sama"
                return
            }
        }

        val newUser = UserCustomer(
            type = "p",
            email = email,
            nama = nama,
            jenisIdentitas = jenisIdentitas,
            noIdentitas = noIdentitas,
            noTelp = noTelp,
            alamat = alamat,
            createdAt = "",
            updatedAt = "",
            password = newPassword,
            oldPassword = oldPassword
        )

        viewModel.updateProfile(
            newProfile = newUser
        )
    }

    private fun resetErrors() {
        binding.tilEmail.error = null
        binding.tilNama.error = null
        setErrorJenisIdentitas(null)
        binding.tilNomorIdentitas.error = null
        binding.tilNoTelp.error = null
        binding.tilAlamat.error = null
        binding.tilOldPassword.error = null
        binding.tilNewPassword.error = null
        binding.tilNewPassConf.error = null
    }

    private fun setJenisIdentitas(jenis: String) {
        when(jenis) {
            "ktp" -> binding.rbKTP.isChecked = true
            "sim" -> binding.rbSIM.isChecked = true
            "paspor" -> binding.rbPaspor.isChecked = true
        }
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

    private fun setEnabledJenisIdentitas(newState: Boolean) {
        binding.rbKTP.isEnabled = newState
        binding.rbSIM.isEnabled = newState
        binding.rbPaspor.isEnabled = newState
    }
}
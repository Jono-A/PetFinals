package com.shorbgy.petsshelter.ui.home_activity.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.FragmentSettingsBinding
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity
import com.shorbgy.petsshelter.ui.home_activity.HomeViewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        (requireActivity() as HomeActivity).toolbar.visibility = View.GONE
        (requireActivity() as HomeActivity).navView.visibility = View.GONE


        viewModel = (requireActivity() as HomeActivity).viewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        getUserData()

        binding.closeTv.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.updateTv.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.etUsername.text) -> {
                    Toast.makeText(requireContext(), "Please Enter Username", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.etPhone.text) -> {
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Your phone Number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(binding.etAddress.text) -> {
                    Toast.makeText(requireContext(), "Please Enter Your Address", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    updateUserData()
                }
            }
        }

        return binding.root
    }

    private fun getUserData(){
        viewModel.usersMutableLiveData.observe(viewLifecycleOwner, {
            binding.etUsername.setText(it.username)
            binding.etPhone.setText(it.phone)
            binding.etAddress.setText(it.address)
            Glide.with(requireContext())
                .load(it.image_url)
                .into(binding.image)
        })
    }

    private fun updateUserData(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.progress)
        val dialog: Dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        val userMap = HashMap<String, Any>()

        userMap["username"] = binding.etUsername.text.toString()
        userMap["phone"] = binding.etPhone.text.toString()
        userMap["address"] = binding.etAddress.text.toString()

        viewModel.updateUserData(FirebaseAuth.getInstance().uid.toString(), userMap).addOnCompleteListener{
            dialog.dismiss()
            if (it.isSuccessful){
                Toast.makeText(
                    requireContext(),
                    "User Info Updated Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }else{
                Toast.makeText(requireContext(), it.exception?.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }


}
package com.example.petsshelter.ui.home_activity.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.example.petsshelter.R
import com.example.petsshelter.databinding.FragmentSettingsBinding
import com.example.petsshelter.ui.home_activity.HomeActivity
import com.example.petsshelter.ui.home_activity.HomeViewModel
import java.io.FileNotFoundException


class SettingsFragment : Fragment() {

    companion object{
        const val RESULT_LOAD_IMG = 123
    }

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var viewModel: HomeViewModel

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (requireActivity() as HomeActivity).toolbar.visibility = View.GONE
        (requireActivity() as HomeActivity).navView.visibility = View.GONE


        viewModel = (requireActivity() as HomeActivity).viewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        binding.image.setOnClickListener {
            startGallery()
        }

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
                    Toast.makeText(
                        requireContext(),
                        "Please Enter Your Address",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    builder.setView(R.layout.progress)
                    val dialog: Dialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()

                    if (imageUri!=null){

                        viewModel.uploadUserImage(imageUri)

                        viewModel.uploadUserImageTaskMutableLiveData.observe(viewLifecycleOwner, {
                            if (it != null) {
                                val imageUrl = it.result.toString()
                                updateUserData(imageUrl)
                                dialog.dismiss()
                                viewModel.uploadUserImageTaskMutableLiveData.postValue(null)
                            }
                        })
                    }else{
                        updateUserData(null)
                        dialog.dismiss()
                    }


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
                .placeholder(R.mipmap.person_placeholdr)
                .into(binding.image)
        })
    }

    private fun updateUserData(imageUrl: String?){



        val userMap = HashMap<String, Any>()

        userMap["username"] = binding.etUsername.text.toString()
        userMap["phone"] = binding.etPhone.text.toString()
        userMap["address"] = binding.etAddress.text.toString()
        if (imageUrl!=null){
            userMap["image_url"] = imageUrl
        }

        viewModel.updateUserData(FirebaseAuth.getInstance().uid.toString(), userMap).addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(
                    requireContext(),
                    "User Info Updated Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }else{
                Toast.makeText(
                    requireContext(),
                    it.exception?.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG)
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(reqCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            try {
                imageUri = data?.data
                binding.image.setImageURI(imageUri)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "You haven't picked Image", Toast.LENGTH_LONG).show()
        }
    }

}
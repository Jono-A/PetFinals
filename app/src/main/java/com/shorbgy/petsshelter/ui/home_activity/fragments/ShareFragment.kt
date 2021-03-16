package com.shorbgy.petsshelter.ui.home_activity.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.FragmentShareBinding
import com.shorbgy.petsshelter.pojo.Pet
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity
import com.shorbgy.petsshelter.ui.home_activity.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*


class ShareFragment : Fragment() {

    companion object {
        private const val TAG = "ShareFragment"
    }

    lateinit var binding: FragmentShareBinding
    lateinit var viewModel: HomeViewModel

    private var imageUri: Uri? = null
    private val myCalendar: Calendar = Calendar.getInstance()
    var gender = "Male"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        (requireActivity() as HomeActivity).toolbar.visibility = View.GONE
        (requireActivity() as HomeActivity).navView.visibility = View.GONE

        viewModel = (requireActivity() as HomeActivity).viewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share, container, false)

        binding.closeTv.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.rg.setOnCheckedChangeListener {_, checkedId ->
            when(checkedId){
                R.id.male_radio -> gender = "Male"
                R.id.female_radio -> gender = "Female"
            }
        }

        binding.shareTv.setOnClickListener {
            when {
                imageUri == null -> {
                    Toast.makeText(requireContext(), "Please Choose Pet Gif", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.petName.text) -> {
                    Toast.makeText(requireContext(), "Please Enter Pet Name", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.breed.text) -> {
                    Toast.makeText(requireContext(), "Please Enter Pet Breed", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.dob.text) -> {
                    Toast.makeText(requireContext(), "Please Choose Pet Date Of Birth", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.age.text) -> {
                    Toast.makeText(requireContext(), "Please Enter Age In Months", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.about.text) -> {
                    Toast.makeText(requireContext(), "Please Enter About Field", Toast.LENGTH_SHORT).show()
                }
                else -> {

                    val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    builder.setView(R.layout.progress)
                    val dialog: Dialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()


                    viewModel.uploadPetImage(imageUri)

                    viewModel.uploadPetImageTaskMutableLiveData.observe(viewLifecycleOwner) { uri ->

                        if (uri != null) {
                            val id: String = Calendar.getInstance().timeInMillis.toString()
                            val name: String = binding.petName.text.toString()
                            val owner = FirebaseAuth.getInstance().currentUser!!.email
                            val dateOfBirth: String = binding.dob.text.toString()
                            val gender: String = gender
                            val age: String = binding.age.text.toString()
                            val breed: String = binding.breed.text.toString()
                            val about: String = binding.about.text.toString()
                            val imageUrl: String = uri.result.toString()
                            val uid: String = FirebaseAuth.getInstance().currentUser!!.uid

                            val pet = Pet(0, id, name,
                                owner, dateOfBirth, gender,
                                age, breed, about,
                                imageUrl, uid)

                            viewModel.sharePet(pet)

                            viewModel.sharePetTaskMutableLiveData.observe(viewLifecycleOwner) {

                                dialog.dismiss()

                                if (it != null) {
                                    if (it.isSuccessful) {
                                        Snackbar.make(requireView(), "Pet Shared Successfully",
                                            Snackbar.LENGTH_SHORT).show()

                                        viewModel.sharePetTaskMutableLiveData.postValue(null)
                                        viewModel.uploadPetImageTaskMutableLiveData.postValue(null)
                                        viewModel.imageMutableLiveData.postValue(null)

                                        findNavController().popBackStack()
                                    } else {
                                        Log.d(TAG, "onCreateView: " + it.exception?.message)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        val date = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        binding.dob.setOnClickListener {
            DatePickerDialog(requireContext(), date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]).show()
        }

        binding.petGif.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .galleryMimeTypes(  //Exclude gif images
                        mimeTypes = arrayOf(
                                "image/gif"
                        )
                )
                .start()
        }

        viewModel.imageMutableLiveData.observe(viewLifecycleOwner){
            imageUri = it
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.mipmap.pick_gif_placeholder)
                .into(binding.petGif)
        }


        return binding.root
    }


    private fun updateLabel() {
        val myFormat = "MM/dd/yy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.dob.setText(sdf.format(myCalendar.time))
    }
}
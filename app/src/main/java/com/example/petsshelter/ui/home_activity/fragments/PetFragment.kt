package com.example.petsshelter.ui.home_activity.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.example.petsshelter.R
import com.example.petsshelter.databinding.FragmentPetBinding
import com.example.petsshelter.pojo.Pet
import com.example.petsshelter.ui.home_activity.HomeActivity
import com.example.petsshelter.ui.home_activity.HomeViewModel
import java.util.*


class PetFragment : Fragment() {

    private lateinit var binding: FragmentPetBinding
    private lateinit var viewModel: HomeViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        (requireActivity() as HomeActivity).toolbar.visibility = View.GONE
        (requireActivity() as HomeActivity).navView.visibility = View.GONE


        val pet: Pet? = arguments?.getParcelable("pet")

        viewModel = (requireActivity() as HomeActivity).viewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet, container, false)

        binding.petName.text = pet?.name
        binding.dob.text = pet?.dateOfBirth
        binding.age.text = "${pet?.age} Months"
        binding.breed.text = pet?.breed
        binding.gender.text = pet?.gender
        binding.about.text = pet?.about
        binding.adoptBtn.text = "Adopt ${pet?.name} Now!"
        binding.ownerTv.text = pet?.owner

        if (pet?.gender?.toLowerCase(Locale.ROOT) == "male"){
            binding.genderIcon.setImageResource(R.mipmap.male)
        }else{
            binding.genderIcon.setImageResource(R.mipmap.female)
        }

        if (pet?.ownerId.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
            binding.buttons.visibility = View.GONE
            binding.ownerLabel.visibility = View.GONE
            binding.ownerTv.visibility = View.GONE
            binding.deleteBtn.visibility = View.VISIBLE
        }else{
            binding.buttons.visibility = View.VISIBLE
            binding.ownerLabel.visibility = View.VISIBLE
            binding.ownerTv.visibility = View.VISIBLE
            binding.deleteBtn.visibility = View.GONE
        }

        Glide.with(this)
            .load(pet?.imageUrl)
            .into(binding.petImage)

        binding.adoptBtn.setOnClickListener{

            val email = pet?.owner.toString()
            val subject = "Adopting ${pet?.name}"
            val body = " Hi There, I Want To Adopt Your Pet '${pet?.name}', " +
                    "Please If You Interested, Let Me Know."

            sendEmail(email, subject, body)
        }

        binding.deleteBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())

            dialog.setTitle("Delete")
            dialog.setMessage("Do you want to delete this pet?")
            dialog.setPositiveButton("Yes"
            ) { _, _ ->
                viewModel.deletePet(pet!!).addOnCompleteListener {
                    if (it.isSuccessful){
                        Snackbar.make(requireView(), "Pet Deleted Successfully", Snackbar.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }else{
                        Toast.makeText(requireContext(), it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            dialog.setNegativeButton("Cancel"
            ) { dia, _ -> dia.dismiss() }

            dialog.show()
        }

        binding.favouriteBtn.setOnClickListener{

            viewModel.insertFavouritePet(pet!!).addOnCompleteListener{
                if (it.isSuccessful) {
                    Snackbar.make(
                        requireView(),
                        "Added Successfully to Favourite",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.ownerTv.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("owner_id", pet?.ownerId)
            bundle.putBoolean("current_user", false)
            findNavController().navigate(R.id.action_petFragment_to_profileFragment, bundle)
        }

        return binding.root
    }

    private fun sendEmail(email: String, subject: String, body: String) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        i.putExtra(Intent.EXTRA_SUBJECT, subject)
        i.putExtra(Intent.EXTRA_TEXT, body)
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "There are no email clients installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
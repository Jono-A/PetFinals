package com.shorbgy.petsshelter.ui.home_activity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.FragmentPetBinding
import com.shorbgy.petsshelter.pojo.Pet
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity
import com.shorbgy.petsshelter.ui.home_activity.HomeViewModel
import java.util.*

class PetFragment : Fragment() {

    private lateinit var binding: FragmentPetBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {


        (requireActivity() as HomeActivity).toolbar.visibility = View.GONE
        (requireActivity() as HomeActivity).navView.visibility = View.GONE


        val pet: Pet? = arguments?.getParcelable("pet")

        viewModel = (requireActivity() as HomeActivity).viewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet, container, false)

        binding.petName.text = pet?.name
        binding.dob.text = pet?.dateOfBirth
        binding.age.text = pet?.age
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
        }else{
            binding.buttons.visibility = View.VISIBLE
            binding.ownerLabel.visibility = View.VISIBLE
            binding.ownerTv.visibility = View.VISIBLE
        }

        Glide.with(this)
            .load(pet?.imageUrl)
            .into(binding.petImage)

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

}
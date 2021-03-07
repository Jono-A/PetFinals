package com.shorbgy.petsshelter.ui.home_activity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.FragmentPetBinding
import com.shorbgy.petsshelter.pojo.Pet
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity
import java.util.*

class PetFragment : Fragment() {

    private lateinit var binding: FragmentPetBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {


        (requireActivity() as HomeActivity).toolbar.visibility = View.GONE
        (requireActivity() as HomeActivity).navView.visibility = View.GONE


        val pet: Pet? = arguments?.getParcelable("pet")

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet, container, false)

        binding.petName.text = pet?.name
        binding.dob.text = pet?.dateOfBirth
        binding.age.text = pet?.age
        binding.breed.text = pet?.breed
        binding.gender.text = pet?.gender
        binding.about.text = pet?.about
        binding.adoptBtn.text = "Adopt ${pet?.name} Now!"

        if (pet?.gender?.toLowerCase(Locale.ROOT) == "male"){
            binding.genderIcon.setImageResource(R.mipmap.male)
        }else{
            binding.genderIcon.setImageResource(R.mipmap.female)
        }

        Glide.with(this)
            .load(pet?.imageUrl)
            .into(binding.petImage)

        return binding.root
    }

}
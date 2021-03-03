package com.shorbgy.petsshelter.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.FragmentHomeBinding
import com.shorbgy.petsshelter.databinding.FragmentPetBinding

class PetFragment : Fragment() {

    private lateinit var binding: FragmentPetBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pet, container, false)


        Glide.with(this)
            .load("https://media.giphy.com/media/Xb6J8QWlfJR3svhyqD/giphy.gif")
            .into(binding.imageView)

        return binding.root
    }

}
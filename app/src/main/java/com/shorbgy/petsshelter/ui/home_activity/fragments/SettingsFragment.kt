package com.shorbgy.petsshelter.ui.home_activity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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

        return binding.root
    }

    private fun getUserData(){
        viewModel.usersMutableLiveData.observe(viewLifecycleOwner, {
            binding.etUsername.setText(it.username)
            binding.etPhone.setText(it.phone)
            Glide.with(requireContext())
                .load(it.image_url)
                .into(binding.image)
        })
    }


}
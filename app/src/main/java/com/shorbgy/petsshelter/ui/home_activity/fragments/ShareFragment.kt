package com.shorbgy.petsshelter.ui.home_activity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.FragmentShareBinding
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity
import com.shorbgy.petsshelter.ui.home_activity.HomeViewModel

class ShareFragment : Fragment() {

    lateinit var binding: FragmentShareBinding
    lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        viewModel = (requireActivity() as HomeActivity).viewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share, container, false)

        binding.pickImage.setOnClickListener {
            ImagePicker.with(this)
                .galleryMimeTypes(  //Exclude gif images
                    mimeTypes = arrayOf(
                        "image/gif"
                    )
                )
                .start()
        }

        viewModel.imageMutableLiveData.observe(viewLifecycleOwner){
            Glide.with(this)
                .load(it)
                .into(binding.pickImage)
        }
        return binding.root
    }
}
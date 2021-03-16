package com.shorbgy.petsshelter.ui.home_activity.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.FragmentProfileBinding
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity
import com.shorbgy.petsshelter.ui.home_activity.HomeViewModel
import com.shorbgy.petsshelter.utils.ImageDialog

class ProfileFragment : Fragment(){

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var uid: String

    var imageUrl = ""
    var ownerId: String? = ""

    private var isCurrentUserProfile = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        (requireActivity() as HomeActivity).toolbar.visibility = View.VISIBLE
        (requireActivity() as HomeActivity).navView.visibility = View.VISIBLE



        if (arguments!=null) {
            isCurrentUserProfile = requireArguments().getBoolean("current_user", true)
            ownerId = requireArguments().getString("owner_id")
        }

        uid = if (isCurrentUserProfile){
            (requireActivity() as HomeActivity).toolbar.title = "My Profile"
            setHasOptionsMenu(true)
            FirebaseAuth.getInstance().uid.toString()
        }else{
            (requireActivity() as HomeActivity).navView.visibility = View.GONE
            (requireActivity() as HomeActivity).toolbar.title = "Owner Profile"
            ""
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = (requireActivity() as HomeActivity).viewModel
        getUserData()

        binding.proProfileImg.setOnClickListener{
            ImageDialog().popupImageDialog(requireContext(), imageUrl)
        }

        return binding.root
    }

    private fun getUserData(){
        if (isCurrentUserProfile) {
            viewModel.usersMutableLiveData.observe(viewLifecycleOwner, {
                binding.proUsernameTv.text = it.username
                binding.locationTv.text = it.address
                binding.emailTv.text = it.email
                binding.phoneTv.text = it.phone
                imageUrl = it.image_url.toString()
                Glide.with(requireContext())
                    .load(it.image_url)
                    .into(binding.proProfileImg)
            })
        }else{
            viewModel.getUserById(ownerId!!)

            viewModel.ownerMutableLiveData.observe(viewLifecycleOwner, {
                binding.proUsernameTv.text = it.username
                binding.locationTv.text = it.address
                binding.emailTv.text = it.email
                binding.phoneTv.text = it.phone
                imageUrl = it.image_url.toString()
                Glide.with(requireContext())
                    .load(it.image_url)
                    .into(binding.proProfileImg)
            })
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings_menu){
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }
        return super.onOptionsItemSelected(item)
    }
}
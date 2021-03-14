package com.shorbgy.petsshelter.ui.home_activity.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.adapters.PetsAdapter
import com.shorbgy.petsshelter.databinding.FragmentProfileBinding
import com.shorbgy.petsshelter.pojo.Pet
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity
import com.shorbgy.petsshelter.ui.home_activity.HomeViewModel
import com.shorbgy.petsshelter.utils.OnPetsItemSelected

class ProfileFragment : Fragment(), OnPetsItemSelected{

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: PetsAdapter
    private lateinit var uid: String


    private var isCurrentUserProfile = true
    private var pets = mutableListOf<Pet>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        (requireActivity() as HomeActivity).toolbar.visibility = View.VISIBLE
        (requireActivity() as HomeActivity).navView.visibility = View.VISIBLE

        (requireActivity() as HomeActivity).toolbar.title = "My Profile"


        if (arguments!=null) {
            isCurrentUserProfile = requireArguments().getBoolean("current_user", true)
        }

        if (isCurrentUserProfile){
            setHasOptionsMenu(true)
            uid = FirebaseAuth.getInstance().uid.toString()
        }else{
            uid = "TODO"
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        viewModel = (requireActivity() as HomeActivity).viewModel
        adapter = PetsAdapter(requireContext(), this)

        binding.petsRv.adapter = adapter

        getPets()
        getUserData()

        return binding.root
    }

    private fun getUserData(){
        viewModel.usersMutableLiveData.observe(viewLifecycleOwner, {
            binding.proUsernameTv.text = it.username
            Glide.with(requireContext())
                .load(it.image_url)
                .into(binding.proProfileImg)
        })
    }

    private fun getPets(){
        viewModel.getPetsForOneUser(uid)

        viewModel.oneUserPetsMutableLiveData.observe(viewLifecycleOwner, {
            pets = it
            adapter.differ.submitList(pets)
        })
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

    override fun onItemSelected(pos: Int) {
    }
}
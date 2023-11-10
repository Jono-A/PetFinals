package com.example.petsshelter.ui.home_activity.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.example.petsshelter.R
import com.example.petsshelter.adapters.PetsAdapter
import com.example.petsshelter.databinding.FragmentHomeBinding
import com.example.petsshelter.ui.LoginActivity
import com.example.petsshelter.ui.home_activity.HomeActivity
import com.example.petsshelter.ui.home_activity.HomeViewModel
import com.example.petsshelter.utils.OnPetsItemSelected


class HomeFragment : Fragment() , OnPetsItemSelected{

    companion object {
        private const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: PetsAdapter
    private lateinit var viewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{

        setHasOptionsMenu(true)
        (requireActivity() as HomeActivity).toolbar.visibility = View.VISIBLE
        (requireActivity() as HomeActivity).navView.visibility = View.VISIBLE

        (requireActivity() as HomeActivity).toolbar.title = "Home"

        viewModel =  (requireActivity() as HomeActivity).viewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        adapter = PetsAdapter(requireContext(), this)

        binding.petsRv.adapter = adapter

        getPets()

        binding.shareEt.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_shareFragment)
        }

        getUserPhoto()

        return binding.root
    }

    private fun getUserPhoto(){

        viewModel.usersMutableLiveData.observe(viewLifecycleOwner, {

            val photoUrl = it.image_url
            Log.d(TAG, "getUserPhoto: $photoUrl")

            Glide.with(requireContext())
                .load(photoUrl)
                .into(binding.userImage)
        })


    }

    private fun getPets(){
        viewModel.petsMutableLiveData.observe(viewLifecycleOwner){
            adapter.differ.submitList(it.reversed())
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sign_out){
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            startActivity(
                Intent(requireActivity(), LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            requireActivity().finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(pos: Int) {
        val bundle = Bundle()

        bundle.putParcelable("pet", adapter.differ.currentList[pos])

        findNavController().navigate(R.id.action_homeFragment_to_petFragment, bundle)
    }
}
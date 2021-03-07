package com.shorbgy.petsshelter.ui.home_activity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.adapters.PetsAdapter
import com.shorbgy.petsshelter.databinding.FragmentHomeBinding
import com.shorbgy.petsshelter.pojo.Pet
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity
import com.shorbgy.petsshelter.ui.home_activity.HomeViewModel
import com.shorbgy.petsshelter.utils.OnPetsItemSelected
import com.shorbgy.petsshelter.utils.OnShareItemSelected

class HomeFragment : Fragment() , OnPetsItemSelected, OnShareItemSelected{

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: PetsAdapter
    private lateinit var viewModel: HomeViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{

        (requireActivity() as HomeActivity).toolbar.visibility = View.VISIBLE
        (requireActivity() as HomeActivity).navView.visibility = View.VISIBLE

        (requireActivity() as HomeActivity).toolbar.title = "Home"

        viewModel =  (requireActivity() as HomeActivity).viewModel
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        adapter = PetsAdapter(requireContext(), this, this)

        binding.petsRv.adapter = adapter

        getPets()


        return binding.root
    }

    private fun getPets(){
        viewModel.petsMutableLiveData.observe(viewLifecycleOwner){
            adapter.pets = it.reversed() as MutableList<Pet>
            adapter.notifyDataSetChanged()
        }
    }

    override fun onItemSelected(pos: Int) {
        val bundle = Bundle()

        bundle.putParcelable("pet", adapter.pets[pos])

        findNavController().navigate(R.id.action_homeFragment_to_petFragment, bundle)
    }

    override fun onShareSelected(pos: Int) {
        findNavController().navigate(R.id.action_homeFragment_to_shareFragment)
    }
}
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
import com.shorbgy.petsshelter.utils.OnPetsItemSelected

class HomeFragment : Fragment() , OnPetsItemSelected{

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: PetsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{

        (requireActivity() as HomeActivity).toolbar.visibility = View.VISIBLE
        (requireActivity() as HomeActivity).navView.visibility = View.VISIBLE

        (requireActivity() as HomeActivity).toolbar.title = "Home"

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        adapter = PetsAdapter(requireContext(), this)

        binding.petsRv.adapter = adapter


        val pets = mutableListOf<Pet>()

        pets.add(Pet("", "Kitty", "25/02",
            "Female", "Small", "Cat", "",
            "https://media.giphy.com/media/TA6Fq1irTioFO/giphy.gif"))

        pets.add(Pet("", "Za3tar", "25/02",
            "Male", "Grown", "Dog", "",
            "https://media.giphy.com/media/hkFgpYE8CRqog/giphy.gif"))

        pets.add(Pet("", "Zo2lot", "25/02",
            "Male", "Medium", "Dog", "",
            "https://media.giphy.com/media/Gx2vpQi2WPToc/giphy.gif"))

        pets.add(Pet("", "Rocky", "25/02",
            "Male", "Grown", "Dog", "",
            "https://media.giphy.com/media/ASsGSJEh0a63u/giphy.gif"))

        pets.add(Pet("", "Kot Kot", "25/02",
            "Female", "Grown", "Cat", "",
            "https://media.giphy.com/media/fbL0RLU92W7Oo/giphy.gif"))

        adapter.pets = pets
        adapter.notifyDataSetChanged()


        return binding.root
    }

    override fun onItemSelected(pos: Int) {
        val bundle = Bundle()

        bundle.putParcelable("pet", adapter.pets[pos])

        findNavController().navigate(R.id.action_homeFragment_to_petFragment, bundle)
    }
}
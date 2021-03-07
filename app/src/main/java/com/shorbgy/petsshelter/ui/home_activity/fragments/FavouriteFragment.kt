package com.shorbgy.petsshelter.ui.home_activity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.adapters.PetsAdapter
import com.shorbgy.petsshelter.databinding.FragmentFavouriteBinding
import com.shorbgy.petsshelter.pojo.Pet
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity
import com.shorbgy.petsshelter.ui.home_activity.HomeViewModel
import com.shorbgy.petsshelter.utils.OnPetsItemSelected

class FavouriteFragment : Fragment(), OnPetsItemSelected{

    lateinit var binding: FragmentFavouriteBinding
    lateinit var viewModel: HomeViewModel
    lateinit var adapter: PetsAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        (requireActivity() as HomeActivity).toolbar.title = "Favourite"
        (requireActivity() as HomeActivity).toolbar.visibility = View.VISIBLE
        (requireActivity() as HomeActivity).navView.visibility = View.VISIBLE

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite, container, false)
        viewModel = (requireActivity() as HomeActivity).viewModel
        adapter = PetsAdapter(requireContext(), this)

        binding.petsRv.adapter = adapter

        getPets()

        val itemTouchHelper = object :
            ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
            ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pos = viewHolder.adapterPosition
                val pet = adapter.pets[pos]
                viewModel.deletePet(pet)
            }
        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.petsRv)

        return binding.root
    }

    private fun getPets(){
        viewModel.getPetsFromLocalDb().observe(viewLifecycleOwner, {
            adapter.pets = it as MutableList<Pet>
            adapter.notifyDataSetChanged()
        })

    }
    override fun onItemSelected(pos: Int) {

        val bundle = Bundle()
        bundle.putParcelable("pet", adapter.pets[pos])

        findNavController().navigate(R.id.action_favouriteFragment_to_petFragment, bundle)
    }
}
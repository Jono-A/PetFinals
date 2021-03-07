package com.shorbgy.petsshelter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.PetItemBinding
import com.shorbgy.petsshelter.pojo.Pet
import com.shorbgy.petsshelter.utils.OnPetsItemSelected
import com.shorbgy.petsshelter.utils.OnShareItemSelected

class PetsAdapter(private val context: Context,
                  private val onPetsItemSelected: OnPetsItemSelected,
                  private val onShareItemSelected: OnShareItemSelected):
    RecyclerView.Adapter<PetsAdapter.PetsViewHolder>() {

    class PetsViewHolder(itemView: View, val binding: PetItemBinding) : RecyclerView.ViewHolder(itemView)



    var pets = mutableListOf<Pet>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {

        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val binding: PetItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.pet_item, parent, false)

        return PetsViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {

        if(position>0){
            holder.binding.shareLayout.visibility = View.GONE
        }
        holder.binding.petName.text = pets[position].name

        Glide.with(context)
            .load(pets[position].imageUrl)
            .into(holder.binding.petImage)

        holder.binding.petImage.setOnClickListener {
            onPetsItemSelected.onItemSelected(position)
        }

        holder.binding.shareEt.setOnClickListener {
            onShareItemSelected.onShareSelected(position)
        }
    }

    override fun getItemCount(): Int {
        return pets.size
    }
}
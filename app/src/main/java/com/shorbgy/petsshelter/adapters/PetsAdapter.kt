package com.shorbgy.petsshelter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.PetItemBinding
import com.shorbgy.petsshelter.pojo.Pet
import com.shorbgy.petsshelter.utils.OnPetsItemSelected

class PetsAdapter(private val context: Context,
                  private val onPetsItemSelected: OnPetsItemSelected):
    RecyclerView.Adapter<PetsAdapter.PetsViewHolder>() {

    class PetsViewHolder(itemView: View, val binding: PetItemBinding) : RecyclerView.ViewHolder(itemView)


    private val differCallback = object: DiffUtil.ItemCallback<Pet>() {

        override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetsViewHolder {

        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val binding: PetItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.pet_item, parent, false)

        return PetsViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: PetsViewHolder, position: Int) {

        holder.binding.petName.text = differ.currentList[position].name

        Glide.with(context)
            .load(differ.currentList[position].imageUrl)
            .into(holder.binding.petImage)

        holder.binding.petImage.setOnClickListener {
            onPetsItemSelected.onItemSelected(position)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
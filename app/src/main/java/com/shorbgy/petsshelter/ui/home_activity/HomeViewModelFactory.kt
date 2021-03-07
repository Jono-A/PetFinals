package com.shorbgy.petsshelter.ui.home_activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shorbgy.petsshelter.repository.PetRepository

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val repository: PetRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}
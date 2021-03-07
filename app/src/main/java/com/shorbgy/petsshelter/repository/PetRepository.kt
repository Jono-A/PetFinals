package com.shorbgy.petsshelter.repository

import com.shorbgy.petsshelter.db.PetDatabase
import com.shorbgy.petsshelter.pojo.Pet

class PetRepository(private val db: PetDatabase){

    suspend fun insertPet(pet: Pet) = db.petDao().insertPet(pet)

    suspend fun deletePet(pet: Pet) = db.petDao().deletePet(pet)

    fun getPets() = db.petDao().getPets()
}
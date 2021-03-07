package com.shorbgy.petsshelter.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.shorbgy.petsshelter.pojo.Pet

@Dao
interface PetDao {

    @Insert
    suspend fun insertPet(pet: Pet)

    @Delete
    suspend fun deletePet(pet: Pet)

    @Query("SELECT * FROM Pet")
    fun getPets(): LiveData<List<Pet>>
}
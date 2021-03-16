package com.shorbgy.petsshelter.ui.home_activity

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.shorbgy.petsshelter.pojo.Pet
import com.shorbgy.petsshelter.pojo.User
import java.util.*


class HomeViewModel(private val uid: String): ViewModel(){

    companion object {
        private const val TAG = "HomeViewModel"
    }


    private var petReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Pets")
    private var userReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private var favReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Favourite")
    private val petStorageReference: StorageReference = FirebaseStorage.getInstance().getReference("Pets")

    private val pets = mutableListOf<Pet>()
    private val favPets = mutableListOf<Pet>()

    val petsMutableLiveData = MutableLiveData<MutableList<Pet>>()
    val favPetsMutableLiveData = MutableLiveData<MutableList<Pet>>()
    val usersMutableLiveData = MutableLiveData<User>()
    val ownerMutableLiveData = MutableLiveData<User>()


    val uploadImageTaskMutableLiveData = MutableLiveData<Task<Uri>?>()
    val sharePetTaskMutableLiveData = MutableLiveData<Task<Void>?>()
    val imageMutableLiveData = MutableLiveData<Uri?>()


    init {
        getPets()
        getUser()
        getFavouritePets()
    }

    fun uploadImage(fileUri: Uri?) {
        val filePath: StorageReference = petStorageReference
            .child(
                Objects.requireNonNull(Calendar.getInstance().timeInMillis
                    .toString() + ".gif")
            )
        val uploadTask = filePath.putFile(fileUri!!)
        uploadTask.addOnFailureListener { e: Exception ->
            Log.d(TAG, "uploadImage: " + e.message)
        }
            .addOnSuccessListener { _: UploadTask.TaskSnapshot? ->
                uploadTask.continueWithTask { task: Task<UploadTask.TaskSnapshot?> ->
                    if (!task.isSuccessful) {
                        throw Objects.requireNonNull(task.exception) as Throwable
                    }
                    filePath.downloadUrl
                }.addOnCompleteListener{
                    uploadImageTaskMutableLiveData.postValue(it)
                }
            }
    }

    fun sharePet(pet: Pet){
        petReference.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(pet.id!!).setValue(pet).addOnCompleteListener {
            sharePetTaskMutableLiveData.postValue(it)
        }
    }

    private fun getPets(){
        val reference = FirebaseDatabase.getInstance().getReference("Pets")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pets.clear()

                snapshot.children.forEach { item ->
                    Log.d(TAG, "onDataChange: ${item.key.toString()}")
                    snapshot.child(item.key.toString()).children.forEach{ posts->
                        val pet = posts.getValue(Pet::class.java)
                        pets.add(pet!!)
                    }
                }

                pets.sortBy {
                    it.id?.toLong()
                }
                petsMutableLiveData.postValue(pets)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }


    private fun getUser(){
        userReference.child(uid).addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                usersMutableLiveData.postValue(user)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }

    fun getUserById(ownerId: String){
        userReference.child(ownerId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                ownerMutableLiveData.postValue(user)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }

    fun updateUserData(uid: String, userMap: Map<String, Any>): Task<Void>{
        return userReference.child(uid).updateChildren(userMap)
    }

    fun insertFavouritePet(pet: Pet): Task<Void>{
        return favReference.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(pet.id!!).setValue(pet)
    }

    fun deletePetFromFavourite(pet: Pet): Task<Void>{
        return favReference.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child(pet.id!!).removeValue()
    }

    private fun getFavouritePets(){
        val reference = FirebaseDatabase.getInstance().getReference("Favourite")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favPets.clear()

                snapshot.children.forEach { item ->
                    Log.d(TAG, "onDataChange: ${item.key.toString()}")
                    snapshot.child(item.key.toString()).children.forEach{ posts->
                        val pet = posts.getValue(Pet::class.java)
                        favPets.add(pet!!)
                    }
                }

                favPetsMutableLiveData.postValue(favPets)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }
}
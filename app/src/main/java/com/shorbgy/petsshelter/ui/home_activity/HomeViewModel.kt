package com.shorbgy.petsshelter.ui.home_activity

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.shorbgy.petsshelter.pojo.Pet
import java.util.*


class HomeViewModel: ViewModel(){

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private var petReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Pets")
    private val petStorageReference: StorageReference = FirebaseStorage.getInstance().getReference("Pets")

    private val pets = mutableListOf<Pet>()

    val petsMutableLiveData = MutableLiveData<MutableList<Pet>>()

    val uploadImageTaskMutableLiveData = MutableLiveData<Task<Uri>?>()
    val sharePetTaskMutableLiveData = MutableLiveData<Task<Void>?>()
    val imageMutableLiveData = MutableLiveData<Uri?>()


    init {
        getPets()
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
        petReference.child(pet.id.toString()).setValue(pet).addOnCompleteListener {
            sharePetTaskMutableLiveData.postValue(it)
        }
    }

    private fun getPets(){
        val reference = FirebaseDatabase.getInstance().getReference("Pets")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pets.clear()
                snapshot.children.forEach { item ->
                    val pet = item.getValue(Pet::class.java)
                    pets.add(pet!!)
                }

                petsMutableLiveData.postValue(pets)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: ${error.message}")
            }
        })
    }
}
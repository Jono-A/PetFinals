package com.shorbgy.petsshelter.ui.home_activity

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*


class HomeViewModel: ViewModel(){

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val petReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Pets")
    private val petStorageReference: StorageReference = FirebaseStorage.getInstance().getReference("Pets")

    val uploadImageTaskMutableLiveData = MutableLiveData<Task<Uri>>()
    val imageMutableLiveData = MutableLiveData<Uri?>()


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
}
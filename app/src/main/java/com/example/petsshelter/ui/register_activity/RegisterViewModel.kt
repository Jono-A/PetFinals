package com.example.petsshelter.ui.register_activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class RegisterViewModel : ViewModel() {

    companion object {
        private const val TAG = "RegisterViewModel"
    }

    private val auth = FirebaseAuth.getInstance()
    private val usersStorageReference: StorageReference = FirebaseStorage.getInstance().getReference("Users")
    private val userDatabaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

    val uploadImageTaskMutableLiveData = MutableLiveData<Task<Uri>?>()

    val imageMutableLiveData = MutableLiveData<Uri?>()

    fun signUp(email: String, password: String): Task<AuthResult>{
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun createUser(uid: String, userMap: Map<String, Any>): Task<Void>{
        return userDatabaseReference.child(uid).setValue(userMap)
    }

    fun uploadImage(fileUri: Uri?, userId: String) {
        if(fileUri!=null) {
            val filePath: StorageReference = usersStorageReference
                .child(
                    Objects.requireNonNull(
                        "$userId.jpg"
                    )
                )
            val uploadTask = filePath.putFile(fileUri)
            uploadTask.addOnFailureListener { e: Exception ->
                Log.d(TAG, "uploadImage: " + e.message)
            }
                .addOnSuccessListener { _: UploadTask.TaskSnapshot? ->
                    uploadTask.continueWithTask { task: Task<UploadTask.TaskSnapshot?> ->
                        if (!task.isSuccessful) {
                            throw Objects.requireNonNull(task.exception) as Throwable
                        }
                        filePath.downloadUrl
                    }.addOnCompleteListener {
                        uploadImageTaskMutableLiveData.postValue(it)
                    }
                }
        }
    }

    fun updateUserImage(uid: String, imageUrl: String): Task<Void>{
        return userDatabaseReference.child(uid).updateChildren(mapOf(Pair("image_url", imageUrl)))
    }

}
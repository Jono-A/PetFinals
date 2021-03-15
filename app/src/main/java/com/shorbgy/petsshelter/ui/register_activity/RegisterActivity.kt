package com.shorbgy.petsshelter.ui.register_activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.ActivityRegisterBinding
import com.shorbgy.petsshelter.ui.home_activity.HomeActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel


    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding.signupImageLayout.setOnClickListener{
            ImagePicker.with(this)
                .start()
        }

        binding.signUpBtn.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.etEmail.text) -> {
                    Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.etUsername.text) -> {
                    Toast.makeText(this, "Please Enter Username", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.etAddress.text) -> {
                    Toast.makeText(this, "Please Enter Your Address", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.etPhone.text) -> {
                    Toast.makeText(this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show()
                }
                TextUtils.isEmpty(binding.etPassword.text) -> {
                    Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    signUp()
                }
            }
        }

        previewPhoto()
    }

    private fun previewPhoto(){
        viewModel.imageMutableLiveData.observe(this, {
            fileUri = it
            Glide.with(this)
                .load(fileUri)
                .placeholder(R.mipmap.pick_gif_placeholder)
                .into(binding.image)
        })
    }

    private fun signUp(){

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(R.layout.progress)
        val dialog: Dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        viewModel.signUp(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            .addOnCompleteListener{sTask->
                if (sTask.isSuccessful){

                    val userMap = mutableMapOf<String, Any>()

                    userMap["email"] = binding.etEmail.text.toString()
                    userMap["username"] = binding.etUsername.text.toString()
                    userMap["phone"] = binding.etPhone.text.toString()
                    userMap["address"] = binding.etAddress.text.toString()
                    userMap["image_url"] = ""
                    userMap["uid"] = FirebaseAuth.getInstance().currentUser!!.uid

                    viewModel.createUser(FirebaseAuth.getInstance().currentUser!!.uid, userMap)
                        .addOnCompleteListener{
                            if (it.isSuccessful){
                                viewModel.uploadImage(fileUri, FirebaseAuth.getInstance().currentUser!!.uid)
                                viewModel.uploadImageTaskMutableLiveData.observe(this, {uploadTask->
                                    if (uploadTask!=null && uploadTask.isSuccessful){
                                        val imageUrl = uploadTask.result.toString()
                                        viewModel
                                            .updateUserImage(FirebaseAuth.getInstance().currentUser!!.uid, imageUrl)
                                            .addOnCompleteListener{uTask->
                                                dialog.dismiss()
                                                if (uTask.isSuccessful){
                                                    val intent = Intent(this, HomeActivity::class.java)
                                                    intent.addFlags(
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                                                Intent.FLAG_ACTIVITY_NEW_TASK
                                                    )
                                                    startActivity(intent)
                                                    viewModel.uploadImageTaskMutableLiveData.postValue(null)
                                                }else{
                                                    Toast.makeText(this, "An Error Occurred",
                                                        Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    }
                                })

                            }else{
                                dialog.dismiss()
                                Toast.makeText(this, "An Error Occurred", Toast.LENGTH_SHORT).show()
                            }
                        }


                }else{
                    dialog.dismiss()
                    Toast.makeText(this, sTask.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data
                viewModel.imageMutableLiveData.postValue(fileUri)

            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }



}
package com.shorbgy.petsshelter.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.shorbgy.petsshelter.R
import com.shorbgy.petsshelter.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var  binding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)


        binding.buttonSignup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}
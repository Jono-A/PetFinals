package com.shorbgy.petsshelter.ui.home_activity

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.shorbgy.petsshelter.R


class HomeActivity : AppCompatActivity(), ChipNavigationBar.OnItemSelectedListener{

    companion object {
        private const val TAG = "HomeActivity"
    }

    private lateinit var navController: NavController
    lateinit var viewModel: HomeViewModel

    lateinit var navView: ChipNavigationBar
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)


        toolbar = findViewById(R.id.bar)
        setSupportActionBar(toolbar)

        navView.setMenuResource(R.menu.bottom_nav_menu)
        navView.setItemSelected(R.id.menu_home)
        navView.setOnItemSelectedListener(this)

    }

    override fun onItemSelected(id: Int) {
        when (id) {
            R.id.menu_home -> {
                navController.popBackStack()
                navController.navigate(R.id.homeFragment)
            }
            R.id.menu_fav -> {
                navController.popBackStack()
                navController.navigate(R.id.favouriteFragment)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            viewModel.imageMutableLiveData.postValue(fileUri)

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
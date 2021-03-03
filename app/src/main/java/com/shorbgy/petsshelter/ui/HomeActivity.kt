package com.shorbgy.petsshelter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.shorbgy.petsshelter.R

class HomeActivity : AppCompatActivity(), ChipNavigationBar.OnItemSelectedListener{

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navView: ChipNavigationBar = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)

        navView.setMenuResource(R.menu.bottom_nav_menu)

        navView.setOnItemSelectedListener(this)

    }

    override fun onItemSelected(id: Int) {
        when (id) {
            R.id.menu_home -> {
                navController.navigate(R.id.homeFragment)
            }
            R.id.menu_fav -> {
                navController.navigate(R.id.favouriteFragment)
            }
        }
    }
}
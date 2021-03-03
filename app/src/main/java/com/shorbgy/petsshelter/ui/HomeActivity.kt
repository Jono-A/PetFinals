package com.shorbgy.petsshelter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.shorbgy.petsshelter.R


class HomeActivity : AppCompatActivity(), ChipNavigationBar.OnItemSelectedListener{

    private lateinit var navController: NavController

    lateinit var navView: ChipNavigationBar
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
}
package com.example.yp_playlist.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.navigation.fragment.NavHostFragment
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.ActivityMainBinding
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        mainBinding.bottomNavigationView.setupWithNavController(navController)
    }
}
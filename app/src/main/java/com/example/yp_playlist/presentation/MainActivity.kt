package com.example.yp_playlist.presentation

import android.content.Intent
import android.net.VpnService
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import com.example.yp_playlist.R
import com.example.yp_playlist.databinding.ActivityMainBinding
import androidx.navigation.ui.setupWithNavController
import com.example.yp_playlist.vpn.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private val vpnPreparation = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> if (result.resultCode == RESULT_OK) viewModel.startVpn(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        mainBinding.bottomNavigationView.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerActivity,
                R.id.newPlaylistFragment,
                R.id.OpenPlaylistFragment,
                R.id.editPlaylistFragment-> {
                    mainBinding.bottomNavigationView.isVisible = false
                    mainBinding.BottomNavBar.isVisible = false
                }

                else -> {
                    mainBinding.bottomNavigationView.isVisible = true
                    mainBinding.BottomNavBar.isVisible = true
                }
            }
        }

        startVpn()


    }

    private fun startVpn() = VpnService.prepare(this)?.let {
        vpnPreparation.launch(it)
    } ?: viewModel.startVpn(this)

}
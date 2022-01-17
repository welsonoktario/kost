package com.ubaya.kost

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ubaya.kost.databinding.ActivityMainBinding
import com.ubaya.kost.util.PrefManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragment_login,
                R.id.fragment_dashboard,
                R.id.fragment_services,
                R.id.fragment_pembukuan,
                R.id.fragment_denda,
                R.id.fragment_transaksi,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.fragment_login -> navView.visibility = View.GONE
                R.id.fragment_register -> navView.visibility = View.GONE
                R.id.fragment_catatan -> navView.visibility = View.GONE
                R.id.fragment_chats -> navView.visibility = View.GONE
                R.id.fragment_notifications -> navView.visibility = View.GONE
                R.id.fragment_chat_room -> navView.visibility = View.GONE
                R.id.fragment_detail_tenant -> navView.visibility = View.GONE
                R.id.fragment_add_tenant -> navView.visibility = View.GONE
                else -> navView.visibility = View.VISIBLE
            }
        }

        val pref = PrefManager.getInstance(this)

        if (!pref.authToken.isNullOrEmpty() && pref.authUser != null) {
            setContentView(binding.root)
            if (pref.authUser!!.type == "Owner") {
                navController.navigate(R.id.action_fragment_login_to_owner_navigation)
            } else {
                navController.navigate(R.id.action_fragment_login_to_tenant_navigation)
            }
        } else {
            setContentView(binding.root)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
        }

        return super.onOptionsItemSelected(item)
    }
}
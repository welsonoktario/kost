package com.ubaya.kost

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ubaya.kost.data.Global
import com.ubaya.kost.databinding.ActivityMainBinding
import com.ubaya.kost.util.PrefManager
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity(), ImageLoaderFactory {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_main
        ) as NavHostFragment

        navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.fragment_login,
                R.id.fragment_dashboard,
                R.id.fragment_services,
                R.id.fragment_pembukuan,
                R.id.fragment_denda,
                R.id.fragment_transaksi,
                R.id.fragment_tenant_home,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragment_login -> navView.visibility = View.GONE
                R.id.fragment_register -> navView.visibility = View.GONE
                R.id.fragment_catatan -> navView.visibility = View.GONE
                R.id.fragment_chats -> navView.visibility = View.GONE
                R.id.fragment_notifications -> navView.visibility = View.GONE
                R.id.fragment_chat_room -> navView.visibility = View.GONE
                R.id.fragment_detail_tenant -> navView.visibility = View.GONE
                R.id.fragment_add_tenant -> navView.visibility = View.GONE
                R.id.fragment_tenant_home -> navView.visibility = View.GONE
                R.id.fragment_tenant_notification -> navView.visibility = View.GONE
                R.id.fragment_tenant_message -> navView.visibility = View.GONE
                else -> navView.visibility = View.VISIBLE
            }
        }

        navView.setOnItemReselectedListener { }

        val pref = PrefManager.getInstance(this)

        if (!pref.authToken.isNullOrEmpty() && pref.authUser != null) {
            if (pref.authUser!!.type == "Owner") {
                Global.apply {
                    authUser = pref.authUser!!
                    authToken = pref.authToken!!
                }

                navController.navigate(R.id.action_fragment_login_to_owner_navigation)
            } else {
                Global.apply {
                    authUser = pref.authUser!!
                    authTenant = pref.authTenant!!
                    authToken = pref.authToken!!
                }

                navController.navigate(R.id.action_fragment_login_to_tenant_navigation)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(this))
                    .build()
            }
            .build()
    }
}
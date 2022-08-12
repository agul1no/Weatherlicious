package com.example.weatherlicious

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.weatherlicious.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private var navDestination: NavDestination? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbarMainFragment = findViewById<View>(R.id.toolbarMainFragment) as Toolbar
        setSupportActionBar(toolbarMainFragment)


        navController = configureNavController()
        appBarConfiguration = configureAppBar(navController)

        //binding.navigationView.menu.add("Hola Hola")

        setToolbarItemListener()

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            configureActionBarDependingOnDestination(nd)
            navDestination = nd
        }
    }

    private fun configureNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        return navController
    }

    private fun configureAppBar(navController: NavController): AppBarConfiguration {
        //appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        // Add the id of the fragment where you don't need the back arrow
        //appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFragment, R.id.addFragment))
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.transparent
                )
            )
        )
        appBarConfiguration =
            AppBarConfiguration(navController.graph, findViewById(R.id.drawerLayout))
        setupActionBarWithNavController(navController, appBarConfiguration)
        return appBarConfiguration
    }

    private fun configureActionBarDependingOnDestination(nd: NavDestination) {
        if (nd.id == R.id.mainFragment) {
            //supportActionBar?.hide()
            binding.toolbarMainFragment.isVisible = true
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            //window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        if (nd.id == R.id.addFragment) {
            binding.toolbarMainFragment.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.add_menu_main_fragment, menu)
        return true
    }

    private fun setToolbarItemListener() {
        binding.toolbarMainFragment.setOnMenuItemClickListener { menuItem ->
            navigateToAddFragment(menuItem)
        }
    }

    private fun navigateToAddFragment(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_add -> {
                // Navigate to add screen
                //findNavController().navigate(R.id.action_mainFragment_to_addFragment)
                navController.navigate(R.id.action_mainFragment_to_addFragment)
                true
            }

            else -> false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        when (navDestination?.id) {
            R.id.mainFragment -> {
                val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    finish()
                }
            }
//            R.id.splashFragment -> { finish() }
            R.id.addFragment -> {
                super.onBackPressed()
            }
//            R.id.gameFragment -> { super.onBackPressed() }
//            R.id.afterGameFragment -> { super.onBackPressed() }
        }

    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
//    }
}
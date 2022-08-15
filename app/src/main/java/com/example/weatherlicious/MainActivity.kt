package com.example.weatherlicious

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.weatherlicious.databinding.ActivityMainBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private var navDestination: NavDestination? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val mainActivityViewModel : MainActivityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbarMainFragment = findViewById<View>(R.id.toolbarMainFragment) as Toolbar
        setSupportActionBar(toolbarMainFragment)

        navController = configureNavController()
        appBarConfiguration = configureAppBar(navController)

        setToolbarItemListener()

        configureHeaderNavigationDrawer()
        configureNavigationDrawerMenu()
        infoButtonHeaderInfoDialog()

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            configureActionBarDependingOnDestination(nd)
            navDestination = nd
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun configureHeaderNavigationDrawer(){
        val header = binding.navigationView.getHeaderView(0)
        val cityMainLocation = header.findViewById<TextView>(R.id.tvCityMainLocation)
        //val view = View(applicationContext)
//        mainActivityViewModel.mainLocation.observe(this){ mainLocation ->
//            cityMainLocation.text = mainLocation
//            cityMainLocation.setTextColor(MaterialColors.getColor(cityMainLocation,
//                com.google.android.material.R.attr.colorOnSecondary))
//        }

        mainActivityViewModel.getMainLocation().observe(this){ mainLocation ->
            if (mainLocation != null){
                cityMainLocation.text = mainLocation.name
            }else{
                Toast.makeText(applicationContext, "Click on the add button to add a main location", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun infoButtonHeaderInfoDialog(){
        val header = binding.navigationView.getHeaderView(0)
        val infoButton = header.findViewById<ImageView>(R.id.ivInfoButton)
        infoButton.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setMessage("Click on a city to delete it or make it your main location")
            alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
                dialog.cancel()
            }
            val alterDialog = alertDialogBuilder.create()
            alterDialog.show()
        }
    }

    private fun configureNavigationDrawerMenu(){
        binding.navigationView.menu.add("Other Locations")
//        mainActivityViewModel.listOtherLocations.observe(this){ listOtherLocations ->
//            listOtherLocations?.forEach { city ->
//                binding.navigationView.menu.add(city.name)
//            }
//        }

        mainActivityViewModel.getOtherLocations().observe(this) { listOfCities ->
            binding.navigationView.menu.clear()
            listOfCities.forEach { city ->
                binding.navigationView.menu.add(city.name)
            }
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
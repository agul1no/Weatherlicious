package com.example.weatherlicious

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.data.model.forecastweather.RemoteForecastWeather
import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.databinding.ActivityMainBinding
import com.example.weatherlicious.ui.mainfragment.MainFragmentDirections
import com.example.weatherlicious.util.dialog.CustomAlertDialog
import com.example.weatherlicious.util.dialog.DeleteOrMakeMainLocationDialog
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private var navDestination: NavDestination? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbarMainFragment = findViewById<View>(R.id.toolbarMainActivity) as Toolbar
        setSupportActionBar(toolbarMainFragment)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.transparent)))

        navController = configureNavController()
        drawerLayout = binding.drawerLayout //findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView: NavigationView = binding.navigationView

        appBarConfiguration = AppBarConfiguration(
            navController.graph,
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setToolbarItemListener()

        configureHeaderNavigationDrawer()
        configureNavigationDrawerMenu()
        infoButtonHeaderInfoDialog()


//        val toggle = ActionBarDrawerToggle(this, drawerLayout, binding.appBarMain.toolbarMainActivity, R.string.open, R.string.close)
//        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
        //navigationDrawerItemClickListener()

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            configureActionBarDependingOnDestination(nd)
            navDestination = nd
        }
    }

    override fun onResume() {
        super.onResume()
        statusBarListener()
    }

    private fun configureHeaderNavigationDrawer() {
        mainActivityViewModel.getMainLocationLive().observe(this) { mainLocation ->
            if (mainLocation != null) {
                val header = binding.navigationView.getHeaderView(0)
                val cityMainLocation = header.findViewById<TextView>(R.id.tvCityMainLocation)
                cityMainLocation.text = mainLocation.name
            } else {
                Toast.makeText(
                    applicationContext,
                    "Click on the add button to add a main location",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun infoButtonHeaderInfoDialog() {
        val header = binding.navigationView.getHeaderView(0)
        val infoButton = header.findViewById<ImageView>(R.id.ivInfoButton)
        infoButton.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setMessage("Click on a city to delete it or make it your main location")
            alertDialogBuilder.setPositiveButton("Ok") { dialog, _ ->
                dialog.cancel()
            }
            val alterDialog = alertDialogBuilder.create()
            alterDialog.show()
        }
    }

    private fun configureNavigationDrawerMenu() {
        binding.navigationView.menu.addSubMenu("Other Locations")

        mainActivityViewModel.getOtherLocations().observe(this) { listOfCities ->
            binding.navigationView.menu.clear()
            binding.navigationView.menu.addSubMenu("Other Locations")
            mainActivityViewModel.getListOfRemoteForecastWeathersByCity(listOfCities)

            mainActivityViewModel.listOfResponses.observe(this){ listOfResponses ->

                listOfCities.forEachIndexed { index, city ->
                    binding.navigationView.menu[0].subMenu.add(city.name).setActionView(R.layout.drawer_menu_image_temperature)
                    val imageView = binding.navigationView.menu[0].subMenu[index].actionView.findViewById<ImageView>(R.id.cityWeatherImage)
                    Glide.with(applicationContext).load("https:${listOfResponses[index].body()!!.current.condition.icon}")
                        .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView)
                    val textTemperature = binding.navigationView.menu[0].subMenu[index].actionView.findViewById<TextView>(R.id.temperature)
                    textTemperature.text = "${listOfResponses[index].body()!!.current.temp_c.toInt()}°"
                    binding.navigationView.menu[0].subMenu[index].setOnMenuItemClickListener { cityName ->
                        Toast.makeText(applicationContext, "You have selected $cityName", Toast.LENGTH_SHORT).show()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        val action = MainFragmentDirections.actionMainFragmentToDetailFragment(cityName.toString())
                        //navController.navigate(action)
                        findNavController(R.id.fragmentContainerView).navigate(action)

//                        val city = mainActivityViewModel.searchCityObjectInDB(item.toString())
//                        mainActivityViewModel.insertCityResettingMainLocation(city)
                        // make searchCityObject suspend, observe it from the UI, update new main location
                        true
                    }
                }
            }
        }
    }

    private fun configureNavController(): NavController {
        return findNavController(R.id.fragmentContainerView)
    }

    private fun configureActionBarDependingOnDestination(nd: NavDestination) {
        if (nd.id == R.id.mainFragment || nd.id == R.id.detailFragment) {
            binding.appBarMain.toolbarMainActivity.isVisible = true
            //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        if (nd.id == R.id.addFragment) {
            binding.appBarMain.toolbarMainActivity.isVisible = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.add_menu_main_fragment, menu)
        return true
    }

    private fun setToolbarItemListener() {
        binding.appBarMain.toolbarMainActivity.setOnMenuItemClickListener { menuItem ->
            navigateToAddFragment(menuItem)
        }
    }

    private fun navigateToAddFragment(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_add -> {
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
            R.id.addFragment -> {
                super.onBackPressed()
            }
        }
    }

    private fun statusBarListener(){
        val decorView = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                val timer = object: CountDownTimer(1000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                    }
                }
                timer.start()
            }
        }
    }

}
package com.example.weatherlicious

import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.weatherlicious.databinding.ActivityMainBinding
import com.example.weatherlicious.ui.mainfragment.MainFragment
import com.example.weatherlicious.ui.mainfragment.MainFragmentDirections
import com.example.weatherlicious.util.Constants
import com.example.weatherlicious.util.dialog.DeleteOrMakeMainLocationDialog
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.lang.IndexOutOfBoundsException


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private var navDestination: NavDestination? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private val TAG = "Main Activity"

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

        configureHeaderNavigationDrawer()
        configureNavigationDrawerMenu()
        infoButtonHeaderInfoDialog()
    }

    private fun configureHeaderNavigationDrawer() {
        Log.i(TAG, "Begin configureHeaderNavigationDrawer()")
        mainActivityViewModel.getRemoteForecastWeatherByCity()
        mainActivityViewModel.getMainLocationLive().observe(this) { mainLocation ->
            Log.i(TAG, "Begin of the observation of main location in configureHeaderNavigationDrawer()")
            if (mainLocation != null) {
                val header = binding.navigationView.getHeaderView(0)
                val cityNameMainLocation = header.findViewById<TextView>(R.id.tvCityMainLocation)
                val imageWeatherMainLocation = header.findViewById<ImageView>(R.id.ivMainLocation)
                val temperatureWeatherMainLocation =
                    header.findViewById<TextView>(R.id.tvTemperature)
                cityNameMainLocation.text = mainLocation.name
                //mainActivityViewModel.getRemoteForecastWeatherByCity()
                var mainLocationImageUrl = "//cdn.weatherapi.com/weather/64x64/day/113.png"
                var mainLocationTemperature = ""
                mainActivityViewModel.remoteForecastWeatherByCity.observe(
                    this,
                    Observer { remoteForecastWeather ->
                        mainLocationImageUrl =
                            remoteForecastWeather.data?.current?.condition?.icon.toString()
                        mainLocationTemperature =
                            remoteForecastWeather.data?.current?.temp_c?.toInt().toString()
                    })
                Glide.with(applicationContext).load("https:${mainLocationImageUrl}").centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageWeatherMainLocation)
                temperatureWeatherMainLocation.text = mainLocationTemperature
            } else {
                Toast.makeText(
                    applicationContext,
                    "Click on the add button to add a main location",
                    Toast.LENGTH_LONG
                ).show()
            }
            Log.i(
                TAG,
                "End of the observation of main location in configureHeaderNavigationDrawer()"
            )
        }
        Log.i(TAG, "End configureHeaderNavigationDrawer()")
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
        //binding.navigationView.menu.addSubMenu("Other Locations")
        val deleteOrMakeMainLocationDialog = DeleteOrMakeMainLocationDialog(this, mainActivityViewModel)

        mainActivityViewModel.getNotMainLocations().observe(this) { listOfCities ->
//            binding.navigationView.menu.clear()
//            binding.navigationView.menu.addSubMenu("Other Locations")
            mainActivityViewModel.getListOfRemoteForecastWeathersByCity(listOfCities)

            mainActivityViewModel.listOfResponses.observe(this) { listOfResponses ->
                listOfCities.forEachIndexed { index, city ->
//                    try {
//                        Log.i(TAG, "Index = $index")
//                        binding.navigationView.menu[Constants.FIRST_MENU_ITEM].subMenu.add(city.name)
//                            .setActionView(R.layout.drawer_menu_image_temperature)
//                        val imageView =
//                            binding.navigationView.menu[Constants.FIRST_MENU_ITEM].subMenu[index].actionView.findViewById<ImageView>(
//                                R.id.cityWeatherImage
//                            )
//                        Glide.with(applicationContext)
//                            .load("https:${listOfResponses[index].body()!!.current.condition.icon}")
//                            .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
//                            .into(imageView)
//                        val textTemperature =
//                            binding.navigationView.menu[Constants.FIRST_MENU_ITEM].subMenu[index].actionView.findViewById<TextView>(
//                                R.id.temperature
//                            )
//                        textTemperature.text =
//                            "${listOfResponses[index].body()!!.current.temp_c.toInt()}°"
//                        binding.navigationView.menu[Constants.FIRST_MENU_ITEM].subMenu[index].setOnMenuItemClickListener { cityName ->
//                            drawerLayout.closeDrawer(GravityCompat.START)
//                            val action =
//                                MainFragmentDirections.actionMainFragmentToDetailFragment(cityName.toString())
//                            findNavController(R.id.fragmentContainerView).navigate(action)
//
////                        val city = mainActivityViewModel.searchCityObjectInDB(item.toString())
////                        mainActivityViewModel.insertCityResettingMainLocation(city)
//                            // make searchCityObject suspend, observe it from the UI, update new main location
//                            true
//                        }
//                    } catch (e: IndexOutOfBoundsException) {
//                        e.printStackTrace()
//                    }
                    try {
                        binding.navigationView.menu[Constants.FIRST_MENU_ITEM].subMenu.getItem(index).isVisible = true
//                        binding.navigationView.menu.getItem(index).isVisible = true
                        binding.navigationView.menu[Constants.FIRST_MENU_ITEM].subMenu.getItem(index).title = city.name
                        binding.navigationView.menu[Constants.FIRST_MENU_ITEM].subMenu.getItem(index).setActionView(R.layout.drawer_menu_image_temperature)
                        val imageView = binding.navigationView.menu[Constants.FIRST_MENU_ITEM].subMenu.getItem(index).actionView.findViewById<ImageView>(R.id.cityWeatherImage)
                        Glide.with(applicationContext)
                            .load("https:${listOfResponses[index].body()!!.current.condition.icon}")
                            .centerCrop().transition(DrawableTransitionOptions.withCrossFade())
                            .into(imageView)
                        val textTemperature = binding.navigationView.menu[Constants.FIRST_MENU_ITEM].subMenu.getItem(index).actionView.findViewById<TextView>(R.id.temperature)
                        textTemperature.text = "${listOfResponses[index].body()!!.current.temp_c.toInt()}°"
                        binding.navigationView.menu[Constants.FIRST_MENU_ITEM].subMenu.getItem(index).setOnMenuItemClickListener { cityName ->
                            drawerLayout.closeDrawer(GravityCompat.START)
                            deleteOrMakeMainLocationDialog.createDeleteOrMakeMainLocationDialog(navController, city, this, intent)
                            true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
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
                if (navDestination?.id == R.id.mainFragment) navController.navigate(R.id.action_mainFragment_to_addFragment)
                else if (navDestination?.id == R.id.detailFragment) navController.navigate(R.id.action_detailFragment_to_addFragment)
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
            R.id.detailFragment -> {
                super.onBackPressed()
            }
        }
    }

    /**
     * listen to the status of the status bar and hides it after 1 second
     */
    private fun statusBarListener() {
        val decorView = window.decorView
        decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                val timer = object : CountDownTimer(1000, 1000) {
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
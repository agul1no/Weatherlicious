package com.example.weatherlicious.ui.addfragment

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherlicious.R
import com.example.weatherlicious.databinding.FragmentAddBinding
import com.example.weatherlicious.ui.mainfragment.RemoteWeatherForecastAdapterDaily
import com.example.weatherlicious.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterCitySearch: CitySearchAdapter

    private val addFragmentViewModel: AddFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        createMenuAddIcon()
        initializeRecyclerView()
        observeListOfCities()
        return binding.root
    }

    private fun initializeRecyclerView(): RecyclerView {
        adapterCitySearch = CitySearchAdapter()
        binding.recyclerView.adapter = adapterCitySearch
        return binding.recyclerView
    }

    private fun observeListOfCities(){
        addFragmentViewModel.citySearch.observe(viewLifecycleOwner){ response ->
            when(response){
                is Resource.Success -> {
                    hideProgressbar()
                    response.data?.let { listOfCityItems ->
                        adapterCitySearch.submitList(listOfCityItems)
                    }
                }
                is Resource.Error -> {
                    hideProgressbar()
                    response.message?.let { message ->
                        Toast.makeText(context, "An error occurred: $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressbar()
                }
            }
        }
    }

    private fun createMenuAddIcon(){
        activity?.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu_add_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val searchView = menuItem.actionView as SearchView


                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        //addFragmentViewModel.checkQueryIsEmpty(query)
                        return true
                    }
                    override fun onQueryTextChange(query: String?): Boolean {
                        addFragmentViewModel.checkQueryIsEmpty(query)
                        return true
                    }
                })
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu_add_fragment, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //addFragmentViewModel.checkQueryIsEmpty(query, searchView)
                return true
            }
            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }
        })
    }*/

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun hideProgressbar(){
        binding.progressBar.isVisible = false
    }

    private fun showProgressbar(){
        binding.progressBar.isVisible = true
    }

}
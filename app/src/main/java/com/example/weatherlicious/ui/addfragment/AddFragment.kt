package com.example.weatherlicious.ui.addfragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.weatherlicious.R
import com.example.weatherlicious.databinding.FragmentAddBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        createMenuAddIcon()
        return binding.root
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
                        //addFragmentViewModel.checkQueryIsEmpty(query, searchView)
                        return true
                    }
                    override fun onQueryTextChange(query: String?): Boolean {
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

    //this method should be implemented in the AddFragmentViewModel
    private fun checkQueryIsEmpty(query: String?, searchView: SearchView){
        if (query?.isEmpty() == true){
            //binding.rvGallery.isVisible = false
            //binding.tvNoQuery.isVisible = true
        }
        if (query?.isNotEmpty() == true){
            //binding.rvGallery.scrollToPosition(0)
            //viewModel.searchImages(query.toString())
            //binding.tvNoQuery.isVisible = false
            //searchView.clearFocus()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
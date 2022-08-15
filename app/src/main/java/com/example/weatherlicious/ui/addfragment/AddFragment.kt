package com.example.weatherlicious.ui.addfragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
import com.example.weatherlicious.data.source.local.entities.City
import com.example.weatherlicious.databinding.FragmentAddBinding
import com.example.weatherlicious.util.dialog.MainLocationDialog
import com.example.weatherlicious.util.OnItemClickListener
import com.example.weatherlicious.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFragment : Fragment(), OnItemClickListener {

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
        initializeRecyclerView()
        observeListOfCities()
        editTextChangeListener()
        return binding.root
    }

    private fun initializeRecyclerView(): RecyclerView {
        adapterCitySearch = CitySearchAdapter(this)
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

    private fun editTextChangeListener(){
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                binding.recyclerView.isVisible = text.toString().isNotEmpty()
                addFragmentViewModel.checkQueryIsEmpty(text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // left empty because it is not going to be used or needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // left empty because it is not going to be used or needed
            }
        })
    }

    override fun onItemClick(position: Int) {
        val currentCityItem = adapterCitySearch.getCityItemAtPosition(position)
        createAlertDialog(currentCityItem)
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
//        val action = AddFragmentDirections.actionAddFragmentToMainFragment()
//        findNavController().navigate(action)
    }

    private fun createAlertDialog(currentCityItem: CityItem){
        val mainLocationDialog = MainLocationDialog(context!!, addFragmentViewModel)
        mainLocationDialog.createMainLocationAlterDialog(currentCityItem, findNavController())
    }

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
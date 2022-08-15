package com.example.weatherlicious.ui.addfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherlicious.data.model.searchautocomplete.CityItem
import com.example.weatherlicious.databinding.CityItemBinding
import com.example.weatherlicious.util.OnItemClickListener

class CitySearchAdapter(
    private val onItemClickListener: OnItemClickListener
    ) : ListAdapter<CityItem, CitySearchAdapter.ViewHolder>(CityItemDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitySearchAdapter.ViewHolder {
        val view = CityItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cityItem = getItem(position)
        holder.bind(cityItem)
    }

    fun getCityItemAtPosition(position: Int): CityItem {
        return getItem(position)
    }

    inner class ViewHolder(
        private val itemBinding: CityItemBinding
    ): RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {

        fun bind(cityItem: CityItem){
            itemBinding.apply {
                tvCityName.text = cityItem.name
                tvCountryName.text = "${cityItem.region}, ${cityItem.country}"
            }
        }

        init {
            itemBinding.root.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                onItemClickListener.onItemClick(position)
            }
        }
    }

    class CityItemDiffCallBack : DiffUtil.ItemCallback<CityItem>() {
        override fun areItemsTheSame(oldItem: CityItem, newItem: CityItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CityItem, newItem: CityItem): Boolean {
            return oldItem == newItem
        }
    }
}
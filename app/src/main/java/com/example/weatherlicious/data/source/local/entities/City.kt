package com.example.weatherlicious.data.source.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_table")
data class City(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    @ColumnInfo(name = "preferred_location")
    val preferredLocation: Int
)

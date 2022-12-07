package com.example.mercaditonld2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ProductEntity")
data class ProductEntity(@PrimaryKey var id:String = "",
                         var title:String = "",
                         var price:Double = 0.0,
                         var description: String = "",
                         var photoUrl: String = "")


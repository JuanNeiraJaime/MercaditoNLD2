package com.example.mercaditonld2

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Product(@get:Exclude var id:String = "",
                   var title: String = "",
                   var description: String = "",
                   var price: Double = 0.0,
                   var photoUrl: String = "")
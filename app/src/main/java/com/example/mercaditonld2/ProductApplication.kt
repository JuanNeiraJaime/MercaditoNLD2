package com.example.mercaditonld2

import android.app.Application
import androidx.room.Room

class ProductApplication: Application() {
    companion object{
        lateinit var database: ProductDatabase
        lateinit var contactApi: ContactAPI
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this,
            ProductDatabase::class.java,
            "ProductDatabase")
            .build()
        contactApi = ContactAPI.getInstance(this)
    }
}
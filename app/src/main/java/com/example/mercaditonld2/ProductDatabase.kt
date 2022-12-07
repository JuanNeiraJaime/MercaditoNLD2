package com.example.mercaditonld2

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = arrayOf(ProductEntity::class), version = 2)
abstract class ProductDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDao
}

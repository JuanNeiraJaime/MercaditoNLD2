package com.example.mercaditonld2

import androidx.room.*

@Dao
interface ProductDao {
    @Query("SELECT * FROM ProductEntity") //Visualizar las tiendas almacenadas
    fun getAllProducts(): MutableList<ProductEntity>

    @Query("SELECT * FROM ProductEntity WHERE id = :id")
    fun getProductById(id: Long): ProductEntity

    @Insert
    fun addProduct(productEntity: ProductEntity)

    @Update
    fun updateProduct(productEntity: ProductEntity)

    @Delete
    fun deleteProduct(productEntity: ProductEntity)
}

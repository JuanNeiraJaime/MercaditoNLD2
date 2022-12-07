package com.example.mercaditonld2

interface OnClickListener {
    fun onClick(storeId: Long)
    fun onFavoriteStore(storeEntity: ProductEntity)
    fun onDeleteStore(storeEntity: ProductEntity)
}
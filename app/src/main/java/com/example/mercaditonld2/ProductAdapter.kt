package com.example.mercaditonld2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mercaditonld2.databinding.ItemProductBinding

class ProductAdapter(private var products: MutableList<ProductEntity>/*, private var listener: OnClickListener*/):
    RecyclerView.Adapter<ProductAdapter.ViewHolder>(){

    //Para saber cual es la pantalla en la que estoy trabajando. Es la pantalla de origen de donde partira una accion
    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val view = LayoutInflater.from(mContext).inflate(R.layout.item_product, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products.get(position)

        with(holder){
            setListener(product)

            binding.tvTitle.text = product.title
            binding.tvPrice.text = product.price.toString()

            com.bumptech.glide.Glide.with(mContext)
                .load(product.photoUrl)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.imgPhoto)
        }
    }

    override fun getItemCount(): Int = products.size

    fun setProducts(products: MutableList<ProductEntity>) {
        this.products = products
        notifyDataSetChanged()//Notificar que hubo un cambio

    }

    fun add(productEntity: ProductEntity) {
        if(!products.contains(productEntity)){
            products.add((productEntity))
            //Notificar al adaptador que refresque la lista
            //notifyDataSetChanged()
            notifyItemInserted(products.size-1)
        }
    }

    fun update(productEntity: ProductEntity) {
        val index = products.indexOf(productEntity) //Se modifico el metodo equals para comparar solamente  con id
        if(index != -1) //Sifnifica que lo encontro
        {
            products.set(index, productEntity)
            notifyItemChanged(index)//Actualizar solamente el elemento actualizado en la tabla y no toda la tabla
        }
    }

    fun delete(productEntity: ProductEntity) {
        val index = products.indexOf(productEntity)
        if(index != -1) //Sifnifica que lo encontro
        {
            products.removeAt(index)
            notifyItemRemoved(index)//Actualizar solamente el elemento borrado en la tabla
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val binding = ItemProductBinding.bind(view)

        fun setListener(productEntity: ProductEntity){
            binding.root.setOnLongClickListener {
                //listener.onDeleteProduct(productEntity)
                true
            }
            binding
        }
    }
}
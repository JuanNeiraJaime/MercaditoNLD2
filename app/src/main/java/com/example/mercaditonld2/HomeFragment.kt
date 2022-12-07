package com.example.mercaditonld2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.mercaditonld2.databinding.FragmentHomeBinding
import com.example.mercaditonld2.databinding.ItemProductBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mFireBaseAdapter: FirebaseRecyclerAdapter<Product, ProductHolder>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val query = FirebaseDatabase.getInstance().reference.child("products")

        val options =
        FirebaseRecyclerOptions.Builder<Product>().setQuery(query, SnapshotParser {
            val snapshot = it.getValue(Product::class.java)
            snapshot!!.id = it.key!!
            snapshot
        }).build()
        //FirebaseRecyclerOptions.Builder<Product>()
        //.setQuery(query,Product::class.java).build()

        mFireBaseAdapter = object : FirebaseRecyclerAdapter<Product, ProductHolder>(options){

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
                mContext = parent.context

                val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_product, parent, false)
                return ProductHolder(view)
            }

            override fun onBindViewHolder(holder: ProductHolder, position: Int, model: Product) {
                val product = getItem(position)

                with(holder){
                    setListener(product)
                    binding.tvTitle.text = product.title
                    binding.tvPrice.text = product.price.toString()
                    Glide.with(mContext)
                        .load(product.photoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(binding.imgPhoto)
                }
            }

            override fun onDataChanged() {
                super.onDataChanged()
                mBinding.progressBar.visibility = View.GONE
                notifyDataSetChanged()
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                Toast.makeText(mContext, error.message, Toast.LENGTH_SHORT).show()
            }
        }
        mLayoutManager = LinearLayoutManager(context)
        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = mFireBaseAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        mFireBaseAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mFireBaseAdapter.stopListening()
    }

    inner class ProductHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = ItemProductBinding.bind(view)

        fun setListener(product: Product){
            binding.root.setOnLongClickListener{
                deleteProduct(product)
                true
            }
        }
    }
    private fun deleteProduct(product: Product){
        MaterialAlertDialogBuilder(mContext)
            .setTitle("Esta seguro que dese eliminar el producto ${product.title}?")
            .setPositiveButton(R.string.dialog_delete_confirm, { dialogInterface, i ->

                val databaseReference = FirebaseDatabase.getInstance().reference.child("products")
                databaseReference.child(product.id).removeValue()
            })
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()




    }
}
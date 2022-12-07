package com.example.mercaditonld2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mercaditonld2.databinding.FragmentEditProductBinding
import com.example.mercaditonld2.databinding.FragmentHistoryBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class HistoryFragment : Fragment() {

    private lateinit var mAdapter:ProductAdapter
    private lateinit var mBinding: FragmentHistoryBinding
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHistoryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        mAdapter = ProductAdapter(mutableListOf())
        //Cuantas columnas queremos en el grid
        mLayoutManager = LinearLayoutManager(context)
        getProducts()

        mBinding.recyclerView.apply {
            //Indicar que no cambiara de tamano y puede optimizar recursos
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            adapter = mAdapter
        }
    }

    private fun getProducts(){
        doAsync {
            val products = ProductApplication.database.productDao().getAllProducts()
            uiThread {
                mAdapter.setProducts(products)
                mBinding.progressBar.visibility = View.GONE
            }
        }
    }
}
package com.example.mercaditonld2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.mercaditonld2.databinding.FragmentContactBinding
import com.example.mercaditonld2.databinding.FragmentEditProductBinding
import java.io.Console

class ContactFragment : Fragment() {
    private lateinit var mBinding: FragmentContactBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentContactBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ConectApi()
    }

    private fun ConectApi(){
        val url = "https://contact.free.beeceptor.com/contacts"
        //val queue = Volley.newRequestQueue(this)
        val jsonObjectResquest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            mBinding.tvNombre.text = response["nombre"].toString()
            mBinding.tvEmail.text = response["email"].toString()
            mBinding.tvTelefono.text = response["telefono"].toString()
        },{
            it.printStackTrace()
        })
        ProductApplication.contactApi.addToRequestQueue(jsonObjectResquest)
    }

}
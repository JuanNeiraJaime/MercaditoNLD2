package com.example.mercaditonld2

import android.app.Activity
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.icu.text.CaseMap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.example.mercaditonld2.databinding.FragmentEditProductBinding
import com.example.mercaditonld2.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EditProductFragment : Fragment() {

    private val RC_GALLERY = 18
    private val PATH_PRODUCT = "products"

    private lateinit var mBinding: FragmentEditProductBinding
    private lateinit var mStorageReference: StorageReference
    private lateinit var mDatabaseReference: DatabaseReference

    private var mPhotoSelectedUri: Uri? = null
    private var mActivity: MainActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentEditProductBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.imgPhoto.setOnClickListener { openGallery() }

        mStorageReference = FirebaseStorage.getInstance().reference
        mDatabaseReference = FirebaseDatabase.getInstance().reference.child(PATH_PRODUCT)
        setupActionBar()
    }

    private fun openGallery() {
        //Abrir galeria
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == RC_GALLERY){
                mPhotoSelectedUri = data?.data
                mBinding.imgPhoto.setImageURI(mPhotoSelectedUri)
                mBinding.tvStatusPhoto.text = "Se subio la foto con exito"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            android.R.id.home ->{
                mActivity?.onBackPressed()
                true
            }
            R.id.action_save ->{
                postProduct()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun postProduct() {
        mBinding.progressBar.visibility = View.VISIBLE
        val key = mDatabaseReference.push().key!!//Generar nuevo nodo en bd
        val storageReference = mStorageReference.child(PATH_PRODUCT)
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child(key)
        if(mPhotoSelectedUri != null){
            storageReference.putFile(mPhotoSelectedUri!!)
                .addOnProgressListener {
                    val progress = (100 * it.bytesTransferred/it.totalByteCount).toDouble()
                    mBinding.progressBar.progress = progress.toInt()
                }
                .addOnSuccessListener {
                    Snackbar.make(mBinding.root, "Foto publicada",
                    Snackbar.LENGTH_SHORT).show()
                    it.storage.downloadUrl.addOnSuccessListener {
                        saveProduct(key, it.toString(),
                            mBinding.etTitle.text.toString().trim(),
                            mBinding.etDescription.text.toString().trim(),
                            mBinding.etPrice.text.toString().toDouble())
                        //Shared Preference
                        mActivity?.updateSP()
                    }
                }
                .addOnFailureListener {
                    Snackbar.make(mBinding.root, "No se pudo subir",
                        Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProduct(key: String, url: String, title: String, description: String, price:Double) {
        val product = Product(title = title, photoUrl = url, description = description, price = price)
        mDatabaseReference.child(key).setValue(product)

        //Guardar en BD Local
        val productEntity = ProductEntity(id = key, title = title, price = price, description = description, photoUrl = url)
        Thread{
            ProductApplication.database.productDao().addProduct(productEntity)
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupActionBar() {
        //obtenemos la actividad en la que esta alojada el fragmento y casteamos a mainactivity
        mActivity = activity as? MainActivity
        //Agregar boton arriba para retroceder
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Cambiamos el texto de arriba
        mActivity?.supportActionBar?.title = getString(R.string.edit_store_title_edit)

        setHasOptionsMenu(true)//Darle acceso al menu
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)//Desvincular menu
        mActivity?.supportActionBar?.title=getString(R.string.app_name) //Restaurar titulo
        //Se implementa una interface como una forma de obtener acceso a componentes del activity padre
        //desde el fragment
        mActivity?.hideFab(true)
        mActivity?.hideBottomNav(true)

        setHasOptionsMenu(false)
        super.onDestroy()
    }
}
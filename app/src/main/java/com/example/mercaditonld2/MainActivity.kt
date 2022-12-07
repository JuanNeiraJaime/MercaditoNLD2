package com.example.mercaditonld2

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mercaditonld2.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity(), MainAux {
    private val RC_SIGN_IN = 21
    private lateinit var mBinding: ActivityMainBinding

    private lateinit var  mActiveFragment: Fragment
    private lateinit var mFragmentManager: FragmentManager

    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private var mFirebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupAuth()
        setupBottomNav()
        sharedPreferences()
        mBinding.fab.setOnClickListener{launchEditFragment()}
    }

    override fun onResume() {
        super.onResume()
        mFirebaseAuth?.addAuthStateListener(mAuthListener)
    }

    override fun onPause() {
        super.onPause()
        mFirebaseAuth?.removeAuthStateListener(mAuthListener)
    }

    override fun onDestroy() {
        this?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            if(resultCode != RESULT_OK){
                if(IdpResponse.fromResultIntent(data) == null){
                    finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_logout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            R.id.action_logout -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        this?.let {
            AuthUI.getInstance().signOut(it)
                .addOnCompleteListener {
                    Toast.makeText(this, "Hasta pronto...", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if(user == null){
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(
                        Arrays.asList(AuthUI.IdpConfig.EmailBuilder().build(),
                            AuthUI.IdpConfig.GoogleBuilder().build())
                    )
                    .build(), RC_SIGN_IN)
            }
        }
    }

    private fun sharedPreferences() {
        //SHARED PREFERENCES
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val isFirstTime = preferences.getBoolean(getString(R.string.sp_first_time), true)
        Log.i("SP","${getString(R.string.sp_first_time)} = $isFirstTime")


        //Al inicio de la aplicacion si el usuario no presiona el boton confirmar sequira saliendo este dialogo
        if(isFirstTime){
            MaterialAlertDialogBuilder(this)
                .setTitle("Bienvenido!")
                .setMessage("Bienvenido por primera ves a nuestra aplicacion, para usar nuestra aplicacion ingresa un producto")
                .setPositiveButton("Agregar Producto", { dialogInterface, i ->
                    launchEditFragment()
                })
                .setNegativeButton("Cancelar",null)
                .show()
        }
        else{
            val nombre = FirebaseAuth.getInstance().currentUser?.displayName
            Toast.makeText(this, "Bienvenido de nuevo $nombre", Toast.LENGTH_LONG).show()
        }
    }

    private fun launchEditFragment(args: Bundle? = null) {
        //Crear instancia del fragmento que queremos lanzar
        val fragment = EditProductFragment()

        if(args != null) fragment.arguments = args
        //Implementar fragment manager:Gestor para contrlar los fragmentos
        val fragmentManager = supportFragmentManager
        //Fragment transaction: Es quien va a decidir como se va a ejecutar
        val fragmentTransition = fragmentManager.beginTransaction()

        //Que fragmento y en donde
        fragmentTransition.add(R.id.hostFragment, fragment)
        fragmentTransition.addToBackStack(null)//Para retroceder al activity principal
        fragmentTransition.commit()//Aplicar los cambios

        hideFab()
        hideBottomNav()
    }

    private fun setupBottomNav(){
        mFragmentManager = supportFragmentManager

        val homeFragment = HomeFragment()
        val historyFragment = HistoryFragment()
        val contactFragment = ContactFragment()

        mActiveFragment = homeFragment

        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, contactFragment, ContactFragment::class.java.name)
            .hide(contactFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, historyFragment, HistoryFragment::class.java.name)
            .hide(historyFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, homeFragment, HomeFragment::class.java.name).commit()

        mBinding.bottomNav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> {
                    //Ocultas el fragmento actual y muestras el nuevo y actualizas el activo
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(homeFragment).commit()
                    mActiveFragment = homeFragment
                    true
                }
                R.id.action_history -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(historyFragment).commit()
                    mActiveFragment = historyFragment
                    true
                }
                R.id.action_contact -> {
                    mFragmentManager.beginTransaction().hide(mActiveFragment).show(contactFragment).commit()
                    mActiveFragment = contactFragment
                    true
                }
                else -> false
            }
        }
    }

    override fun hideFab(isVisible: Boolean) {
        if(isVisible){
            mBinding.fab.show()
        }else{
            mBinding.fab.hide()
        }
    }

    override fun hideBottomNav(isVisible: Boolean) {
        if(isVisible){
            mBinding.bottomNav.visibility = View.VISIBLE
        }else{
            mBinding.bottomNav.visibility = View.GONE
        }
    }

    //Actualiza la bandera guardada en shared preferences
    override fun updateSP() {
        val preferences = getPreferences(Context.MODE_PRIVATE)
        preferences.edit().putBoolean(getString(R.string.sp_first_time), false).commit()
    }
}
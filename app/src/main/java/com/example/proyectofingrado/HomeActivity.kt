package com.example.proyectofingrado

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyectofingrado.databinding.ActivityAuthBinding
import com.example.proyectofingrado.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

/*Creo una clase enumerada para referenciar las diferentes opciones de autentificación de usuario,
* que en este caso son correo electrónico y google*/
enum class Proveedor{
    CORREO,
    GOOGLE
}


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val proveedor = bundle?.getString("proveedor")

        setup(email ?:"",proveedor ?:"")

        binding.logOutButton.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            //Antes de salir, tenemos que asegurarnos de borrar los datos de usuario almancenados en sharedPreferences
            val datos_usuario = getSharedPreferences(getString(R.string.archivo_login), Context.MODE_PRIVATE).edit()
            datos_usuario.clear()
            datos_usuario.apply()

            onBackPressed()
        }


        /*El código siguiente almacenará los datos de sesión de usuario para evitar que se tenga que loguear de nuevo
        * a través de almacenamiento de datos de tipo clave-valor. Lo pongo en modo edición para almacenar a los usuarios
        * que entren en la app*/
        val datos_almacenados = getSharedPreferences(getString(R.string.archivo_login), Context.MODE_PRIVATE).edit()
        datos_almacenados.putString("email",email)
        datos_almacenados.putString("proveedor",proveedor)
        datos_almacenados.apply()

    }//Fin de método onCreate

    private fun setup(email:String,provider:String){
        title = "Inicio"
        binding.texto1.text = email
        binding.texto2.text = provider

    }





}
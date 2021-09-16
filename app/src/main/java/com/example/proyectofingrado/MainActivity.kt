package com.example.proyectofingrado

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.proyectofingrado.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Añado al banding el layout de ActivityMainBanding a través del inflate.
        binding = ActivityMainBinding.inflate(layoutInflater)

        /*Muestro en el layout desde la raíz, de esta manera no me hará falta unir cada elemento
        * de la clase al layout, solo me hará falta usar la variable binding, que es donde están
        * almacenados todos los datos de la ActivityMain a través de la clase ActivityMainBinding */
        setContentView(binding.root)

        binding.botonPrincipalEntrar.setOnClickListener{
            val auth_intent = Intent(this, AuthActivity::class.java)
            startActivity(auth_intent)
        }







    }//Fin del método onCreate





}
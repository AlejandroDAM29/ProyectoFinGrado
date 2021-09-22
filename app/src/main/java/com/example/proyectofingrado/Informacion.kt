package com.example.proyectofingrado

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Informacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_informacion)

        //Le pongo el título a la ventana de información
        title="Informacion"
    }
}
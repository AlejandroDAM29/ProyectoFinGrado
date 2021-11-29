package com.example.proyectofingrado.PresentationDomain

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.proyectofingrado.Interfaces.FillCaloricTable
import com.example.proyectofingrado.R
import com.google.firebase.auth.FirebaseAuth

class CaloricTable : AppCompatActivity(), FillCaloricTable {

    lateinit var caloricList:ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caloric_table)

        //Activo el toolbar en la activity
        toolbar = findViewById(R.id.tool_bar4)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)


        val arrayAdapter:ArrayAdapter<*>

        caloricList = findViewById(R.id.caloricList)
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,FillList())
        caloricList.adapter = arrayAdapter
    }



    //Creación del menú en el activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_en_calorictable,menu)
        return true
    }


    //Método para seleccionar los items del menú toolbar y ejecutar su acción
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        /*La id que almaceno aquí equivale a una de las elegidas dentro del menú.xml que se
         ha creado en la carpeta menú*/
        val elegida = item.itemId

        //Opciones posibles
        if (elegida == R.id.informacion){
            var ir_info = Intent(this, Informacion::class.java)
            startActivity(ir_info)
        }

        if (elegida == R.id.cerrar_sesion){

            //salir de la sesión dee Firebase
            FirebaseAuth.getInstance().signOut()

            //Antes de salir, tenemos que asegurarnos de borrar los datos de usuario almancenados en sharedPreferences
            val datos_usuario = getSharedPreferences(getString(R.string.archivo_login), Context.MODE_PRIVATE).edit()
            datos_usuario.clear()
            datos_usuario.apply()

            //Ir a la pantalla de anterior para salir de la sesión de usuario
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }


        //Si ha habido un error, retornará la elección padre por defecto
        return super.onOptionsItemSelected(item)

    }//Fin del método onOptionsItemSelected






}
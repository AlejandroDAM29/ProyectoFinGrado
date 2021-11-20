package com.example.proyectofingrado.Services

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.proyectofingrado.DataClases.User
import com.example.proyectofingrado.PresentationDomain.DatosUsuario
import com.example.proyectofingrado.PresentationDomain.HttpURI
import org.json.JSONException
import org.json.JSONObject

interface UserRequest {




    /*Si el usuario existe en la base de datos MySql remota, se mostrará la receta. Si no está registrado se llevará
   * al usuario a la pantalla de registro de datos para ingresar sus datos en la base de datos*/
    private fun mostrarReceta(email:String) {

        //Cadena a ejecutar en el web Service
        val stringRequest: StringRequest = object : StringRequest(Request.Method.POST,
            HttpURI, Response.Listener { serverResponse ->
                //Este try es para recorrer el JSON
                try {
                    val  obj = JSONObject(serverResponse)
                    //Requerimos el nombre del objeto booleano. En el web-servie se llama error
                    var noExiste:Boolean = false;
                    noExiste = obj.getBoolean("noExiste")
                    var datosUsuario = User(obj.getString("email"),
                        obj.getString("peso"),
                        obj.getString("altura"),
                        obj.getString("sexo"),
                        obj.getString("actividad"),
                        obj.getString("edad"))


                    if(noExiste){
                        val intent = Intent(this.UserRequest, DatosUsuario::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(intent)
                    }else{
                        //TODO si el usuario no existe, hacer pruebas aqui. Viene de la opción login de php
                        Toast.makeText(this,datosUsuario?.email.toString(),
                            Toast.LENGTH_LONG).show()
                    }

                }catch (e: JSONException){
                    e.printStackTrace()
                }

            }, Response.ErrorListener {
                Toast.makeText(this,"Volley incorrecto",
                    Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String,String>{
                val parametros = HashMap<String,String>()
                parametros.put("email",email.toString())
                parametros.put("opcion","login")
                return parametros
            }
        }
        requesrQueue.add(stringRequest)
    }

}
package com.example.proyectofingrado.PresentationDomain

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.proyectofingrado.DataClases.Food
import com.example.proyectofingrado.Interfaces.CurrentTime
import com.example.proyectofingrado.R
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class SelectedFood : AppCompatActivity(), CurrentTime {

    lateinit var nombreComida:TextView
    lateinit var imagenComida: ImageView
    lateinit var ingredientesComida: TextView
    lateinit var recetaComida: TextView
    lateinit var recievedFood:Food
    val HttpURI = "https://alejandroexpdeveloper.com/usuario.php"
    lateinit var requesrQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_food)

        //Activo el toolbar en la activity
        toolbar = findViewById(R.id.tool_bar3)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        imagenComida = findViewById(R.id.imagenComida)
        nombreComida = findViewById(R.id.nombreComida)
        ingredientesComida = findViewById(R.id.ingredientesComida)
        recetaComida = findViewById(R.id.recetaComida)

        //Inicizalizamos a RequestQueue
        requesrQueue = Volley.newRequestQueue(this)
        //Consulta de datos en el servidor dependiendo de los datos introducidos por el usuario en el formulario
        consultaReceta()
    }

    private fun consultaReceta() {
        //Extraigo las calorias a consumir calculadas en la clase anterior.
        val bundle = intent.extras
        val caloriesToConsum = bundle?.getInt("caloriesToConsum")
        //Cadena a ejecutar en el web Service
        val stringRequest: StringRequest = object : StringRequest(Request.Method.POST,
            HttpURI, Response.Listener { serverResponse ->
                //Este try es para recorrer el JSON
                try {
                    //Cambiar Object por array
                    val obj = JSONObject(serverResponse)
                    recievedFood = Food(obj.getString("nombre"),
                                            obj.getString("calorias"),
                                            obj.getString("imagen"),
                                            obj.getString("ingredientes"),
                                            obj.getString("receta"),)

                    //Se introducen los datos obtenidos del servidor en el XML
                    insertDataXML(recievedFood)
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                Toast.makeText(this,"Volley incorrecto",
                    Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String,String>{
                val parametros = HashMap<String,String>()
                parametros.put("momento",calculateCurrentTime())//
                parametros.put("caloriesToConsum",caloriesToConsum.toString())
                parametros.put("opcion","login_alimento")
                return parametros
            }
        }
        requesrQueue.add(stringRequest)
    }

    //Establezco la imagen del alimento en el imageView
    fun setImage(imagen:String){
        Glide.with(this).load(imagen).into(imagenComida)
    }

    //Para introducir los datos del server en el XML
    fun insertDataXML(food:Food){
        nombreComida.text = recievedFood.nombre //Nombre del plato
        setImage(recievedFood.imagen) //Imagen del plato
        ingredientesComida.text = "Calorías: "+recievedFood.calorias+"\n- - - - - - - - - - - -\n"+recievedFood.ingredientes+"\n" +
                "- - - - - - - - - - - -\nPreparación:"
        recetaComida.text = recievedFood.receta
    }

    //Función para que el botón Back de android siempre te devuelva a la actividad Home
    override fun onBackPressed() {
        super.onBackPressed()
        //startActivity(Intent(this,HomeActivity::class.java))
    }

    //Creación del menú en el activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_en_selectedfood,menu)
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
    }

}
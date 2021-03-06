package com.example.proyectofingrado.PresentationDomain

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
                    val jsonObject = JSONObject(serverResponse)
                    val result = jsonObject.getJSONArray("response")
                    //RandomFood recieved
                    var randomFood = Random().nextInt(result.length())
                    val obj = result.getJSONObject(randomFood)

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
        if(imagen != null){
            Glide.with(this).load(imagen).into(imagenComida)
        }else{
            Glide.with(this).load(R.drawable.imagen_no_disponible).into(imagenComida)
        }

    }


    //Para introducir los datos del server en el XML
    fun insertDataXML(food:Food){
        nombreComida.text = recievedFood.nombre //Nombre del plato
        setImage(recievedFood.imagen) //Imagen del plato
        ingredientesComida.text = "Calor??as: "+recievedFood.calorias+"\n- - - - - - - - - - - -\n"+recievedFood.ingredientes+"\n" +
                "- - - - - - - - - - - -\nPreparaci??n:"
        recetaComida.text = recievedFood.receta
    }

    //Funci??n para que el bot??n Back de android siempre te devuelva a la actividad Home
    override fun onBackPressed() {
        super.onBackPressed()
        //startActivity(Intent(this,HomeActivity::class.java))
    }


    //Creaci??n del men?? en el activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_en_selectedfood,menu)
        return true
    }


    //M??todo para seleccionar los items del men?? toolbar y ejecutar su acci??n
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        /*La id que almaceno aqu?? equivale a una de las elegidas dentro del men??.xml que se
         ha creado en la carpeta men??*/
        val elegida = item.itemId

        //Opciones posibles
        if (elegida == R.id.informacion){
            var ir_info = Intent(this, Informacion::class.java)
            startActivity(ir_info)
        }

        if (elegida == R.id.cerrar_sesion){

            //salir de la sesi??n dee Firebase
            FirebaseAuth.getInstance().signOut()

            //Antes de salir, tenemos que asegurarnos de borrar los datos de usuario almancenados en sharedPreferences
            val datos_usuario = getSharedPreferences(getString(R.string.archivo_login), Context.MODE_PRIVATE).edit()
            datos_usuario.clear()
            datos_usuario.apply()

            //Ir a la pantalla de anterior para salir de la sesi??n de usuario
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }


        //Si ha habido un error, retornar?? la elecci??n padre por defecto
        return super.onOptionsItemSelected(item)

    }//Fin del m??todo onOptionsItemSelected





}
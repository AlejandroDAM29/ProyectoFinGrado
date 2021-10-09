package com.example.proyectofingrado

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyectofingrado.databinding.ActivityAuthBinding
import com.example.proyectofingrado.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var toolbar:Toolbar

    //Datos para el contacto con el web-service que gestiona la base de datos MySQL
    lateinit var requesrQueue: RequestQueue
    val HttpURI = "https://alejandroexpdeveloper.com/usuario.php"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Activo el toolbar en la activity
        toolbar = findViewById(R.id.tool_bar)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)



        val bundle = intent.extras
        val email = bundle?.getString("email")

        setup(email ?:"")



        /*El código siguiente almacenará los datos de sesión de usuario para evitar que se tenga que loguear de nuevo
        * a través de almacenamiento de datos de tipo clave-valor. Lo pongo en modo edición para almacenar a los usuarios
        * que entren en la app*/
        val datos_almacenados = getSharedPreferences(getString(R.string.archivo_login), Context.MODE_PRIVATE).edit()
        datos_almacenados.putString("email",email)
        datos_almacenados.apply()


        //Inicizalizamos a RequestQueue
        requesrQueue = Volley.newRequestQueue(this)

        val btn = findViewById<Button>(R.id.btnmostrar)
        btn.setOnClickListener {
            mostrarReceta()
        }






    }//Fin de método onCreate


    /*Si el usuario existe en la base de datos MySql remota, se mostrará la receta. Si no está registrado se llevará
    * al usuario a la pantalla de registro de datos para ingresar sus datos en la base de datos*/
    private fun mostrarReceta() {

        //Obtenemos el email para ver si está en la base de datos. Esto significaría que el usuario ya ha rellenado sus datos anteriormente
        val bundle = intent.extras
        val email = bundle?.getString("email")
        //Cadena a ejecutar en el web Service
        val stringRequest: StringRequest = object : StringRequest(Request.Method.POST,
            HttpURI, Response.Listener { serverResponse ->
                //Este try es para recorrer el JSON
                try {
                    val  obj = JSONObject(serverResponse)
                    //Requerimos el nombre del objeto booleano. En el web-servie se llama error
                    val noExiste:Boolean = obj.getBoolean("noExiste")

                    if(noExiste == true){
                        val intent = Intent(this, DatosUsuario::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(intent)
                    }else{
                        Toast.makeText(this,"Parece que ya existe humano",
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




    //Este método sirve para cambiar el título al toolbar y hacer que el texto identifique al correo entrante
  private fun setup(email:String){
        title = "El recetario"
        binding.texto1.text = "Bienvenido \n"+email

    }//Fin del método setup


    //Creación del menú en el activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_en_activity,menu)
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
            onBackPressed()
        }


        //Si ha habido un error, retornará la elección padre por defecto
        return super.onOptionsItemSelected(item)

    }//Fin del método onOptionsItemSelected





}
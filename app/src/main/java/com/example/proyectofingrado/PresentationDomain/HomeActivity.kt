package com.example.proyectofingrado.PresentationDomain

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyectofingrado.DataClases.User
import com.example.proyectofingrado.Interfaces.CalculateCalories
import com.example.proyectofingrado.R
import com.example.proyectofingrado.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONException
import org.json.JSONObject
import kotlin.system.exitProcess


class HomeActivity : AppCompatActivity(), CalculateCalories {

    private lateinit var binding: ActivityHomeBinding
    lateinit var toolbar:Toolbar
    lateinit var requesrQueue: RequestQueue
    val HttpURI = "https://alejandroexpdeveloper.com/usuario.php"
    lateinit var datosUsuario: User

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

        val btnMostrar = findViewById<Button>(R.id.btnmostrar)
        btnMostrar.setOnClickListener {
            mostrarReceta()
        }

        val btnGaleria = findViewById<Button>(R.id.btnGaleria)
        btnGaleria.setOnClickListener{
            startActivity(Intent(this, CaloricTable::class.java ))
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
                    var noExiste:Boolean = false;
                    noExiste = obj.getBoolean("noExiste")

                    if(noExiste){
                        val intent = Intent(this, DatosUsuario::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(intent)
                    }else{
                        datosUsuario = User(obj.getString("peso"),
                            obj.getString("altura"),
                            obj.getString("sexo"),
                            obj.getString("actividad"),
                            obj.getString("edad"))
                        var caloriesToConsum = makeCalculate(datosUsuario.peso,datosUsuario.altura,datosUsuario.sexo,datosUsuario.actividad,datosUsuario.edad)
                        //Aquí traspaso las calorias a consumir según la persona. Se extraerá en la próxima actividad y se llamará al alimento correspondiente.
                        val intent = Intent(this,SelectedFood::class.java).apply{
                            putExtra("caloriesToConsum",caloriesToConsum)
                        }//z
                        startActivity(intent)
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
        binding.texto1.text = "Bienvenido"

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

            //salir de la sesión de Firebase
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

    //Se manipula el método del botón Back de android para salir directamente de la aplicación y evitar el encolamiento de actividades
    override fun onBackPressed() {
        finishAffinity()
    }



}

class ActivityHomeBinding {

}

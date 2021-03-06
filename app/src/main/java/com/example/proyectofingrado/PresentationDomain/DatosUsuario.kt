package com.example.proyectofingrado.PresentationDomain

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.proyectofingrado.DataClases.User
import com.example.proyectofingrado.Interfaces.CalculateCalories
import com.example.proyectofingrado.R
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject


lateinit var etPeso: EditText
lateinit var etAltura: EditText
lateinit var radioGroup2:RadioGroup
var etSexo: String = "Hombre"
lateinit var radioGroup: RadioGroup
var etActividad:String = "Sedentaria"
lateinit var etEdad: EditText
lateinit var btnInsert:Button
lateinit var datosUsuario:User
lateinit var toolbar: Toolbar
lateinit var requesrQueue: RequestQueue
val HttpURI = "https://alejandroexpdeveloper.com/usuario.php"

class DatosUsuario : AppCompatActivity(), RadioGroup.OnCheckedChangeListener,CalculateCalories{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_usuario)

        etPeso = findViewById(R.id.etPeso)
        etAltura = findViewById(R.id.etAltura)
        radioGroup = findViewById(R.id.etRadioGroup)
        etEdad = findViewById(R.id.etEdad)
        radioGroup2 = findViewById(R.id.radioGroup2)
        btnInsert = findViewById(R.id.btnInsert)
        //Activo el toolbar en la activity
        toolbar = findViewById(R.id.tool_bar2)
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        //Inicizalizamos a RequestQueue
        requesrQueue = Volley.newRequestQueue(this)
        //Método para introducir los datos del formulario al pulsar el botón "enviar"
        btnInsert.setOnClickListener{
            insertarDatos()
        }
        radioGroup.setOnCheckedChangeListener(this)
        radioGroup2.setOnCheckedChangeListener(this)
    }

    //Creación del menú en el activity
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_en_datosusuario,menu)
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

    fun insertarDatos(){
        //Para obterner el Email introducido en la autentificación del usuario
        val bundle = intent.extras
        val email:String = bundle?.getString("email").toString()
        val peso = etPeso.text.toString()
        val altura = etAltura.text.toString()
        val edad = etEdad.text.toString()
        if (peso.isEmpty() || altura.isEmpty() || edad.isEmpty()) {
            Toast.makeText(this, "Debes introducir los dos campos"+". RadioButton seleccionado: ", Toast.LENGTH_LONG).show()
        }else{
            //Cadena a ejecutar en el web Service
            val stringRequest:StringRequest = object : StringRequest(Request.Method.POST,
            HttpURI, Response.Listener { serverResponse ->
                    //Este array es para recorrer el JSON
                    val obj = JSONObject(serverResponse)
                    //Requerimos el nombre del objeto booleano. En el web-servie se llama error
                    val error:Boolean = obj.getBoolean("error")
                    val mensaje = obj.getString("mensaje")
                    if(error){
                        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show()
                    }else{
                        //Si los datos son introducidos correctamente se llamará a la actividad para iniciar el servidor con el calculo de calorias ya realizado
                        datosUsuario = User(peso,altura,etSexo, etActividad,edad)
                        var caloriesToConsum = makeCalculate(datosUsuario.peso,datosUsuario.altura,datosUsuario.sexo,datosUsuario.actividad,datosUsuario.edad)
                        val intent = Intent(this,SelectedFood::class.java).apply{
                            putExtra("caloriesToConsum",caloriesToConsum)
                        }
                        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show()
                        startActivity(intent)
                    }
                }, Response.ErrorListener {
                    //En caso de que la conexión tenga un error se activará el siguiente código
                    Toast.makeText(this,"Volley incorrecto",
                        Toast.LENGTH_LONG).show()
                }){
                //Ahora mapeo los datos que quiero introducir en la BBDD a través de la función getParams
                override fun getParams():MutableMap<String,String>{
                    val parametros = HashMap<String,String>()
                    parametros.put("email",email)
                    parametros.put("peso", peso)
                    parametros.put("altura",altura)
                    parametros.put("sexo", etSexo)
                    parametros.put("edad",edad)
                    parametros.put("actividad", etActividad)
                    parametros.put("opcion","insert")
                    return parametros
                }
            }
            requesrQueue.add(stringRequest)
        }
    }

    //Esta función asignará un texto a la variable etAtividad, en función del radioButton seleccionado
    override fun onCheckedChanged(p0: RadioGroup?, checkedID: Int) {
        when(checkedID) {
            R.id.etSedentaria -> etActividad = "Sedentaria"
            R.id.etModerada -> etActividad = "Moderada"
            R.id.etIntensa -> etActividad = "Intensa"
            R.id.etHombre -> etSexo = "Hombre"
            R.id.etMujer -> etSexo = "Mujer"
        }
    }

    override fun onStop(){
        super.onStop()
        finish()
    }

}
package com.example.proyectofingrado

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.proyectofingrado.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


class AuthActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAuthBinding
    private val INICIO_GOOGLE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Introduzco el splashScreen
        setTheme(R.style.ThemeProyectoFinGrado)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Llamo a la función para la gestión de usuarios con Firebase
        configuracion()

        //Identifico si el usuario ya ha sido logueado anteriormente
        inicio_sesion()


    }//Fin del método onCreate


    //Función para implementar la lógica para los botones de registro y acceso de usuario en correo electrónico
    private fun configuracion() {
        title = "Auth"

        //Cuando el usuario pulse el boton de registrar se ejecutará el siguiente código
        binding.botonRegistro.setOnClickListener {

            //Tenemos que comprobar que los datos introducidos son correctos. Para eso este if
            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty() && binding.passwordEditText.text.toString().length >= 6) {

                /*Si es correcto, uso los servicios de Firebase para realizar la identificación de usuario.
                * El método createUserWithEmailAndPassword necesita dos parámetros de string, los cuáles
                * representan el email y la contraseña. addOnCompleteListener nos informará si fue bien*/
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                ).addOnCompleteListener {
                    //Si el proceso fue correctamente entrará en el if
                    if (it.isSuccessful) {
                        /*Permito que haya un String vacío para quitar el error, porque debería
                        exister siempre el correo al llegar a este punto*/
                        accesoCuenta(it.result?.user?.email ?: "")
                    } else {
                        //Si no fue bien, ejecuto el método para el mensaje de error
                        mensajeError(
                            binding.emailEditText.text.toString(),
                            binding.passwordEditText.text.toString()
                        )
                    }
                }//Fin de addOnCompleteListener
            } else {
                mensajeError(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
            }//Fin del if/else
        }//Fin de listener botonRegistro


        //Cuando el usuario pulse el botón de acceder, se ejecutará el siguiente código
        binding.botonAcceso.setOnClickListener {

            //Tenemos que comprobar que los datos introducidos son correctos. Para eso este if


            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()) {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        accesoCuenta(it.result?.user?.email ?: "")
                    } else {
                        val acceso = AlertDialog.Builder(this)
                        acceso.setTitle("Información incorrecta")
                        acceso.setMessage("El email o contraseña introducidos no son correctos. Inténtalo de nuevo")
                        acceso.setPositiveButton("Ok", null)
                        //Meto toda la configuración del build en una variable de tipo AlertDialog y lo muestro con show()
                        val mensaje: AlertDialog = acceso.create()
                        mensaje.show()
                    }
                }//Fin de addOnCompleteListener
            }//Fin del ifelse
        }//Fin de listener botonAcceso

        binding.botonGoogle.setOnClickListener {
            //Configuración de autentificación
                val google_conf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("383578448761-2cc9p1b42m5vbvqc87o0in9k0d344kci.apps.googleusercontent.com")
                    .requestEmail()
                    .build()



            //Configuro los datos del cliente Google con los datos antriores.
            val googleCliente = GoogleSignIn.getClient(this, google_conf)
            googleCliente.signOut()
            startActivityForResult(googleCliente.signInIntent, INICIO_GOOGLE)

        }//Fin del botón de Google


    }//Fin del método setup

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == INICIO_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {

                            if (it.isSuccessful) {
                                accesoCuenta(account.email ?: "")
                            } else {
                                val acceso = AlertDialog.Builder(this)
                                acceso.setTitle("Información incorrecta")
                                acceso.setMessage("El email o contraseña introducidos no son correctos. Inténtalo de nuevo")
                                acceso.setPositiveButton("Ok", null)
                                //Meto toda la configuración del build en una variable de tipo AlertDialog y lo muestro con show()
                                val mensaje: AlertDialog = acceso.create()
                                mensaje.show()
                            }

                        }//Fin de addOnCompleteListener

                }//Fin del if
            } catch (e: ApiException) {
                e.printStackTrace()
            }


        }//Fin de if
    }//Fin de onActivityResult


    /*Este método lo usaré para mostrar una alerta en caso de que alguna parte del proceso haya ido mal
    * o si un usuario intenta registrarse cuando ya tiene una cuenta existente o intenta acceder sin tener
    * una cuenta en la base de datos de Firebase*/
    private fun mensajeError(email: String, password: String) {

        //Aquí empezaré a controlar cuáles son los posibles errores para especificar al usuario qué ocurre

        //Este if saltará si la contraseña no tiene como mínimo los 6 caracteres que pide Firebase
        if (password.length < 6) {
            val num_caracter = AlertDialog.Builder(this)
            num_caracter.setTitle("Contraseña incorrecta")
            num_caracter.setMessage("La contraseña debe tener 6 caracteres como mínumo")
            num_caracter.setPositiveButton("Ok", null)
            val mens_caracter = num_caracter.create()
            mens_caracter.show()
        } else if (!validarEmail(email)) {
            //Si el mail no es correcto, entonces saltará este mensaje
            val email_valido = AlertDialog.Builder(this)
            //Agrego las características al mensaje
            email_valido.setTitle("Email incorrecto")
            email_valido.setMessage("No es correcto. Siga este ejemplo: elrecetero@gmail.com")
            email_valido.setPositiveButton("Ok", null)
            //Meto toda la configuración del build en una variable de tipo AlertDialog y lo muestro con show()
            val mensaje: AlertDialog = email_valido.create()
            mensaje.show()
        } else {
            //Construyo un mensaje emergente si no existe el email en la base de datos, que es la única opción posible que queda
            val builder = AlertDialog.Builder(this)
            //Agrego las características al mensaje
            builder.setTitle("Ya estás registrado")
            builder.setMessage("El email introducido ya está registrado en la base de datos")
            builder.setPositiveButton("Ok", null)
            //Meto toda la configuración del build en una variable de tipo AlertDialog y lo muestro con show()
            val mensaje: AlertDialog = builder.create()
            mensaje.show()
        }
    }//Fin del método mensajeError


    //Método para ver si el email es correcto
    private fun validarEmail(email: String): Boolean {
        val email_valido = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return email_valido.matcher(email).matches()

    }//Fin del método para validar email


    /*Si no hay error y el usuario se loguea, se ejecutará este menú, donde se mostrará la pantalla
    * de cuenta de usuario*/
    private fun accesoCuenta(email: String) {
        val cuentaIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(cuentaIntent)
    }


    /*Si el usuario ya ha entrado antes en la aplicación se cargarán los datos del home automáticamente,
    sin que tenga que volver a logearse. Para ello es el siguiente método.*/
    private fun inicio_sesion() {

        //Extraigo los datos del sharedPreference
        val datos_usuario =
            getSharedPreferences(getString(R.string.archivo_login), Context.MODE_PRIVATE)
        val email = datos_usuario.getString("email", null)

        //Si el proveedor y el email no son nulos, entonces es que alguien ya tenemos la sesión iniciada en nuestra app
        if (email != null) {

            //Si accedo con la cuenta ya creada de antes, haré invisible la pantalla de auth.
            binding.authLayout.visibility = View.INVISIBLE

            accesoCuenta(email)
        }

    }//Fin del método inicio sesión


    //Cada vez que yo quiera iniciar esta actividad la haré visible, por si no lo está al haber iniciado sesión anteriormente
    override fun onStart() {
        super.onStart()
        binding.authLayout.visibility = View.VISIBLE
    }//Fin del método onStart


}



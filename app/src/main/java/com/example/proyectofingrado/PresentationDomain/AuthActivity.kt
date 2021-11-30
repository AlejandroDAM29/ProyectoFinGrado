package com.example.proyectofingrado.PresentationDomain

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.proyectofingrado.R
import com.example.proyectofingrado.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val GOOGLE_INIT = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Exclude dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //Splashscreen introduced
        setTheme(R.style.ThemeProyectoFinGrado)
        //Start XML in class
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //User management with firebase
        configuration()
        //Identifico si el usuario ya ha sido logueado anteriormente
        userLogin()
    }

    //Implement logic for user login and registration buttons in email
    private fun configuration() {
        title = "Auth"

        binding.registerButton.setOnClickListener {
            //Data entered is checked. if -> / OK else -> ERROR
            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty() && binding.passwordEditText.text.toString().length >= 6) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        /*An empty String is allowed to remove the error, because the mail should always exist at this point*/
                        accountAccess(it.result?.user?.email ?: "")
                    } else {
                        messageError(
                            binding.emailEditText.text.toString(),
                            binding.passwordEditText.text.toString()
                        )
                    }
                }
            } else {
                messageError(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
            }
        }

        binding.accessButton.setOnClickListener {
            //Data entered is checked. if -> / OK else -> ERROR
            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        accountAccess(it.result?.user?.email ?: "")
                    } else {
                        val access = AlertDialog.Builder(this)
                        access.setTitle("Información incorrecta")
                        access.setMessage("El email o contraseña introducidos no son correctos. Inténtalo de nuevo")
                        access.setPositiveButton("Ok", null)
                        val message: AlertDialog = access.create()
                        message.show()
                    }
                }
            }
        }

        binding.botonGoogle.setOnClickListener {
            //identification configuration
                val google_conf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("383578448761-2cc9p1b42m5vbvqc87o0in9k0d344kci.apps.googleusercontent.com")
                    .requestEmail()
                    .build()
            //Google client configuration with the previous data
            val googleCliente = GoogleSignIn.getClient(this, google_conf)
            googleCliente.signOut()
            startActivityForResult(googleCliente.signInIntent, GOOGLE_INIT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_INIT) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                accountAccess(account.email ?: "")
                            } else {
                                val access = AlertDialog.Builder(this)
                                access.setTitle("Información incorrecta")
                                access.setMessage("El email o contraseña introducidos no son correctos. Inténtalo de nuevo")
                                access.setPositiveButton("Ok", null)
                                //Meto toda la configuración del build en una variable de tipo AlertDialog y lo muestro con show()
                                val message: AlertDialog = access.create()
                                message.show()
                            }
                        }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }

    private fun messageError(email: String, password: String) {
        //These are the possible errors when entering with a user's account

        //Minimum 6 characters for Firebase password
        if (password.length < 6) {
            val num_caracter = AlertDialog.Builder(this)
            num_caracter.setTitle("Contraseña incorrecta")
            num_caracter.setMessage("La contraseña debe tener 6 caracteres como mínimo")
            num_caracter.setPositiveButton("Ok", null)
            val messageCaracter = num_caracter.create()
            messageCaracter.show()
        } else if (!emailValidation(email)) {
            //Wrong email
            val validatedEmail = AlertDialog.Builder(this)
            validatedEmail.setTitle("Email incorrecto")
            validatedEmail.setMessage("No es correcto. Siga este ejemplo: elrecetero@gmail.com")
            validatedEmail.setPositiveButton("Ok", null)
            val message: AlertDialog = validatedEmail.create()
            message.show()
        } else {
            //Email does not exist in the firebase database
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Ya estás registrado")
            builder.setMessage("El email introducido ya está registrado en la base de datos")
            builder.setPositiveButton("Ok", null)
            val message: AlertDialog = builder.create()
            message.show()
        }
    }

    //Check email format
    private fun emailValidation(email: String): Boolean {
        val validatedEmail = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return validatedEmail.matcher(email).matches()
    }

    //Correct login
    private fun accountAccess(email: String) {
        val cuentaIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(cuentaIntent)
    }

    /*If the user has already entered the application before, the home data will be loaded automatically, without having to log in again*/
    private fun userLogin() {
        //SharedPreferences data are extracted
        val userData =
            getSharedPreferences(getString(R.string.archivo_login), Context.MODE_PRIVATE)
        val email = userData.getString("email", null)
        //If the provider and the email are not null, then someone already has the session started in our app
        if (email != null) {
            //The authentication screen becomes invisible
            binding.authLayout.visibility = View.INVISIBLE
            accountAccess(email)
        }
    }

    //The authentication screen is visible again
    override fun onStart() {
        super.onStart()
        binding.authLayout.visibility = View.VISIBLE
    }

}



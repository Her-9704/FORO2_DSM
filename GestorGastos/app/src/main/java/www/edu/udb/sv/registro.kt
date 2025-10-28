package www.edu.udb.sv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Registro : AppCompatActivity() {

    //referencia firebase
    private lateinit var auth: FirebaseAuth

    private lateinit var buttonRegistrarse: Button
    private lateinit var textIniciarSesión: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. INICIALIZAR FIREBASE AUTH (Necesario antes de chequear el usuario)
        auth = FirebaseAuth.getInstance()

        // 2. VERIFICAR SESIÓN ACTIVA (EL CAMBIO CLAVE)
        if (auth.currentUser != null) {
            // Si el usuario YA está logueado, ir a la actividad principal
            val intent = Intent(this, GastosActivity::class.java)
            startActivity(intent)
            finish()
            return // IMPORTANTE: Salir del onCreate
        }

        // Si no está logueado, continuar con la configuración de la vista de registro
        setContentView(R.layout.activity_registro)

        buttonRegistrarse = findViewById<Button>(R.id.buttonIniciar)
        buttonRegistrarse.setOnClickListener {
            val email = findViewById<EditText>(R.id.Email).text.toString()
            val password = findViewById<EditText>(R.id.Password).text.toString()
            this.registrate(email, password)
        }
        textIniciarSesión = findViewById<TextView>(R.id.textIniciarSesión)
        textIniciarSesión.setOnClickListener{
            this.goToLogin()
        }
    }

    private fun registrate(email: String, password: String){
        // Puedes agregar una verificación básica aquí para email y password no vacíos
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(applicationContext, "Por favor, introduce email y contraseña.", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{task ->
            if (task.isSuccessful){
                // REGISTRO EXITOSO: Redirige a GastosActivity y cierra Registro
                val intent =  Intent(this, GastosActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {exception ->
            // REGISTRO FALLIDO: Muestra el error
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun goToLogin(){
        val intent = Intent (this, Login::class.java)
        startActivity(intent)
        finish()
    }
}
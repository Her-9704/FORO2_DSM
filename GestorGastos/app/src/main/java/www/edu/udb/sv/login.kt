package www.edu.udb.sv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var btnLogin : Button
    private lateinit var textViewRegistrarse: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            // Si el usuario ya está logueado, vamos directamente a GastosActivity
            val intent = Intent(this, GastosActivity::class.java)
            startActivity(intent)
            finish()
            return // Salimos del onCreate para no inicializar más vistas
        }

        btnLogin = findViewById<Button>(R.id.buttonIniciar)
        btnLogin.setOnClickListener{
            val email = findViewById<EditText>(R.id.Email).text.toString()
            val password = findViewById<EditText>(R.id.Password).text.toString()
            this.login(email, password)
        }

        textViewRegistrarse = findViewById<TextView>(R.id.textRegistrarse)
        textViewRegistrarse.setOnClickListener{
            this.goToRegister()
        }
    }

    private fun login(email: String, password: String){

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{task ->
            if (task.isSuccessful){
                val intent = Intent(this, GastosActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun goToRegister(){
        val intent = Intent(this, Registro::class.java)
        startActivity(intent)
        finish()
    }
}
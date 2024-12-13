package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.model.CloudRepository
import com.example.myapplication.model.UserSettings
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var userSettings: UserSettings
    private val cloudRepository = CloudRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val correoElectronico = findViewById<EditText>(R.id.idCorreoElectronico)
        val contrasena = findViewById<EditText>(R.id.idContrasena)
        val registrar = findViewById<Button>(R.id.btnRegistrar)
        val iniciarSesion = findViewById<Button>(R.id.idIniciarSesion)

        userSettings = UserSettings()

        // En el método donde obtienes las configuraciones del usuario
        cloudRepository.getUserSettings { settings ->
            if (settings != null) {
                // Actualizar las configuraciones del usuario
                userSettings = settings
                // Continuar con la lógica de inicio de sesión
                // ...
            } else {
                // Manejar el caso en que no se puedan obtener las configuraciones
                // Por ejemplo, puedes mostrar un mensaje de error o utilizar valores por defecto
                Toast.makeText(this, "Error al obtener configuraciones del usuario", Toast.LENGTH_SHORT).show()
                // Continuar con la lógica de inicio de sesión utilizando valores por defecto
                // ...
            }
        }

        val auth = FirebaseAuth.getInstance()

        // En el método donde obtienes las configuraciones del usuario
        cloudRepository.getUserSettings { settings ->
            if (settings != null) {
                // Actualizar las configuraciones del usuario
                userSettings = settings
                // Continuar con la lógica de inicio de sesión
                // ...
            } else {
                // Manejar el caso en que no se puedan obtener las configuraciones
                // Por ejemplo, puedes mostrar un mensaje de error o utilizar valores por defecto
                Toast.makeText(this, "Error al obtener configuraciones del usuario", Toast.LENGTH_SHORT).show()
                // Continuar con la lógica de inicio de sesión utilizando valores por defecto
                // ...
            }
        }

        registrar.setOnClickListener {
            val intent = Intent(this, RegistrarActivity::class.java)
            startActivity(intent)
        }

        iniciarSesion.setOnClickListener {
            val correo = correoElectronico.text.toString().trim()
            val contra = contrasena.text.toString().trim()

            if (correo.isEmpty()) {
                correoElectronico.error = "El correo electrónico es obligatorio"
                correoElectronico.requestFocus()
                return@setOnClickListener
            }

            if (contra.isEmpty()) {
                contrasena.error = "La contraseña es obligatoria"
                contrasena.requestFocus()
                return@setOnClickListener
            }

            if (contra.length < 6) {
                contrasena.error = "La contraseña debe tener al menos 6 caracteres"
                contrasena.requestFocus()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(correo, contra)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Error al iniciar sesión: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
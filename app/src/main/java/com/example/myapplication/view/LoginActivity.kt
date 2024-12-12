package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val correoElectronico = findViewById<EditText>(R.id.idCorreoElectronico)
        val contrasena = findViewById<EditText>(R.id.idContrasena)
        val registrar = findViewById<Button>(R.id.btnRegistrar)
        val iniciarSesion = findViewById<Button>(R.id.idIniciarSesion)

        //Variable para registrar y almacenar en la base de datos al usuario
        val auth = FirebaseAuth.getInstance()

        registrar.setOnClickListener{
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

            auth.createUserWithEmailAndPassword(correo,contra)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"Registro exitoso",Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Error en el registro: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            iniciarSesion.setOnClickListener{
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

                auth.signInWithEmailAndPassword(correo,contra)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Inicio de sesion exitoso",Toast.LENGTH_SHORT).show()

                            val intent= Intent(this,MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Error al iniciar sesion: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

        }
    }
}
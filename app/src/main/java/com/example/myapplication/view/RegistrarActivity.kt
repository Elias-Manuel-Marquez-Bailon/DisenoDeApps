package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth

class RegistrarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        val nombreUsuario = findViewById<EditText>(R.id.nombreUsuario)
        val apellidosUsuario = findViewById<EditText>(R.id.apellidosUsuario)
        val correoUsuario = findViewById<EditText>(R.id.correoUsuario)
        val contrasenaUsaurio = findViewById<EditText>(R.id.contrasenaUsuario)
        val confirmarContrasena = findViewById<EditText>(R.id.confirmarContrasena)
        val registrarUsuario = findViewById<Button>(R.id.registrarUsuario)

        //Variable para registrar al nuevo usuario
        val auth = FirebaseAuth.getInstance()

        registrarUsuario.setOnClickListener {
            val nombre = nombreUsuario.text.toString().trim()
            val apellidos = apellidosUsuario.text.toString().trim()
            val correo = correoUsuario.text.toString().trim()
            val contra = contrasenaUsaurio.text.toString().trim()
            val confirmar = confirmarContrasena.text.toString().trim()

            if (nombre.isEmpty()) {
                nombreUsuario.error = "El nombre es obligatorio"
                nombreUsuario.requestFocus()
                return@setOnClickListener
            }

            if (apellidos.isEmpty()) {
                apellidosUsuario.error = "Los apellidos son obligatorios"
                apellidosUsuario.requestFocus()
                return@setOnClickListener
            }

            if (correo.isEmpty()) {
                correoUsuario.error = "El correo es obligatorio"
                correoUsuario.requestFocus()
                return@setOnClickListener
            }

            if (contra.isEmpty()) {
                contrasenaUsaurio.error = "La contraseña es obligatoria"
                contrasenaUsaurio.requestFocus()
                return@setOnClickListener
            }

            if (confirmar.isEmpty()) {
                confirmarContrasena.error = "La contraseña es obligatoria"
                confirmarContrasena.requestFocus()
                return@setOnClickListener
            }

            if (contra.length < 6) {
                contrasenaUsaurio.error = "La contraseña debe tener almenos 6 caracteres"
                contrasenaUsaurio.requestFocus()
                return@setOnClickListener
            }

            if (confirmar.length < 6) {
                confirmarContrasena.error = "La contraseña debe tener almenos 6 caracteres"
                confirmarContrasena.requestFocus()
                return@setOnClickListener
            }

            if (confirmar.length != confirmar.length) {
                confirmarContrasena.error = "La contraseña debe ser igual ala anterior"
                confirmarContrasena.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(correo,contra)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this,LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Error en el registro: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

    }
}
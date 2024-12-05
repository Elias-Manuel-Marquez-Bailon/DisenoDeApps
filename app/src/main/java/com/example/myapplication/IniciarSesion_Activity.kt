package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class IniciarSesion_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iniciar_sesion)

        val correoElectronico = findViewById<EditText>(R.id.idCorreoElectronico)
        val contrasena = findViewById<EditText>(R.id.idContrasena)
        val registrar = findViewById<Button>(R.id.btnRegistrar)
        val iniciarSesion = findViewById<Button>(R.id.idIniciarSesion)

        val auth = FirebaseAuth.getInstance()

        registrar.setOnClickListener{
            val email = correoElectronico.text.toString().trim()
            val password = contrasena.text.toString().trim()

            if(email.isEmpty()) {
                correoElectronico.error = "El correo electrónico es obligatorio"
                correoElectronico.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()) {
                contrasena.error = "La contraseña es obligatoria"
                contrasena.requestFocus()
                return@setOnClickListener
            }

            if(password.length < 10){
                contrasena.error = "La contraseña debe tener almenos 10 caracteres"
                contrasena.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this,"Registro exitoso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Error en el registro:${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            iniciarSesion.setOnClickListener{
                val email = correoElectronico.text.toString().trim()
                val password = contrasena.text.toString().trim()

                if (email.isEmpty()) {
                    correoElectronico.error=""
                    correoElectronico.requestFocus()
                    return@setOnClickListener
                }

                if (password.isEmpty()) {
                    contrasena.error = ""
                    contrasena.requestFocus()
                    return@setOnClickListener
                }

                auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener{ task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Inicio de sesion", Toast.LENGTH_SHORT).show()

                            val intent = Intent (this,VistaUsuario::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Error al iniciar sesion:${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
            //
            //
        }
    }
}
package com.example.myapplication.view

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R

class LoginActivity : AppCompatActivity() {
    val correoElectronico = findViewById<EditText>(R.id.idCorreoElectronico)
    val contrasena = findViewById<EditText>(R.id.idContrasena)
    val registrar = findViewById<Button>(R.id.btnRegistrar)
    val iniciarSesion = findViewById<Button>(R.id.idIniciarSesion)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


    }
}
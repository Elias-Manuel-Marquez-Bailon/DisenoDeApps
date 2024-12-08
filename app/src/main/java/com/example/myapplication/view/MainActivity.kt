package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.controller.AlertController
import com.example.myapplication.controller.LightSensorController
import com.example.myapplication.model.CloudRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var idIndicadorLuz: MaterialCardView
    private lateinit var idValorLuz: TextView
    private lateinit var idEstadoLectura: TextView
    private lateinit var idSeleccionar: RadioGroup
    private lateinit var idLectura: RadioButton
    private lateinit var idExterior: RadioButton
    private lateinit var btnIniciarDeteccion: MaterialButton
    private lateinit var btnHistorial: MaterialButton

    private lateinit var lightSensorController: LightSensorController
    private lateinit var alertController: AlertController

    private val cloudRepository = CloudRepository()
    private val currentMode = "Lectura" //o Exterior

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ejemplo de cómo guardar una lectura
        val lightLevel = 500f // Valor del sensor de luz

        lifecycleScope.launch {
            try {
                cloudRepository.uploadLightReading(lightLevel,currentMode)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity,"Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayReadings(readings: List<Map<String,Any>>) {
        // Implementa la lógica para mostrar las lecturas
        // Por ejemplo, en un RecyclerView
    }

}


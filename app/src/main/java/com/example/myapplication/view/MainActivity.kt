package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.controller.AlertController
import com.example.myapplication.controller.LightSensorController
import com.example.myapplication.model.CloudRepository
import com.example.myapplication.model.UserSettings
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

    private lateinit var userSettings : UserSettings //Aqui hay atributos
    private val cloudRepository = CloudRepository() //Aqui esta la firebase
    private var currentMode: String = "Lectura" //o Exterior

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar los componentes de la vista
        idIndicadorLuz = findViewById(R.id.idIndicadorLuz)
        idValorLuz = findViewById(R.id.idValorLuz)
        idEstadoLectura = findViewById(R.id.idEstadoLectura)
        idSeleccionar = findViewById(R.id.idSeleccionar)
        idLectura = findViewById(R.id.idLectura)
        idExterior = findViewById(R.id.idExterior)
        btnIniciarDeteccion = findViewById(R.id.btnIniciarDeteccion)
        btnHistorial = findViewById(R.id.btnHistorial)

        // Asegúrate de inicializar userSettings aquí <--------------
        userSettings = UserSettings()

        // Inicializar el LightSensorController y el AlertController
        lightSensorController = LightSensorController(this,userSettings)
        alertController = AlertController(this)

        // Obtener la configuración del usuario desde Firebase
        cloudRepository.getUserSettings { settings ->
            userSettings = settings?: UserSettings()
            // Continuar la inicialización del MainActivity
        }

        // Configurar los listeners
        btnIniciarDeteccion.setOnClickListener{
            startLightSensorMonitoring()
        }
        btnHistorial.setOnClickListener{
            navigateToHistoryActivity()
        }
        idSeleccionar.setOnCheckedChangeListener{ _, checkedId ->
            when (checkedId){
                R.id.idLectura -> updateMode("Lectura")
                R.id.idExterior -> updateMode("Exterior")
            }
        }

        // Iniciar el monitoreo del sensor de luz
        lightSensorController.startLightSensorMonitoring()

        lightSensorController.onLightLevelChanged = { level ->
            // Actualizar la interfaz con el nuevo nivel de luz
            val lightStatus = determineLightStatus(level)
            updateUI(level,lightStatus,currentMode)

            if (lightStatus != "Luz adecuada") {
                alertController.triggerAlert(userSettings.alertType)
            } else {
                alertController.stopAlerts()
            }

            // Guardar la lectura en Firebase
            lifecycleScope.launch {
                cloudRepository.uploadLightReading(level,currentMode) { success ->
                    if (success) {
                        // La lectura se guardó correctamente en Firebase
                    } else {
                        // Hubo un error al guardar la lectura en Firebase
                    }

                }
            }
        }


    }

    private fun startLightSensorMonitoring(){
        lightSensorController.startLightSensorMonitoring()
        // Actualizar la interfaz para indicar que se está detectando
    }

    private fun navigateToHistoryActivity(){
        val intent = Intent(this,HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun updateMode(mode:String){
        currentMode = mode
        // Guardar el modo actual en Firebase
        lifecycleScope.launch {
            cloudRepository.uploadLightReading(0f,currentMode){ success ->
                if (success) {
                    // La lectura se guardó correctamente en Firebase
                } else {
                    // Hubo un error al guardar la lectura en Firebase
                }
            }
        }
    }

    private fun updateUI(lightLevel: Float, lightStatus : String, mode: String) {
        idValorLuz.text = "$lightLevel lux"
        idEstadoLectura.text = lightStatus
        // Actualizar el indicador de luz según el estado
        when (lightStatus) {
            "Luz adecuada" -> idIndicadorLuz.setCardBackgroundColor(getColor(R.color.purple_200))
            "Luz baja" -> idIndicadorLuz.setCardBackgroundColor(getColor(R.color.teal_200))
            "Luz alta" -> idIndicadorLuz.setCardBackgroundColor(getColor(R.color.purple_500))
        }
        // Actualizar el modo seleccionado en la interfaz
        if (mode == "Lectura") {
            idLectura.isChecked = true
        } else {
            idExterior.isChecked = true
        }
    }

    private fun determineLightStatus(lightLevel: Float): String {
        return when {
            lightLevel < userSettings.lowLightThreshold -> "Luz baja"
            lightLevel > userSettings.highLightThreshold -> "Luz alta"
            else -> "Luz adecuada"
        }
    }

    private fun navigateToSettingsActivity(){
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    //Aqui abajo iria la logica para agregar el menu (En teoria no deberias de cambiar algo de las funciones del programa)
    //Seria agregar metodos que te permitieran desplazarte del Main a las otras 2 interfaces

}


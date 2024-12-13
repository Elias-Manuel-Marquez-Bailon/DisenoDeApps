package com.example.myapplication.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.controller.AlertController
import com.example.myapplication.controller.LightSensorController
import com.example.myapplication.controller.ProximitySensorController
import com.example.myapplication.model.CloudRepository
import com.example.myapplication.model.UserSettings
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),LightSensorController.LightSensorListener {
    //Identificador unico del canal de notificaciones
    private val channelId="test_id"
    private lateinit var idIndicadorLuz: MaterialCardView
    private lateinit var idValorLuz: TextView
    private lateinit var idEstadoLectura: TextView
    private lateinit var idSeleccionar: RadioGroup
    private lateinit var idLectura: RadioButton
    private lateinit var idExterior: RadioButton
    private lateinit var btnIniciarDeteccion: MaterialButton
    private lateinit var btnDetenerDeteccion: MaterialButton

    private lateinit var alertController: AlertController
    private lateinit var userSettings: UserSettings
    private val cloudRepository = CloudRepository()
    private var currentMode: String = "Lectura"
    private var isMonitoring = false

    private lateinit var txtValorLuz: TextView
    private lateinit var txtEstadoLectura: TextView
    private lateinit var lightSensorController: LightSensorController

    private lateinit var proximitySensorController: ProximitySensorController

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar los componentes de la vista
        idIndicadorLuz = findViewById(R.id.idIndicadorLuz)
        idValorLuz = findViewById(R.id.idValorLuz)
        idEstadoLectura = findViewById(R.id.idEstadoLectura)
        idSeleccionar = findViewById(R.id.idSeleccionar)
        idLectura = findViewById(R.id.idEstadoLectura)
        idExterior = findViewById(R.id.idExterior)
        btnIniciarDeteccion = findViewById(R.id.btnIniciarDeteccion)
        btnDetenerDeteccion = findViewById(R.id.btnDetenerDeteccion)

        // Inicializa los TextViews
        txtValorLuz = findViewById(R.id.idValorLuz) // Asegúrate de que el ID coincida con tu layout
        txtEstadoLectura = findViewById(R.id.idEstadoLectura)

        userSettings = UserSettings()
        lightSensorController = LightSensorController(this, userSettings, cloudRepository)
        alertController = AlertController(this)

        // Inicializa el controller (no olvides pasar UserSettings y CloudRepository)
        lightSensorController.setListener(this) // Establecer el listener

        cloudRepository.getUserSettings { settings ->
            userSettings = settings ?: UserSettings()
        }

        // Crear e iniciar el ProximitySensorController
        proximitySensorController = ProximitySensorController(
            context = this,
            cloudRepository = cloudRepository,
            userSettings = userSettings
        )

        btnIniciarDeteccion.setOnClickListener {
            startLightSensorMonitoring()
        }

        btnDetenerDeteccion.setOnClickListener {
            stopLightSensorMonitoring()
        }

        /*idSeleccionar.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.idLectura -> updateMode("Lectura")
                R.id.idExterior -> updateMode("Exterior")
            }
        }*/

        lightSensorController.onLightLevelChanged = { level ->
            val lightStatus = determineLightStatus(level)
            updateUI(level, lightStatus, currentMode)

            if (lightStatus != "Luz adecuada") {
                alertController.triggerAlert(userSettings.alertType)
            } else {
                alertController.stopAlerts()
            }

            lifecycleScope.launch {
                cloudRepository.uploadLightReading(level, currentMode) { }
            }
        }

        idLectura.isChecked = true
        updateMode("Lectura")
    }
    //Solicitar permiso de notificaciones para Android13 +
    private fun requestNotificationPermission(){
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){isGranted : Boolean ->
            if (isGranted){
                Log.d("Permisos", "Permiso concedido")
                mostrarNotificacionConRetraso()
            }else{
                Log.e("Permisos", "Permiso denegado")
            }
        }

        //Lanzar la solicitud del permiso
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun mostrarNotificacionConRetraso(){
        window.decorView.postDelayed({
            try {
                mostrarNotificacion() //Enviar notificacion basica
                Log.e("Depuracion", "Notificacion enviada correctamente")
            }catch (e: Exception){
                Log.e("Error", "Error al mostrar la notificacion: ${e.message}")
            }
        }, 2000)
    }

    private fun mostrarNotificacion(){
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) //Icono de la notificacion
            .setContentTitle("Notificacion de prueba") //titulode la notificacio
            .setContentText("Esto es una notificacion de prueba") //Testo de la notificacion
            .setPriority(NotificationCompat.PRIORITY_HIGH) //nIVEL DE PRIORIDAD
            .setAutoCancel(true) //La notifiacion desaparecera al tocarla
            .build()

        //Enviar la notificacion
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(1001, notification)
        Log.d("Depuracion", "Notificacion construida y enviada")
    }

    override fun onResume() {
        super.onResume()
        lightSensorController.startLightSensorMonitoring() // Iniciar el sensor al volver a la actividad
    }

    override fun onPause() {
        super.onPause()
        lightSensorController.stopLightSensorMonitoring() // Detener el sensor al salir de la actividad
    }

    // Implementación del método de la interfaz
    override fun onSensorChanged(lightLevel: Float, mode: String) {
        updateSensorData(lightLevel, mode) // Actualiza la UI
    }

    private fun startLightSensorMonitoring() {
        if (!isMonitoring) {
            lightSensorController.startLightSensorMonitoring()
            isMonitoring = true
            //btnIniciarDeteccion.text = getString(R.string.detenerDeteccion)
        }
    }

    private fun stopLightSensorMonitoring() {
        if (isMonitoring) {
            lightSensorController.stopLightSensorMonitoring()
            isMonitoring = false
            //btnIniciarDeteccion.text = getString(R.string.iniciarDeteccion)
        }
    }

    fun updateSensorData(lightLevel: Float, mode: String) {
        txtValorLuz.text = "$lightLevel lux"
        txtEstadoLectura.text = mode
    }

    private fun updateMode(mode: String) {
        currentMode = mode
        lifecycleScope.launch {
            cloudRepository.uploadLightReading(0f, currentMode) { }
        }

        when (currentMode) {
            "Lectura" -> {
                // Configuraciones específicas para el modo lectura
                // Por ejemplo, ajustar umbrales de luz, iniciar procesos, etc.
                userSettings.lowLightThreshold = 100f // Umbral para modo lectura
                userSettings.highLightThreshold = 300f // Umbral para modo lectura
            }
            "Exterior" -> {
                // Configuraciones específicas para el modo exterior
                userSettings.lowLightThreshold = 200f // Umbral para modo exterior
                userSettings.highLightThreshold = 500f // Umbral para modo exterior
            }
        }

    }

    private fun updateUI(lightLevel: Float, lightStatus: String, mode: String) {
        idValorLuz.text = "$lightLevel lux"

        when {
            idLectura.isChecked -> {
                when (lightStatus) {
                    "Luz baja" -> idEstadoLectura.text = "La luz es muy baja, se recomienda encender una luz"
                    "Luz alta" -> idEstadoLectura.text = "La luz es muy alta, trata de cambiarte de lugar"
                    "Luz adecuada" -> idEstadoLectura.text = "Luz adecuada para lectura"
                }
            }
            idExterior.isChecked -> {
                when (lightStatus) {
                    "Luz baja" -> idEstadoLectura.text = "La luz es muy baja, ¿Si estas afuera en el sol?"
                    "Luz alta" -> idEstadoLectura.text = "Estas en un lugar con mucha luz, buscate un lugar para refugiarte"
                    "Luz adecuada" -> idEstadoLectura.text = "Luz adecuada para exterior"
                }
            }
        }

        when (lightStatus) {
            "Luz adecuada" -> idIndicadorLuz.setCardBackgroundColor(getColor(R.color.purple_200))
            "Luz baja" -> idIndicadorLuz.setCardBackgroundColor(getColor(R.color.teal_200))
            "Luz alta" -> idIndicadorLuz.setCardBackgroundColor(getColor(R.color.purple_500))
        }
    }

    private fun determineLightStatus(lightLevel: Float): String {
        return when {
            lightLevel < userSettings.lowLightThreshold -> "Luz baja"
            lightLevel > userSettings.highLightThreshold -> "Luz alta"
            else -> "Luz adecuada"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_historial -> {
                navigateToHistoryActivity()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToHistoryActivity() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

}
package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.model.AlertType
import com.example.myapplication.model.CloudRepository
import com.example.myapplication.model.UserSettings
import com.example.myapplication.utils.Constants
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var userSettings: UserSettings
    private lateinit var idMinimoLectura: Slider
    private lateinit var idMinimoExterior: Slider //
    private lateinit var idAlertasVibracion: SwitchMaterial
    private lateinit var idAlertasSonoras: SwitchMaterial //
    private lateinit var idVolumenAlerta: Slider //
    private lateinit var idCambiarModoAutomatico: SwitchMaterial
    private lateinit var idModoPorDefecto: AutoCompleteTextView//
    private lateinit var idRestaurar: Button
    private lateinit var idGuardar: Button//

    private val cloudRepository = CloudRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Inicializar las referencias a los componentes de la vista
        idMinimoLectura = findViewById(R.id.idMinimoLectura)
        idMinimoExterior = findViewById(R.id.idMinimoExterior)
        idAlertasVibracion = findViewById(R.id.idAlertasVibracion)
        idAlertasSonoras = findViewById(R.id.idAlertasSonoras)
        idVolumenAlerta = findViewById(R.id.idVolumenAlerta)
        idCambiarModoAutomatico = findViewById(R.id.idCambiarModoAutomatico)
        idModoPorDefecto = findViewById(R.id.idModoPorDefecto)
        idRestaurar = findViewById(R.id.idRestaurar)
        idGuardar = findViewById(R.id.idGuardar)

        userSettings = UserSettings()

        // Cargar las UserSettings
        cloudRepository.getUserSettings { settings ->
            if (settings != null) {
                userSettings = settings
                updateUIWithUserSettings()
            } else {
                // Manejar el caso en que no se puedan obtener las configuraciones
                // Por ejemplo, puedes mostrar un mensaje de error o utilizar valores por defecto
                Toast.makeText(this, "Error al obtener configuraciones del usuario", Toast.LENGTH_SHORT).show()
                // Inicializar los valores de la UI con valores por defecto
                handleUserSettingsLoadError()
            }
        }

        // Inicializar los valores de los deslizadores con los valores de las UserSettings
        idMinimoLectura.value = userSettings.readingLowLightThreshold
        idMinimoExterior.value = userSettings.exteriorLowLightThreshold

        // Inicializar el valor del deslizador con el valor de las UserSettings
        idVolumenAlerta.value = userSettings.alertVolume.toFloat()

        // Inicializar los valores de los interruptores con los valores de las UserSettings
        idAlertasVibracion.isChecked = userSettings.readingAlertType == AlertType.VIBRATION || userSettings.readingAlertType == AlertType.BOTH
        idAlertasSonoras.isChecked = userSettings.readingAlertType == AlertType.SOUND || userSettings.readingAlertType == AlertType.BOTH

        // Inicializar el valor del interruptor y el campo desplegable con los valores de las UserSettings
        idCambiarModoAutomatico.isChecked = userSettings.autoModeChangeEnabled
        idModoPorDefecto.setText(userSettings.defaultMode, false)

        // Agregar listeners a los deslizadores para actualizar las UserSettings
        idMinimoLectura.addOnChangeListener { _, value, _ ->
            userSettings.readingLowLightThreshold = value.toInt().toFloat()
            cloudRepository.saveUserSettings(userSettings) { success ->
                // Manejar el resultado de la operación de guardado
            }
        }

        idMinimoExterior.addOnChangeListener { _, value, _ ->
            userSettings.exteriorLowLightThreshold = value.toInt().toFloat()
            cloudRepository.saveUserSettings(userSettings) { success ->
                // Manejar el resultado de la operación de guardado
            }
        }

        // Agregar listeners a los interruptores para actualizar las UserSettings
        idAlertasVibracion.setOnCheckedChangeListener { _, isChecked ->
            userSettings.readingAlertType = when {
                isChecked && idAlertasSonoras.isChecked -> AlertType.BOTH
                isChecked -> AlertType.VIBRATION
                else -> AlertType.SOUND
            }
            cloudRepository.saveUserSettings(userSettings) { success ->
                // Manejar el resultado de la operación de guardado
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_main -> {
                navigateToMainActivity()
                true
            }
            R.id.action_history -> {
                navigateToHistoryActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHistoryActivity() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
    }

    private fun updateUIWithUserSettings(){
       userSettings?.let { settings ->
           // Actualizar los valores de los componentes de la vista con los valores de las UserSettings
           idMinimoLectura.value = userSettings.readingLowLightThreshold
           idMinimoExterior.value = userSettings.exteriorLowLightThreshold

           idAlertasVibracion.isChecked = userSettings.readingAlertType == AlertType.VIBRATION ||
                   userSettings.readingAlertType == AlertType.BOTH

           idAlertasSonoras.isChecked = userSettings.readingAlertType == AlertType.SOUND
                   || userSettings.readingAlertType == AlertType.BOTH

           idVolumenAlerta.value = userSettings.alertVolume.toFloat()
           idCambiarModoAutomatico.isChecked = userSettings.autoModeChangeEnabled
           idModoPorDefecto.setText(userSettings.defaultMode, false)

           idVolumenAlerta.value = userSettings.alertVolume.toFloat()
           idCambiarModoAutomatico.isChecked = userSettings.autoModeChangeEnabled

           idModoPorDefecto.setText(userSettings.defaultMode, false)
       }
    }

    private fun updateUserSettingsFromUI() {
        userSettings?.let { settings ->
            // Actualizar los valores de las UserSettings con los valores seleccionados por el usuario en la interfaz
            userSettings.readingLowLightThreshold = idMinimoLectura.value.toInt().toFloat()
            userSettings.exteriorLowLightThreshold = idMinimoExterior.value.toInt().toFloat()
            userSettings.readingAlertType = when {
                idAlertasVibracion.isChecked && idAlertasSonoras.isChecked -> AlertType.BOTH
                idAlertasVibracion.isChecked -> AlertType.VIBRATION
                idAlertasSonoras.isChecked -> AlertType.SOUND
                else -> AlertType.BOTH
            }
            userSettings.alertVolume = idVolumenAlerta.value.toInt()
            userSettings.autoModeChangeEnabled = idCambiarModoAutomatico.isChecked
            userSettings.defaultMode = idModoPorDefecto.text.toString()

            userSettings.alertVolume = idVolumenAlerta.value.toInt()
            userSettings.autoModeChangeEnabled = idCambiarModoAutomatico.isChecked

            userSettings.defaultMode = idModoPorDefecto.text.toString()
        }
    }

    private fun handleUserSettingsLoadError() {
        // Manejar el caso en que no se puedan obtener las configuraciones
        // Por ejemplo, mostrar un mensaje de error o utilizar valores por defecto
        Toast.makeText(this, "Error al obtener configuraciones del usuario", Toast.LENGTH_SHORT).show()
        initializeUIWithDefaultValues()
    }

    private fun initializeUIWithDefaultValues() {
        // Inicializar los componentes de la UI con valores por defecto
        idMinimoLectura.value = Constants.DEFAULT_READING_LOW_LIGHT_THRESHOLD
        idMinimoExterior.value = Constants.DEFAULT_EXTERIOR_LOW_LIGHT_THRESHOLD
        idAlertasVibracion.isChecked = (userSettings.readingAlertType == AlertType.VIBRATION || userSettings.readingAlertType == AlertType.BOTH)
        idAlertasSonoras.isChecked = (userSettings.readingAlertType == AlertType.SOUND || userSettings.readingAlertType == AlertType.BOTH)
        idVolumenAlerta.value = userSettings.alertVolume.toFloat()
        idCambiarModoAutomatico.isChecked = userSettings.autoModeChangeEnabled
        idModoPorDefecto.setText(userSettings.defaultMode)
    }


}

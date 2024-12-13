package com.example.myapplication.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.controller.LightSensorController
import com.example.myapplication.model.AlertType
import com.example.myapplication.model.CloudRepository
import com.example.myapplication.model.UserSettings
import com.example.myapplication.utils.Constants
import com.google.android.material.card.MaterialCardView
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var userSettings: UserSettings
    private lateinit var idMinimoLectura: Slider
    private lateinit var idMinimoExterior: Slider
    private lateinit var idAlertasVibracion: SwitchMaterial
    private lateinit var idAlertasSonoras: SwitchMaterial
    private lateinit var idVolumenAlerta: Slider
    private lateinit var idCambiarModoAutomatico: SwitchMaterial
    private lateinit var idModoPorDefecto: AutoCompleteTextView
    private lateinit var idRestaurar: Button
    private lateinit var idGuardar: Button

    // Eliminar estas líneas
    // private lateinit var txtValorLuz: TextView
    // private lateinit var txtEstadoLectura: TextView

    private lateinit var lightSensorController: LightSensorController

    private lateinit var idSeleccionar: RadioGroup
    private lateinit var idLectura: RadioButton
    private lateinit var idExterior: RadioButton
    private lateinit var idIndicadorLuz: MaterialCardView

    private val cloudRepository = CloudRepository()

    @SuppressLint("MissingInflatedId")
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

        // Eliminar estas líneas
        // txtValorLuz = findViewById(R.id.txtValorLux) // Asegúrate de que el ID sea correcto
        // txtEstadoLectura = findViewById(R.id.txtEstadoLuz) // Asegúrate de que el ID sea correcto

        idLectura = findViewById(R.id.idLectura)
        idExterior = findViewById(R.id.idExterior)
        idIndicadorLuz = findViewById(R.id.idIndicadorLuz)

        userSettings = UserSettings()

        // Cargar las UserSettings
        cloudRepository.getUserSettings { settings ->
            if (settings != null) {
                userSettings = settings
                updateUIWithUserSettings()
            } else {
                Toast.makeText(this, "Error al obtener configuraciones del usuario", Toast.LENGTH_SHORT).show()
                handleUserSettingsLoadError()
            }
        }

        // Inicializar los valores de los deslizadores con los valores de las UserSettings
        idMinimoLectura.value = userSettings.readingLowLightThreshold
        idMinimoExterior.value = userSettings.exteriorLowLightThreshold
        idVolumenAlerta.value = userSettings.alertVolume.toFloat()

        idAlertasVibracion.isChecked = userSettings.readingAlertType == AlertType.VIBRATION || userSettings.readingAlertType == AlertType.BOTH
        idAlertasSonoras.isChecked = userSettings.readingAlertType == AlertType.SOUND || userSettings.readingAlertType == AlertType.BOTH

        cargarPreferencias()

        idCambiarModoAutomatico.isChecked = userSettings.autoModeChangeEnabled
        idModoPorDefecto.setText(userSettings.defaultMode, false)

        // Agregar listeners a los deslizadores para actualizar las UserSettings
        idMinimoLectura.addOnChangeListener { _, value, _ ->
            userSettings.readingLowLightThreshold = value.toInt().toFloat()
            cloudRepository.saveUserSettings(userSettings) { success -> }
        }

        idMinimoExterior.addOnChangeListener { _, value, _ ->
            userSettings.exteriorLowLightThreshold = value.toInt().toFloat()
            cloudRepository.saveUserSettings(userSettings) { success -> }
        }

        idAlertasVibracion.setOnCheckedChangeListener { _, isChecked ->
            userSettings.readingAlertType = when {
                isChecked && idAlertasSonoras.isChecked -> AlertType.BOTH
                isChecked -> AlertType.VIBRATION
                else -> AlertType.SOUND
            }
            cloudRepository.saveUserSettings(userSettings) { success -> }
        }

        idRestaurar.setOnClickListener {
            restoreDefaultSettings()
        }

        val guardarButton: Button = findViewById(R.id.idGuardar)
        guardarButton.setOnClickListener {
            updateUserSettingsFromUI()
            guardarPreferencias()
            cloudRepository.saveUserSettings(userSettings) { success ->
                if (success) {
                    Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al guardar configuración", Toast.LENGTH_SHORT).show()
                }
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

    private fun updateUIWithUserSettings() {
        userSettings.let { settings ->
            idMinimoLectura.value = userSettings.readingLowLightThreshold
            idMinimoExterior.value = userSettings.exteriorLowLightThreshold
            idAlertasVibracion.isChecked = userSettings.readingAlertType == AlertType.VIBRATION || userSettings.readingAlertType == AlertType.BOTH
            idAlertasSonoras.isChecked = userSettings.readingAlertType == AlertType.SOUND || userSettings.readingAlertType == AlertType.BOTH
            idVolumenAlerta.value = userSettings.alertVolume.toFloat()
            idCambiarModoAutomatico.isChecked = userSettings.autoModeChangeEnabled
            idModoPorDefecto.setText(userSettings.defaultMode, false)
        }
    }

    private fun updateUserSettingsFromUI() {
        userSettings.let { settings ->
            userSettings.readingLowLightThreshold = idMinimoLectura.value.toInt().toFloat()
            userSettings.exteriorLowLightThreshold = idMinimoExterior.value.toInt().toFloat()
            // Aquí eliminamos la referencia a txtValorLuz
            userSettings.readingAlertType = when {
                idAlertasVibracion.isChecked && idAlertasSonoras.isChecked -> AlertType.BOTH
                idAlertasVibracion.isChecked -> AlertType.VIBRATION
                idAlertasSonoras.isChecked -> AlertType.SOUND
                else -> AlertType.BOTH
            }
            userSettings.alertVolume = idVolumenAlerta.value.toInt()
            userSettings.autoModeChangeEnabled = idCambiarModoAutomatico.isChecked
            userSettings.defaultMode = idModoPorDefecto.text.toString()
        }
    }

    private fun handleUserSettingsLoadError() {
        Toast.makeText(this, "Error al obtener configuraciones del usuario", Toast.LENGTH_SHORT).show()
        initializeUIWithDefaultValues()
    }

    private fun initializeUIWithDefaultValues() {
        idMinimoLectura.value = Constants.DEFAULT_READING_LOW_LIGHT_THRESHOLD
        idMinimoExterior.value = Constants.DEFAULT_EXTERIOR_LOW_LIGHT_THRESHOLD
        idAlertasVibracion.isChecked = (userSettings.readingAlertType == AlertType.VIBRATION || userSettings.readingAlertType == AlertType.BOTH)
        idAlertasSonoras.isChecked = (userSettings.readingAlertType == AlertType.SOUND || userSettings.readingAlertType == AlertType.BOTH)
        idVolumenAlerta.value = userSettings.alertVolume.toFloat()
        idCambiarModoAutomatico.isChecked = userSettings.autoModeChangeEnabled
        idModoPorDefecto.setText(userSettings.defaultMode)
    }

    private fun restoreDefaultSettings() {
        userSettings.readingLowLightThreshold = Constants.DEFAULT_READING_LOW_LIGHT_THRESHOLD
        userSettings.exteriorLowLightThreshold = Constants.DEFAULT_EXTERIOR_LOW_LIGHT_THRESHOLD
        userSettings.readingAlertType = AlertType.BOTH
        userSettings.alertVolume = Constants.DEFAULT_ALERT_VOLUME
        userSettings.autoModeChangeEnabled = true
        userSettings.defaultMode = Constants.DEFAULT_MODE

        updateUIWithUserSettings()
        cloudRepository.saveUserSettings(userSettings) { success ->
            if (success) {
                Toast.makeText(this, "Configuración restaurada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al restaurar configuración", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarPreferencias() {
        val sharedPreferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        // Eliminar la referencia a txtValorLuz y txtEstadoLectura
        val valorLuz = sharedPreferences.getFloat("valorLuz", 0f)
        // Aquí puedes establecer el estado según el valor de luz si lo necesitas
    }

    private fun restaurarPreferencias() {
        val sharedPreferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("valorLuz", 500f) // Por ejemplo, luz adecuada
        editor.apply()
        cargarPreferencias()
    }

    private fun guardarPreferencias() {
        val sharedPreferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val valorLuz = userSettings.lightValue
        editor.putFloat("valorLuz", valorLuz)
        editor.putString("modo", userSettings.mode)
        editor.apply()
    }
}
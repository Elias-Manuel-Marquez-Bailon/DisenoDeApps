package com.example.myapplication.view

import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.model.AlertType
import com.example.myapplication.model.CloudRepository
import com.example.myapplication.model.UserSettings
import com.example.myapplication.utils.Constants
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var userSettings: UserSettings
    private lateinit var cloudRepository: CloudRepository

    // Componentes de la interfaz de usuario
    private lateinit var sliderMinimumReadingMode: Slider
    private lateinit var sliderMinimumOutdoorMode: Slider
    private lateinit var switchVibrationAlert: SwitchMaterial
    private lateinit var switchSoundAlert: SwitchMaterial
    private lateinit var sliderAlertVolume: Slider
    private lateinit var switchAutoMode: SwitchMaterial
    private lateinit var autoCompleteTextViewDefaultMode: AutoCompleteTextView
    private lateinit var buttonRestore: MaterialButton
    private lateinit var buttonSave: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        // 1. Inicialización de los componentes de la interfaz
        initializeUIComponents()
        // Obtener las configuraciones de usuario desde Firebase
        cloudRepository = CloudRepository()
        cloudRepository.getUserSettings { userSettings ->
            this.userSettings = userSettings ?: UserSettings()
            loadUserSettingsToUI()
        }
        // 2. Configuración de los listeners
        configureListeners()
    }

    private fun initializeUIComponents() {
        sliderMinimumReadingMode = findViewById(R.id.idMinimoLectura)
        sliderMinimumOutdoorMode = findViewById(R.id.idMinimoExterior)
        switchVibrationAlert = findViewById(R.id.idAlertasVibracion)
        switchSoundAlert = findViewById(R.id.idAlertasSonoras)
        sliderAlertVolume = findViewById(R.id.idVolumenAlerta)
        switchAutoMode = findViewById(R.id.idCambiarModoAutomatico)
        autoCompleteTextViewDefaultMode = findViewById(R.id.idModoPorDefecto)
        buttonRestore = findViewById(R.id.idRestaurar)
        buttonSave = findViewById(R.id.idGuardar)
    }
    private fun configureListeners(){
        // Configurar listeners para los Sliders
        sliderMinimumReadingMode.addOnChangeListener { _, value, _ ->
            userSettings.lowLightThreshold = value
        }
        sliderMinimumOutdoorMode.addOnChangeListener { _, value, _ ->
            userSettings.highLightThreshold = value
        }
        sliderAlertVolume.addOnChangeListener { _, value, _ ->
            // Ajustar el volumen de las alertas
        }
        // Configurar listeners para los Switches
        switchVibrationAlert.setOnCheckedChangeListener { _, isChecked ->
            userSettings.alertType = if (isChecked) AlertType.VIBRATION else AlertType.SOUND
        }
        switchSoundAlert.setOnCheckedChangeListener { _, isChecked ->
            userSettings.alertType = if (isChecked) AlertType.SOUND else AlertType.VIBRATION
        }
        switchAutoMode.setOnCheckedChangeListener { _, isChecked ->
            // Activar/desactivar el modo automático
        }
        // Configurar listener para el AutoCompleteTextView
        autoCompleteTextViewDefaultMode.setOnItemClickListener { _, _, position, _ ->
            // Establecer el modo predeterminado
        }
        // Configurar listeners para los botones
        buttonRestore.setOnClickListener {
            restoreDefaultSettings()
        }
        buttonSave.setOnClickListener {
            saveUserSettings()
        }

    }
    private fun loadUserSettingsToUI(){
        // 3. Lectura y actualización de los ajustes del usuario
        sliderMinimumReadingMode.value = userSettings.lowLightThreshold
        sliderMinimumOutdoorMode.value = userSettings.highLightThreshold
        switchVibrationAlert.isChecked = userSettings.alertType == AlertType.VIBRATION
        switchSoundAlert.isChecked = userSettings.alertType == AlertType.SOUND
        // Establecer otros valores en los componentes de la interfaz
    }
    private fun saveUserSettings() {
        // 4. Guardado de ajustes
        cloudRepository.uploadLightReading(userSettings.lowLightThreshold,"Lectura") { succes->
            if (succes) {
                cloudRepository.updateUserSettings(userSettings) { updateSuccess ->
                    if (updateSuccess) {
                        // 5. Notificación a otros componentes sobre los cambios
                        notifyMainActivityOfSettingsUpdate()
                    } else {
                        // Manejar el error de actualización de configuraciones
                    }
                }
            } else {
                // Manejar el error de guardado de lectura
            }
        }
    }
    private fun restoreDefaultSettings() {
        // 4. Restauración de ajustes predeterminados
        userSettings = UserSettings(
            lowLightThreshold = Constants.DEFAULT_LOW_LIGHT_THRESHOLD,
            highLightThreshold = Constants.DEFAULT_HIGH_LIGHT_THRESHOLD,
            alertType = AlertType.BOTH
        )
        loadUserSettingsToUI()
        saveUserSettings()
    }
    //Esta variable se debe tomar a consideracion en el metodo_main
    private fun notifyMainActivityOfSettingsUpdate() {
        // 5. Notificación a otros componentes sobre los cambios
        // Aquí debes implementar la lógica para notificar a MainActivity sobre los cambios en los ajustes
        // Por ejemplo, puedes enviar un evento a través de un EventBus o un LiveData
    }
    private fun validateUserSettings(): Boolean {
        // 6. Validación de los ajustes
        return userSettings.lowLightThreshold < userSettings.highLightThreshold
    }

}
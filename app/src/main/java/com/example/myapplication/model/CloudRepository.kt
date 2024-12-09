package com.example.myapplication.model

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CloudRepository {
    // Referencia a la base de datos de Firebase
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    // Referencia específica para lecturas de luz
    private val lightReadingsRef = database.child("light_readings")
    // Referencia específica para configuraciones de usuario
    private val userSettingsRef = database.child("user_settings")

    //Metodo para obtener la lista de lecturas almacenadas en firebase
    fun uploadLightReading (lightLevel: Float, mode: String, callback: (Boolean) -> Unit)  {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Crear un nuevo nodo con clave única
                val key = lightReadingsRef.push().key ?:return@launch
                // Crear un mapa con los datos a guardar
                val readingData = mapOf(
                    "timestamp" to System.currentTimeMillis(),
                    "lightLevel" to lightLevel,
                    "mode" to mode // Modo (Lectura o Exterior)
                )
                // Guardar los datos
                lightReadingsRef.child(key).setValue(readingData).await()
            } catch (e: Exception){
                throw Exception ("Error al guardar lectura: ${e.message}")
            }
        }
    }
    // Método para obtener lecturas guardadas
    fun getLightReadings(callback: (List<LightReading>) -> Unit){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = lightReadingsRef.get().await()
                val lightReadings = snapshot.children.mapNotNull {
                    it.getValue(LightReading::class.java)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    callback(emptyList())
                    throw Exception("Error al obtener lecturas: ${e.message}")
                }
            }
        }
    }

    //Metodo para actualizar la configuracion del usuario en la base de datos
    fun updateUserSettings(userSettings: UserSettings, callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userSettingsRef.setValue(userSettings).await()
                withContext(Dispatchers.Main){
                    callback(true)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    callback(false)
                    throw Exception("Error al actualizar configuraciones: ${e.message}")
                }
            }
        }
    }

    //Metodo obtener la configuracion del usuario desde firebase
    fun getUserSettings(callback: (UserSettings?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = userSettingsRef.get().await()
                val userSettings = snapshot.getValue(Map::class.java)?.toUserSettings()
                withContext(Dispatchers.Main){
                    callback(userSettings)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(null)
                    throw Exception("Error al obtener configuraciones: ${e.message}")
                }
            }
        }
    }

    private fun Map<*, *>?.toUserSettings(): UserSettings? {
        return this?.let {
            UserSettings(
                lowLightThreshold = (this["lowLightThreshold"] as? Double)?.toFloat() ?: 0f,
                highLightThreshold = (this["highLightThreshold"] as? Double)?.toFloat() ?: 0f,
                alertType = (this["alertType"] as? String)?.let {
                    AlertType.valueOf(it)
            } ?:AlertType.BOTH
            )
        }
    }

}
package com.example.myapplication.model

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class CloudRepository {
    // Referencia a la base de datos de Firebase
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    // Referencia específica para lecturas de luz
    private val lightReadingsRef = database.child("light_readings")
    // Referencia específica para configuraciones de usuario
    private val userSettingsRef = database.child("user_settings")

    //Metodo para obtener la lista de lecturas almacenadas en firebase
    suspend fun uploadLightReading (lightLevel: Float, mode: String) {
        try {
            // Crear un nuevo nodo con clave única
            val key = lightReadingsRef.push().key ?:return
            // Crear un mapa con los datos a guardar
            //Toda la linea de codigo de readingData, no la modifique
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
    // Método para obtener lecturas guardadas
    suspend fun getLightReadings() : List<LightReading> {
        return try {
            val snapshot = lightReadingsRef.get().await()
            snapshot.children.mapNotNull {
                it.getValue(LightReading::class.java)
            }
        } catch (e: Exception) {
            throw Exception ("Error al obtener lecturas: ${e.message}")
        }
    }
    //Metodo para actualizar la configuracion del usuario en la base de datos
    suspend fun updateUserSettings(userSettings: UserSettings) {
        try {
            userSettingsRef.setValue(userSettings).await()
        } catch (e: Exception) {
            throw Exception("Error al actualizar configuraciones: ${e.message}")
        }
    }
    //Metodo obtener la configuracion del usuario desde firebase
    suspend fun getUserSettings(): UserSettings? {
        return try {
            val snapshot = userSettingsRef.get().await()
            snapshot.getValue(Map::class.java)?.toUserSettings()
        } catch (e: Exception) {
            throw Exception("Error al obtener configuraciones: ${e.message}")
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
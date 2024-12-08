package com.example.myapplication.model

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class CloudRepository {
    // Referencia a la base de datos de Firebase
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    // Referencia específica para lecturas de luz
    private val lightReadingsRef = database.child("light_readings")

    // Método para guardar lecturas de luz
    suspend fun uploadLightReading (lightLevel: Float, mode: String) {
        try {
            // Crear un nuevo nodo con clave única
            val key = lightReadingsRef.push().key ?:return
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

    // Método para obtener lecturas guardadas
    suspend fun getLightReadings() : List<Map<String,Any>> {
        return try {
            val snapshot = lightReadingsRef.get().await()
            snapshot.children.map { child ->
                child.value as Map <String,Any>
            }
        } catch (e: Exception) {
            throw Exception ("Error al obtener lecturas: ${e.message}")
        }
    }

    //Necesito los siguientes metodos, si ya cuento con alguno de ellos, no es necesario que
    //los pongas

    //Metodo para subir una lectura ala base de datos
    //uploadLightReading(lightReading: LightReading)

    //Metodo para obtener la lista de lecturas almacenadas en firebase
    //getLightReadings()

    //Metodo para actualizar la configuracion del usuario en la base de datos
    //updateUserSettings(userSettings: UserSettings)

    //Metodo obtener la configuracion del usuario desde firebase
    //getUserSettings()

}
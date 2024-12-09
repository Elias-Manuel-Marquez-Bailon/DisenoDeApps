package com.example.myapplication.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.R
import com.example.myapplication.model.CloudRepository
import kotlinx.coroutines.launch

class HistoryActivity: AppCompatActivity() {
    private val cloudRepository = CloudRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        //Esto tambien va a cambiar ya que tengo ya funcionalidades en otras de mis clases
        lifecycleScope.launch {
            /*try {
                val readings = cloudRepository.getLightReadings()
                // Mostrar lecturas en un RecyclerView o ListView
                displayReadings(readings)

            } catch (e: Exception) {
                Toast.makeText(this@HistoryActivity,
                    "Error al cargar historial", Toast.LENGTH_SHORT).show()
            }*/
        }
    }

    //Este metodo podrias cambiar para listar usando una de mi clases kotlin, en ves de mapear
    private fun displayReadings(readings: List<Map<String, Any>>) {
        // Implementa la lógica para mostrar las lecturas
        // Por ejemplo, en un RecyclerView
    }

}
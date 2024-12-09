package com.example.myapplication.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.controller.LightReadingAdapter
import com.example.myapplication.model.CloudRepository
import com.example.myapplication.model.LightReading

class HistoryActivity: AppCompatActivity() {
    private val cloudRepository = CloudRepository()
    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var textViewEmpty : TextView
    private lateinit var adapter: LightReadingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Configurar la barra de herramientas
        configureToolbar()
        // Inicializar el RecyclerView
        initRecyclerView()
        // Cargar y mostrar el historial de lecturas
        loadAndDisplayHistory()
    }

    private fun configureToolbar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.historialTitulo)
    }

    private fun setSupportActionBar(toolbar: Toolbar?) {
        TODO("Not yet implemented")
    }

    private fun initRecyclerView(){
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory)
        textViewEmpty = findViewById(R.id.textViewEmpty)
        adapter = LightReadingAdapter(this) { lightReading ->
            // Maneja el clic en un elemento del historial (por ejemplo, navegar a la actividad de detalles)
            navigateToReadingDetails(lightReading)
        }
        recyclerViewHistory.adapter = adapter
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
    }

    private fun loadAndDisplayHistory (){
        cloudRepository.getLightReadings { readings ->
            if (readings.isNotEmpty()) {
                adapter.updateData(readings)
                recyclerViewHistory.visibility = View.VISIBLE
                textViewEmpty.visibility = View.GONE
            } else {
                recyclerViewHistory.visibility = View.GONE
                textViewEmpty.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateToReadingDetails(lightReading: LightReading){
        // Implementa la lógica para navegar a la actividad de detalles de la lectura
        // y pasar los datos relevantes
    }
}
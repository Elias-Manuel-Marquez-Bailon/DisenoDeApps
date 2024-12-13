package com.example.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.controller.HistoryController
import com.example.myapplication.model.LightReading
import com.example.myapplication.model.UserSettings
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Error

class HistoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var historyController: HistoryController
    private lateinit var readings: List<LightReading>
    private lateinit var userSettings: UserSettings // Añadir esta línea para obtener los ajustes del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recyclerViewHistory)
        historyController = HistoryController(this)

        // Cargar los ajustes del usuario
        userSettings = loadUserSettings() // Debes implementar este método

        // Cargar datos de Firebase
        loadDataFromFirebase()

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
            R.id.menu_ajustes -> {
                navigateToSettingsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun loadUserSettings(): UserSettings {
        return UserSettings(
            lowLightThreshold = 100f,
            highLightThreshold = 800f
        )
    }

    private fun loadDataFromFirebase() {
        // Suponiendo que tienes un Firebase referencia
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("light_readings")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange (snapshot: DataSnapshot) {
                readings = snapshot.children.mapNotNull { dataSnapshot ->
                    val lightReading = dataSnapshot.getValue(LightReading::class.java)
                    lightReading
                }
                updateRecyclerView(readings)
            }
            override fun onCancelled (error: DatabaseError){
                //Manejar error
            }
        })
    }

    private fun updateRecyclerView(readings : List<LightReading>){
        if (readings.isEmpty()) {
            findViewById<TextView>(R.id.textViewEmpty).visibility = View.VISIBLE
            recyclerView.visibility = View.GONE // Asegúrate de ocultar el RecyclerView
        } else {
            findViewById<TextView>(R.id.textViewEmpty).visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            adapter = HistoryAdapter(readings, userSettings)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }

}
